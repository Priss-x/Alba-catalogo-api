package cl.duocuc.albacatalogo.controller;
import cl.duocuc.albacatalogo.model.Cuenta;
import cl.duocuc.albacatalogo.repository.CuentaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.web.csrf.CsrfToken;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Locale;
import cl.duocuc.albacatalogo.repository.SolicitudRepository;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SolicitudRepository solicitudRepository;

    public CuentaController(CuentaRepository repository, PasswordEncoder passwordEncoder, SolicitudRepository solicitudRepository){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.solicitudRepository = solicitudRepository;
    }
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaRespuesta registrar(@RequestBody RegistroCuenta datos) {
        String nombre = datos.nombre() == null ? "" : datos.nombre().trim();
        String correo = datos.correo() == null ? "" : datos.correo().trim().toLowerCase(Locale.ROOT);
        String clave = datos.clave() == null ? "" : datos.clave();

        if (nombre.isEmpty() || nombre.length() > 120) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre debe tener entre 1 y 120 caracteres.");}
        if (correo.isEmpty() || correo.length() > 120 || correo.contains(" ") || !correo.contains("@") || !correo.contains(".")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingresa un correo válido de hasta 120 caracteres.");
        }
        if (clave.isBlank() || clave.length() < 6 || clave.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres y hasta 72 bytes.");
        }
        if (repository.existsByCorreo(correo)) {throw correoDuplicado();}
        Cuenta cuenta = new Cuenta(nombre, correo, passwordEncoder.encode(clave));

        try {
            cuenta = repository.saveAndFlush(cuenta);
        } catch (DataIntegrityViolationException e) {throw correoDuplicado();}
        return respuesta(cuenta);
    }
    @GetMapping("/me")
    public CuentaRespuesta miCuenta(Principal principal) {
        Cuenta cuenta = repository.findByCorreo(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return respuesta(cuenta);
    }
    private CuentaRespuesta respuesta(Cuenta cuenta) {
        return new CuentaRespuesta(cuenta.getId(), cuenta.getNombre(), cuenta.getCorreo());
    }
    private ResponseStatusException correoDuplicado() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
    }
    @GetMapping("/csrf")
    public CsrfToken obtenerCsrf(CsrfToken token){return token;}
    @PutMapping("/me")
    @Transactional
    public CuentaRespuesta actualizarPerfil(@RequestBody RegistroCuenta datos, Principal principal
    ) {Cuenta cuenta = repository.findByCorreo(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        String nombre = datos.nombre() == null ? "" : datos.nombre().trim();
        String correo = datos.correo() == null ? "" : datos.correo().trim().toLowerCase(Locale.ROOT);
        String clave = datos.clave() == null ? "" : datos.clave();

        if (nombre.isEmpty() || nombre.length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre debe tener entre 1 y 120 caracteres.");
        }
        if (correo.isEmpty() || correo.length() > 120 || correo.contains(" ") || !correo.contains("@") || !correo.contains(".")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingresa un correo válido de hasta 120 caracteres.");
        }
        if (clave.isBlank()
                || clave.length() < 6 || clave.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres.");
        }
        var otraCuenta = repository.findByCorreo(correo);

        if (otraCuenta.isPresent() && !otraCuenta.get().getId().equals(cuenta.getId())) {throw correoDuplicado();}
        String correoAnterior = cuenta.getCorreo();

        cuenta.actualizar(nombre, correo, passwordEncoder.encode(clave));

        try {
            repository.saveAndFlush(cuenta);
        } catch (DataIntegrityViolationException e) {
            throw correoDuplicado();
        }

        if (!correoAnterior.equals(correo)) {
            solicitudRepository.actualizarPropietario(correoAnterior, correo);
        }

        return respuesta(cuenta);
    }
    public record RegistroCuenta(String nombre, String correo, String clave) {}
    public record CuentaRespuesta(Long id, String nombre, String correo) {}
}