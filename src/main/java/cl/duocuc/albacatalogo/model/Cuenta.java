package cl.duocuc.albacatalogo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cuentas")
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 254)
    private String correo;

    @Column(nullable = false, length = 255)
    private String claveHash;

    protected Cuenta() {}

    public Cuenta(String nombre, String correo, String claveHash){this.nombre = nombre;
        this.correo = correo; this.claveHash = claveHash;}

    public void actualizar(String nombre, String correo, String claveHash){
        this.nombre = nombre;
        this.correo = correo;
        this.claveHash = claveHash;
    }

    public Long getId() {return id;}

    public String getNombre() {return nombre;}

    public String getCorreo() {return correo;}

    public String getClaveHash() {return claveHash;}
}