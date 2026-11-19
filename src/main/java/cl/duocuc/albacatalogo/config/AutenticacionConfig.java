package cl.duocuc.albacatalogo.config;

import cl.duocuc.albacatalogo.repository.CuentaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Locale;

@Configuration
public class AutenticacionConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {return PasswordEncoderFactories.createDelegatingPasswordEncoder();}

    @Bean
    public UserDetailsService userDetailsService(CuentaRepository repository) {
        return correoIngresado -> {String correo = correoIngresado.trim().toLowerCase(Locale.ROOT);
            var cuenta = repository.findByCorreo(correo) .orElseThrow(()-> new UsernameNotFoundException("Credenciales incorrectas."));
            return User.withUsername(cuenta.getCorreo())
                    .password(cuenta.getClaveHash())
                    .roles("USUARIO")
                    .build();
        };
    }
}
