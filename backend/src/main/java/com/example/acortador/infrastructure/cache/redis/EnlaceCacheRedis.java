package com.example.acortador.infrastructure.cache.redis;

import com.example.acortador.application.ports.out.EnlaceCache;
import com.example.acortador.domain.model.Enlace;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Adaptador de salida: implementa {@link EnlaceCache} con Redis.
 * El historial se guarda como JSON con un TTL; el tiempo restante se lee con PTTL.
 * Si Redis no responde se registra una advertencia y se actúa como si no hubiera caché.
 */
public class EnlaceCacheRedis implements EnlaceCache {

    static final String CLAVE_LISTADO = "enlaces:listado";
    static final String PREFIJO_DESTINO = "enlaces:destino:";

    private static final Logger log = LoggerFactory.getLogger(EnlaceCacheRedis.class);
    private static final TypeReference<List<EnlaceCacheado>> TIPO_LISTADO = new TypeReference<>() {
    };

    private final StringRedisTemplate redis;
    private final ObjectMapper json;
    private final Duration ttlListado;
    private final Duration ttlDestino;

    public EnlaceCacheRedis(StringRedisTemplate redis, ObjectMapper json, Duration ttlListado, Duration ttlDestino) {
        this.redis = redis;
        this.json = json;
        this.ttlListado = ttlListado;
        this.ttlDestino = ttlDestino;
    }

    @Override
    public Optional<ListadoCacheado> obtenerListado() {
        try {
            String valor = redis.opsForValue().get(CLAVE_LISTADO);
            Long ttlMs = redis.getExpire(CLAVE_LISTADO, TimeUnit.MILLISECONDS);
            if (valor == null || ttlMs == null || ttlMs <= 0) {
                return Optional.empty();
            }
            List<Enlace> enlaces = json.readValue(valor, TIPO_LISTADO).stream()
                    .map(EnlaceCacheado::aDominio)
                    .toList();
            return Optional.of(new ListadoCacheado(enlaces, Duration.ofMillis(ttlMs)));
        } catch (Exception e) {
            log.warn("No se pudo leer el historial desde Redis: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void guardarListado(List<Enlace> enlaces) {
        try {
            List<EnlaceCacheado> valor = enlaces.stream().map(EnlaceCacheado::desdeDominio).toList();
            redis.opsForValue().set(CLAVE_LISTADO, json.writeValueAsString(valor), ttlListado);
        } catch (Exception e) {
            log.warn("No se pudo guardar el historial en Redis: {}", e.getMessage());
        }
    }

    @Override
    public void invalidarListado() {
        try {
            redis.delete(CLAVE_LISTADO);
        } catch (Exception e) {
            log.warn("No se pudo invalidar el historial en Redis: {}", e.getMessage());
        }
    }

    @Override
    public Optional<String> obtenerDestino(String codigo) {
        try {
            return Optional.ofNullable(redis.opsForValue().get(PREFIJO_DESTINO + codigo));
        } catch (Exception e) {
            log.warn("No se pudo leer el destino {} desde Redis: {}", codigo, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void guardarDestino(String codigo, String enlaceOriginal) {
        try {
            redis.opsForValue().set(PREFIJO_DESTINO + codigo, enlaceOriginal, ttlDestino);
        } catch (Exception e) {
            log.warn("No se pudo guardar el destino {} en Redis: {}", codigo, e.getMessage());
        }
    }

    /** Formato JSON propio de la caché, para no acoplar el dominio a la serialización. */
    record EnlaceCacheado(String id, String enlaceOriginal, String codigo, Instant fechaCreacion) {

        static EnlaceCacheado desdeDominio(Enlace e) {
            return new EnlaceCacheado(e.id(), e.enlaceOriginal(), e.codigo(), e.fechaCreacion());
        }

        Enlace aDominio() {
            return new Enlace(id, enlaceOriginal, codigo, fechaCreacion);
        }
    }
}
