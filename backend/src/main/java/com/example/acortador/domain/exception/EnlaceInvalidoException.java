package com.example.acortador.domain.exception;

public class EnlaceInvalidoException extends RuntimeException {

    public EnlaceInvalidoException(String mensaje) {
        super(mensaje);
    }
}
