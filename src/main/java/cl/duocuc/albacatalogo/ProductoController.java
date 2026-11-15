package cl.duocuc.albacatalogo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    //datos demo para las tarjetas del catalogo
    @GetMapping
    public List<Producto> listarProductos() {
        return List.of(
                new Producto(1, "Difusor de aroma 100 ml", "Aromatizador de ambiente en formato spray, en envase de 100 ml.", 7500),
                new Producto(2, "Difusor colgante", "Aromatizador de 8 ml para espacios pequeños, como el auto o el clóset.", 7500)
        );
    }
    public record Producto(int id, String nombre, String descripcion, int precio) {}
}