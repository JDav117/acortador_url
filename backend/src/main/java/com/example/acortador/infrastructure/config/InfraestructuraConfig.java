package com.example.acortador.infrastructure.config;

import com.example.acortador.application.ports.out.InfraestructuraFactory;
import com.example.acortador.application.service.EnlaceService;
import com.example.acortador.infrastructure.factory.MemoriaInfraestructuraFactory;
import com.example.acortador.infrastructure.factory.MongoRedisInfraestructuraFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Clock;

/**
 * Configuración de arranque (equivale a la flecha naranja del esquema):
 * Spring elige UNA fábrica concreta según el perfil activo y se la entrega a EnlaceService.
 * Esto ocurre una sola vez al iniciar, no en cada petición.
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class InfraestructuraConfig {

    @Bean
    Clock reloj() {
        return Clock.systemUTC();
    }

    /** Perfil por defecto: MongoDB + Redis. */
    @Bean
    @Profile("!memoria")
    InfraestructuraFactory mongoRedisInfraestructuraFactory(MongoTemplate mongo, StringRedisTemplate redis,
                                                            ObjectMapper json, AppProperties props) {
        return new MongoRedisInfraestructuraFactory(mongo, redis, json,
                props.cache().ttlListado(), props.cache().ttlDestino());
    }

    /** Perfil "memoria": sin dependencias externas. */
    @Bean
    @Profile("memoria")
    InfraestructuraFactory memoriaInfraestructuraFactory(Clock reloj, AppProperties props) {
        return new MemoriaInfraestructuraFactory(reloj, props.cache().ttlListado(), props.cache().ttlDestino());
    }

    /** El servicio implementa los tres casos de uso; los controladores lo reciben por sus interfaces. */
    @Bean
    EnlaceService enlaceService(InfraestructuraFactory fabrica, Clock reloj) {
        return new EnlaceService(fabrica, reloj);
    }
}
