package com.example.acortador.infrastructure.web.controllers;

import com.example.acortador.domain.exception.EnlaceInvalidoException;
import com.example.acortador.domain.exception.EnlaceNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce las excepciones del dominio a respuestas HTTP (RFC 7807). */
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(EnlaceInvalidoException.class)
    ProblemDetail enlaceInvalido(EnlaceInvalidoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail peticionInvalida(MethodArgumentNotValidException e) {
        String detalle = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Petición inválida");
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalle);
    }

    @ExceptionHandler(EnlaceNoEncontradoException.class)
    ProblemDetail noEncontrado(EnlaceNoEncontradoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** La base de datos no responde (p. ej. el contenedor de MongoDB está detenido). */
    @ExceptionHandler(DataAccessResourceFailureException.class)
    ProblemDetail baseDeDatosNoDisponible(DataAccessResourceFailureException e) {
        log.error("No se pudo acceder a la base de datos: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "No se pudo conectar con la base de datos. Verifica que MongoDB esté en ejecución.");
    }
}
