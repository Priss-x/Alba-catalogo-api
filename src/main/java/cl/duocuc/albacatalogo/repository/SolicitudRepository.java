package cl.duocuc.albacatalogo.repository;

import cl.duocuc.albacatalogo.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findAllByPropietario(String propietario);
    Optional<Solicitud> findByIdAndPropietario(Long id, String propietario);
}
