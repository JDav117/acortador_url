package com.example.acortador.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad de dominio: un enlace original y su código corto.
 * No conoce MongoDB, Redis ni HTTP.
 */
public record Enlace(String id, String enlaceOriginal, String codigo, Instant fechaCreacion) {

    public Enlace {
        Objects.requireNonNull(enlaceOriginal, "enlaceOriginal");
        Objects.requireNonNull(codigo, "codigo");
        Objects.requireNonNull(fechaCreacion, "fechaCreacion");
    }

    /** Crea un enlace aún no persistido (sin id). */
    public static Enlace nuevo(String enlaceOriginal, String codigo, Instant fechaCreacion) {
        return new Enlace(null, enlaceOriginal, codigo, fechaCreacion);
    }
}
