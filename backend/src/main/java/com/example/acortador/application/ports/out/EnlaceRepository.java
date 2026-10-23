package com.example.acortador.application.ports.out;

import com.example.acortador.domain.model.Enlace;

import java.util.List;
import java.util.Optional;

/** Puerto de salida (contrato): persistencia permanente de los enlaces. */
public interface EnlaceRepository {

    Enlace guardar(Enlace enlace);

    /** Todos los enlaces, del más antiguo al más reciente. */
    List<Enlace> listar();

    Optional<Enlace> buscarPorCodigo(String codigo);

    boolean existeCodigo(String codigo);
}
