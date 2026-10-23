package com.example.acortador.domain.exception;

public class EnlaceNoEncontradoException extends RuntimeException {

    public EnlaceNoEncontradoException(String codigo) {
        super("No existe un enlace con el código: " + codigo);
    }
}
