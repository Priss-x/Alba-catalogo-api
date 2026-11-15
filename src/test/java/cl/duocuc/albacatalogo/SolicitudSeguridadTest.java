package cl.duocuc.albacatalogo;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:solicitudes_seguridad", "spring.datasource.username=sa",
        "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop", "alba.solicitudes.habilitar-cuentas=true"})
class SolicitudSeguridadTest {
    private static final String RUTA = "/api/solicitudes";
    @Autowired
    private WebApplicationContext contexto;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {mvc = MockMvcBuilders.webAppContextSetup(contexto) .apply(springSecurity()).build();}

    @Test
    void cadaUsuarioSoloPuedeAccederASusSolicitudes() throws Exception {String original = """
            {"modalidad":"Voluntariado","mensaje":"Solicitud de Ana"}
            """;

        String respuesta = mvc.perform(post(RUTA)
                        .with(user("ana"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(original))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int id = JsonPath.read(respuesta, "$.id");

        mvc.perform(get(RUTA).with(user("ana")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));

        mvc.perform(get(RUTA).with(user("bruno")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mvc.perform(put(RUTA + "/{id}", id)
                        .with(user("bruno"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"modalidad":"Voluntariado","mensaje":"Cambio de Bruno"}
                    """))
                .andExpect(status().isNotFound());

        mvc.perform(delete(RUTA + "/{id}", id)
                        .with(user("bruno"))
                        .with(csrf()))
                .andExpect(status().isNotFound());

        mvc.perform(get(RUTA).with(user("ana")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].mensaje").value("Solicitud de Ana"));
    }
}