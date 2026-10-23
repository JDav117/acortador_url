package com.example.acortador.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Propiedades "app.*" de application.yml.
 *
 * @param baseUrl URL pública del backend con la que se arma el enlace acortado (baseUrl/codigo)
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(String baseUrl, Cache cache) {

    /**
     * @param ttlListado vida del historial cacheado ("Ver enlaces")
     * @param ttlDestino vida de cada código → enlace original cacheado (redirecciones)
     */
    public record Cache(Duration ttlListado, Duration ttlDestino) {
    }
}
