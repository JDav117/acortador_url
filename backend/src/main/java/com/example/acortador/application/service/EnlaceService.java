package com.example.acortador.application.service;

import com.example.acortador.application.ports.in.AcortarEnlaceUseCase;
import com.example.acortador.application.ports.in.ConsultaEnlaces;
import com.example.acortador.application.ports.in.ConsultarEnlacesUseCase;
import com.example.acortador.application.ports.in.ResolverEnlaceUseCase;
import com.example.acortador.application.ports.out.EnlaceCache;
import com.example.acortador.application.ports.out.EnlaceRepository;
import com.example.acortador.application.ports.out.InfraestructuraFactory;
import com.example.acortador.domain.exception.EnlaceNoEncontradoException;
import com.example.acortador.domain.model.CodigoCorto;
import com.example.acortador.domain.model.Enlace;
import com.example.acortador.domain.model.EnlaceOriginal;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

/**
 * Lógica de negocio. Es el cliente de la Abstract Factory: pide a la fábrica sus productos
 * (repositorio y caché) y trabaja solo contra los contratos, sin saber si detrás hay
 * MongoDB/Redis o memoria. No tiene anotaciones de Spring; se registra en la configuración.
 */
public class EnlaceService implements AcortarEnlaceUseCase, ConsultarEnlacesUseCase, ResolverEnlaceUseCase {

    static final int LONGITUD_CODIGO = 8;
    static final int MAX_INTENTOS_CODIGO = 5;

    private final EnlaceRepository repositorio;
    private final EnlaceCache cache;
    private final Clock reloj;

    public EnlaceService(InfraestructuraFactory fabrica, Clock reloj) {
        this.repositorio = fabrica.crearRepositorio();
        this.cache = fabrica.crearCache();
        this.reloj = reloj;
    }

    @Override
    public Enlace acortar(String enlaceOriginal) {
        String enlace = EnlaceOriginal.normalizar(enlaceOriginal);
        Enlace guardado = repositorio.guardar(Enlace.nuevo(enlace, generarCodigoUnico(), reloj.instant()));
        // El historial cacheado ya no está completo: la próxima consulta irá a la base de datos.
        cache.invalidarListado();
        return guardado;
    }

    @Override
    public ConsultaEnlaces consultar() {
        return cache.obtenerListado()
                .map(cacheado -> new ConsultaEnlaces(cacheado.enlaces(), true, cacheado.ttlRestante()))
                .orElseGet(() -> {
                    List<Enlace> enlaces = repositorio.listar();
                    cache.guardarListado(enlaces);
                    return new ConsultaEnlaces(enlaces, false, Duration.ZERO);
                });
    }

    @Override
    public String resolver(String codigo) {
        if (!CodigoCorto.esValido(codigo)) {
            throw new EnlaceNoEncontradoException(codigo);
        }
        return cache.obtenerDestino(codigo).orElseGet(() -> {
            Enlace enlace = repositorio.buscarPorCodigo(codigo)
                    .orElseThrow(() -> new EnlaceNoEncontradoException(codigo));
            cache.guardarDestino(codigo, enlace.enlaceOriginal());
            return enlace.enlaceOriginal();
        });
    }

    private String generarCodigoUnico() {
        for (int intento = 0; intento < MAX_INTENTOS_CODIGO; intento++) {
            String codigo = CodigoCorto.generar(LONGITUD_CODIGO);
            if (!repositorio.existeCodigo(codigo)) {
                return codigo;
            }
        }
        throw new IllegalStateException("No se pudo generar un código corto único");
    }
}
