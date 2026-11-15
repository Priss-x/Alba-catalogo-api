package cl.duocuc.albacatalogo.controller;

import cl.duocuc.albacatalogo.model.Solicitud;
import cl.duocuc.albacatalogo.repository.SolicitudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    private final SolicitudRepository repository;
    public SolicitudController(SolicitudRepository repository) {this.repository = repository;}

    @GetMapping
    public List<Solicitud> listar(Principal usuario) {return repository.findAllByPropietario(usuario.getName());}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Solicitud crear(@RequestBody CrearSolicitud datos, Principal usuario) {CrearSolicitud validada = validar(datos);
        return repository.save(new Solicitud(validada.modalidad(), validada.mensaje(), usuario.getName()));
    }

    @PostMapping("/invitado")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearComoInvitado(@RequestBody CrearSolicitud datos) {CrearSolicitud validada= validar(datos);
        repository.save(new Solicitud(validada.modalidad(), validada.mensaje()));
    }
    @PutMapping("/{id}")
    public Solicitud editar(@PathVariable("id") Long id, @RequestBody CrearSolicitud datos, Principal usuario){
        Solicitud solicitud = repository.findByIdAndPropietario(id, usuario.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la solicitud."));

        CrearSolicitud validada = validar(datos);
        solicitud.actualizar(validada.modalidad(), validada.mensaje());
        return repository.save(solicitud);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") Long id, Principal usuario){
        Solicitud solicitud = repository.findByIdAndPropietario(id, usuario.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la solicitud."));
        repository.delete(solicitud);
    }

    private CrearSolicitud validar(CrearSolicitud datos){
        String modalidad = datos.modalidad() == null ? "" : datos.modalidad().trim();
        String mensaje = datos.mensaje() == null ? "" : datos.mensaje().trim();

        if (modalidad.isEmpty() || modalidad.length() > 80) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La modalidad debe tener entre 1 y 80 caracteres.");}

        if (mensaje.isEmpty() || mensaje.length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mensaje debe tener entre 1 y 1000 caracteres.");}
        return new CrearSolicitud(modalidad, mensaje);
    }
    public record CrearSolicitud(String modalidad, String mensaje) {}
}