package com.example.acortador.infrastructure.web.controllers;

import com.example.acortador.application.ports.in.AcortarEnlaceUseCase;
import com.example.acortador.application.ports.in.ConsultaEnlaces;
import com.example.acortador.application.ports.in.ConsultarEnlacesUseCase;
import com.example.acortador.domain.model.Enlace;
import com.example.acortador.infrastructure.config.AppProperties;
import com.example.acortador.infrastructure.web.dto.AcortarEnlaceRequest;
import com.example.acortador.infrastructure.web.dto.ConsultaEnlacesResponse;
import com.example.acortador.infrastructure.web.dto.EnlaceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Adaptador de entrada HTTP: recibe la petición y delega en los casos de uso. */
@RestController
@RequestMapping("/api/enlaces")
public class EnlaceController {

    private final AcortarEnlaceUseCase acortarEnlace;
    private final ConsultarEnlacesUseCase consultarEnlaces;
    private final AppProperties props;

    public EnlaceController(AcortarEnlaceUseCase acortarEnlace, ConsultarEnlacesUseCase consultarEnlaces,
                            AppProperties props) {
        this.acortarEnlace = acortarEnlace;
        this.consultarEnlaces = consultarEnlaces;
        this.props = props;
    }

    @PostMapping
    public ResponseEntity<EnlaceResponse> acortar(@Valid @RequestBody AcortarEnlaceRequest request) {
        Enlace enlace = acortarEnlace.acortar(request.enlaceOriginal());
        return ResponseEntity.status(HttpStatus.CREATED).body(aRespuesta(enlace));
    }

    @GetMapping
    public ConsultaEnlacesResponse consultar() {
        ConsultaEnlaces consulta = consultarEnlaces.consultar();
        return new ConsultaEnlacesResponse(
                consulta.enlaces().stream().map(this::aRespuesta).toList(),
                consulta.desdeCache() ? "REDIS" : "MONGODB",
                consulta.desdeCache(),
                consulta.ttlRestante().toMillis(),
                props.cache().ttlListado().toMillis());
    }

    private EnlaceResponse aRespuesta(Enlace enlace) {
        return new EnlaceResponse(enlace.id(), enlace.enlaceOriginal(), enlace.codigo(),
                props.baseUrl() + "/" + enlace.codigo(), enlace.fechaCreacion());
    }
}
