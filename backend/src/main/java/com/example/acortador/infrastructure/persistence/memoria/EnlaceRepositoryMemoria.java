package com.example.acortador.infrastructure.persistence.memoria;

import com.example.acortador.application.ports.out.EnlaceRepository;
import com.example.acortador.domain.model.Enlace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Adaptador de salida en memoria (pruebas / demo). Los datos se pierden al reiniciar. */
public class EnlaceRepositoryMemoria implements EnlaceRepository {

    private final Map<String, Enlace> porCodigo = new LinkedHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public synchronized Enlace guardar(Enlace enlace) {
        Enlace guardado = new Enlace(String.valueOf(secuencia.incrementAndGet()),
                enlace.enlaceOriginal(), enlace.codigo(), enlace.fechaCreacion());
        porCodigo.put(guardado.codigo(), guardado);
        return guardado;
    }

    @Override
    public synchronized List<Enlace> listar() {
        return new ArrayList<>(porCodigo.values());
    }

    @Override
    public synchronized Optional<Enlace> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(porCodigo.get(codigo));
    }

    @Override
    public synchronized boolean existeCodigo(String codigo) {
        return porCodigo.containsKey(codigo);
    }
}
