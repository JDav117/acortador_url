package com.example.acortador.infrastructure.web.controllers;

import com.example.acortador.application.ports.in.ResolverEnlaceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/** Adaptador de entrada HTTP: GET /{codigo} responde 302 hacia el enlace original. */
@RestController
public class RedireccionController {

    private final ResolverEnlaceUseCase resolverEnlace;

    public RedireccionController(ResolverEnlaceUseCase resolverEnlace) {
        this.resolverEnlace = resolverEnlace;
    }

    @GetMapping("/{codigo:[0-9A-Za-z]{8,10}}")
    public ResponseEntity<Void> redirigir(@PathVariable String codigo) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(resolverEnlace.resolver(codigo)))
                .build();
    }
}
