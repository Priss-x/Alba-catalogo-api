package cl.duocuc.albacatalogo.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SeguridadConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Value("${alba.solicitudes.habilitar-cuentas:false}") boolean habilitarCuentas
    ) throws Exception {
        String rutaInvitado = "/api/solicitudes/invitado";
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(request ->
                        "POST".equals(request.getMethod()) && (request.getContextPath() + rutaInvitado).equals(request.getRequestURI())))
                .authorizeHttpRequests(auth -> {
                    auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/productos", "/api/programas").permitAll();
                    auth.requestMatchers(HttpMethod.POST, rutaInvitado).permitAll();

                    var solicitudesConCuenta= auth.requestMatchers("/api/solicitudes", "/api/solicitudes/**");
                    if (habilitarCuentas) {solicitudesConCuenta.authenticated();
                    } else {
                        solicitudesConCuenta.denyAll();
                    }
                    auth.anyRequest().denyAll();
                })
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}