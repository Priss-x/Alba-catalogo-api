package cl.duocuc.albacatalogo.repository;

import cl.duocuc.albacatalogo.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    @Modifying
    @Query("""
        update Solicitud s
        set s.propietario = :nuevo
        where s.propietario = :anterior
        """)
    int actualizarPropietario(@Param("anterior") String anterior, @Param("nuevo") String nuevo);
    List<Solicitud> findAllByPropietario(String propietario);
    Optional<Solicitud> findByIdAndPropietario(Long id, String propietario);
}
