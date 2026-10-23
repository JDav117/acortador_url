package com.example.acortador.domain.model;

import com.example.acortador.domain.exception.EnlaceInvalidoException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Valida y normaliza el enlace que ingresa el usuario.
 * Acepta cualquier esquema (http, https, ftp, ...); si no trae esquema se asume https.
 */
public final class EnlaceOriginal {

    public static final int LONGITUD_MAXIMA = 2048;

    private EnlaceOriginal() {
    }

    public static String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new EnlaceInvalidoException("El enlace no puede estar vacío");
        }
        String enlace = valor.strip();
        if (enlace.length() > LONGITUD_MAXIMA) {
            throw new EnlaceInvalidoException("El enlace supera los " + LONGITUD_MAXIMA + " caracteres");
        }
        if (!enlace.contains("://")) {
            enlace = "https://" + enlace;
        }
        try {
            URI uri = new URI(enlace);
            String host = uri.getHost();
            if (host == null || !(host.contains(".") || host.equalsIgnoreCase("localhost"))) {
                throw new EnlaceInvalidoException("El enlace no tiene un dominio válido: " + valor);
            }
            return enlace;
        } catch (URISyntaxException e) {
            throw new EnlaceInvalidoException("El enlace no tiene un formato válido: " + valor);
        }
    }
}
