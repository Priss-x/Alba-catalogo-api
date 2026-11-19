package cl.duocuc.albacatalogo.config;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            @Value("${alba.solicitudes.habilitar-cuentas:false}")
            boolean habilitarCuentas
    ) throws Exception {
        String rutaInvitado = "/api/solicitudes/invitado";
        String rutaRegistro = "/api/cuentas/registro";

        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(request -> {
                    String ruta = request.getRequestURI();
                    String contexto = request.getContextPath();
                    return "POST".equals(request.getMethod()) && ((contexto + rutaInvitado).equals(ruta)|| (contexto + rutaRegistro).equals(ruta));
                }))
                .authorizeHttpRequests(auth -> {auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
                    auth.requestMatchers(HttpMethod.GET,"/api/productos", "/api/programas").permitAll();
                    auth.requestMatchers(HttpMethod.POST, rutaInvitado, rutaRegistro).permitAll();
                    auth.requestMatchers(HttpMethod.GET,"/api/cuentas/me", "/api/cuentas/csrf").authenticated();

                    var solicitudesConCuenta = auth.requestMatchers("/api/solicitudes","/api/solicitudes/**");
                    if (habilitarCuentas) {
                        solicitudesConCuenta.authenticated();
                    } else {
                        solicitudesConCuenta.denyAll();
                    }
                    auth.requestMatchers(HttpMethod.PUT, "/api/cuentas/me").authenticated();
                    auth.anyRequest().denyAll();
                })
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}