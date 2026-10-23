package com.example.acortador.infrastructure.cache.memoria;

import com.example.acortador.application.ports.out.EnlaceCache;
import com.example.acortador.domain.model.Enlace;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Adaptador de salida: caché en memoria con expiración, equivalente funcional de Redis. */
public class EnlaceCacheMemoria implements EnlaceCache {

    private final Clock reloj;
    private final Duration ttlListado;
    private final Duration ttlDestino;
    private final Map<String, Entrada<String>> destinos = new ConcurrentHashMap<>();
    private volatile Entrada<List<Enlace>> listado;

    public EnlaceCacheMemoria(Clock reloj, Duration ttlListado, Duration ttlDestino) {
        this.reloj = reloj;
        this.ttlListado = ttlListado;
        this.ttlDestino = ttlDestino;
    }

    @Override
    public Optional<ListadoCacheado> obtenerListado() {
        Entrada<List<Enlace>> actual = listado;
        if (actual == null) {
            return Optional.empty();
        }
        Duration restante = Duration.between(reloj.instant(), actual.expiraEn());
        if (restante.isNegative() || restante.isZero()) {
            listado = null;
            return Optional.empty();
        }
        return Optional.of(new ListadoCacheado(actual.valor(), restante));
    }

    @Override
    public void guardarListado(List<Enlace> enlaces) {
        listado = new Entrada<>(List.copyOf(enlaces), reloj.instant().plus(ttlListado));
    }

    @Override
    public void invalidarListado() {
        listado = null;
    }

    @Override
    public Optional<String> obtenerDestino(String codigo) {
        Entrada<String> entrada = destinos.get(codigo);
        if (entrada == null || !entrada.expiraEn().isAfter(reloj.instant())) {
            destinos.remove(codigo);
            return Optional.empty();
        }
        return Optional.of(entrada.valor());
    }

    @Override
    public void guardarDestino(String codigo, String enlaceOriginal) {
        destinos.put(codigo, new Entrada<>(enlaceOriginal, reloj.instant().plus(ttlDestino)));
    }

    private record Entrada<T>(T valor, Instant expiraEn) {
    }
}
