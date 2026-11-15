package cl.duocuc.albacatalogo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitudes")
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String modalidad;

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Column(name= "propietario", length = 120)
    private String propietario;

    //constructor usado por jpa para leer registros de la base
    protected Solicitud() {}
    //para solicitudes sin propietario, osea invitados
    public Solicitud(String modalidad, String mensaje) {this.modalidad = modalidad;this.mensaje = mensaje;}
    //para solicitudes de usuarios autenticadoss
    public Solicitud(String modalidad, String mensaje, String propietario) {this(modalidad, mensaje);this.propietario = propietario;}

    public Long getId() {return id;}
    public String getModalidad() {return modalidad;}
    public String getMensaje() {return mensaje;}

    public void actualizar(String modalidad, String mensaje){this.modalidad=modalidad; this.mensaje= mensaje;}
}