package cl.duocuc.albacatalogo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/programas")
public class ProgramaController {

    @GetMapping
    public List<Programa> listarProgramas() {
        return List.of(
                new Programa(
                        "taller-emprendimiento",
                        "Taller de emprendimiento",
                        "Fortalece habilidades sociales, emocionales y laborales. Incluye el diseño y la autonomía y el aprendizaje colaborativo."),
                new Programa(
                        "intermediacion-laboral",
                        "Intermediación laboral",
                        "Apoya la inclusión laboral de personas con discapacidad múltiple, facilitando oportunidades para desarrollar sus habilidades en el trabajo."),
                new Programa(
                        "asesorias-capacitaciones",
                        "Asesorías y capacitaciones",
                        "Acerca la inclusión a empresas y organizaciones mediante asesorías, formación y experiencias de trabajo colaborativo con participantes de Alba Lab."
                )
        );
    }
    public record Programa(String id, String nombre, String descripcion) {}
}