package cl.duocuc.albacatalogo.repository;

import cl.duocuc.albacatalogo.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long>{Optional<Cuenta> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}