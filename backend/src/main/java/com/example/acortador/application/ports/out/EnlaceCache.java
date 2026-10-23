package com.example.acortador.application.ports.out;

import com.example.acortador.domain.model.Enlace;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (contrato): caché temporal.
 * Una falla de la caché nunca debe romper el caso de uso; las implementaciones
 * deben comportarse como un "fallo de caché" (vacío) si el almacén no responde.
 */
public interface EnlaceCache {

    Optional<ListadoCacheado> obtenerListado();

    void guardarListado(List<Enlace> enlaces);

    void invalidarListado();

    Optional<String> obtenerDestino(String codigo);

    void guardarDestino(String codigo, String enlaceOriginal);

    record ListadoCacheado(List<Enlace> enlaces, Duration ttlRestante) {

        public ListadoCacheado {
            enlaces = List.copyOf(enlaces);
        }
    }
}
