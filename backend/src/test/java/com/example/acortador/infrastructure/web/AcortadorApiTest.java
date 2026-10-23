package com.example.acortador.infrastructure.web;

import com.example.acortador.app.App;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Arranca la aplicación completa con el perfil "memoria" (fábrica en memoria) y prueba la API HTTP. */
@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@ActiveProfiles("memoria")
class AcortadorApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Test
    void flujoCompleto() throws Exception {
        String respuesta = mvc.perform(post("/api/enlaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enlaceOriginal\":\"https://www.youtube.com/?gl=CO&hl=es-419\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(org.hamcrest.Matchers.matchesPattern("[0-9A-Za-z]{8}")))
                .andReturn().getResponse().getContentAsString();
        JsonNode creado = json.readTree(respuesta);
        String codigo = creado.get("codigo").asText();

        mvc.perform(get("/api/enlaces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fuente").value("MONGODB"))
                .andExpect(jsonPath("$.desdeCache").value(false))
                .andExpect(jsonPath("$.enlaces[0].enlaceAcortado").value("http://localhost:8080/" + codigo));

        mvc.perform(get("/api/enlaces"))
                .andExpect(jsonPath("$.fuente").value("REDIS"))
                .andExpect(jsonPath("$.desdeCache").value(true))
                .andExpect(jsonPath("$.ttlRestanteMs").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.ttlCacheMs").value(30000));

        mvc.perform(get("/" + codigo))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.youtube.com/?gl=CO&hl=es-419"));
    }

    @Test
    void erroresSeTraducenAHttp() throws Exception {
        mvc.perform(post("/api/enlaces").contentType(MediaType.APPLICATION_JSON).content("{\"enlaceOriginal\":\"\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/enlaces").contentType(MediaType.APPLICATION_JSON).content("{\"enlaceOriginal\":\"nada\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/ZZZZZZZZ"))
                .andExpect(status().isNotFound());
    }
}
