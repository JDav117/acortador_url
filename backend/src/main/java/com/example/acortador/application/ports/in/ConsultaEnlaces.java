package com.example.acortador.application.ports.in;

import com.example.acortador.domain.model.Enlace;

import java.time.Duration;
import java.util.List;

/**
 * Resultado de consultar el historial.
 *
 * @param desdeCache   true si los datos salieron de la caché (2.ª consulta en adelante mientras viva)
 * @param ttlRestante  tiempo que le queda a la caché; {@link Duration#ZERO} si se leyó de la base de datos
 */
public record ConsultaEnlaces(List<Enlace> enlaces, boolean desdeCache, Duration ttlRestante) {

    public ConsultaEnlaces {
        enlaces = List.copyOf(enlaces);
    }
}
