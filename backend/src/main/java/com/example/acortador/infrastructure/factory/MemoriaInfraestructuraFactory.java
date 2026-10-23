package com.example.acortador.infrastructure.factory;

import com.example.acortador.application.ports.out.EnlaceCache;
import com.example.acortador.application.ports.out.EnlaceRepository;
import com.example.acortador.application.ports.out.InfraestructuraFactory;
import com.example.acortador.infrastructure.cache.memoria.EnlaceCacheMemoria;
import com.example.acortador.infrastructure.persistence.memoria.EnlaceRepositoryMemoria;

import java.time.Clock;
import java.time.Duration;

/** Fábrica concreta: familia "en memoria" → sin MongoDB ni Redis (pruebas / demo sin Docker). */
public class MemoriaInfraestructuraFactory implements InfraestructuraFactory {

    private final Clock reloj;
    private final Duration ttlListado;
    private final Duration ttlDestino;

    public MemoriaInfraestructuraFactory(Clock reloj, Duration ttlListado, Duration ttlDestino) {
        this.reloj = reloj;
        this.ttlListado = ttlListado;
        this.ttlDestino = ttlDestino;
    }

    @Override
    public EnlaceRepository crearRepositorio() {
        return new EnlaceRepositoryMemoria();
    }

    @Override
    public EnlaceCache crearCache() {
        return new EnlaceCacheMemoria(reloj, ttlListado, ttlDestino);
    }
}
