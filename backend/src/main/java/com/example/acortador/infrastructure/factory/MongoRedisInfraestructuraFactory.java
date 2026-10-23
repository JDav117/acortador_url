package com.example.acortador.infrastructure.factory;

import com.example.acortador.application.ports.out.EnlaceCache;
import com.example.acortador.application.ports.out.EnlaceRepository;
import com.example.acortador.application.ports.out.InfraestructuraFactory;
import com.example.acortador.infrastructure.cache.redis.EnlaceCacheRedis;
import com.example.acortador.infrastructure.persistence.mongo.EnlaceRepositoryMongo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/** Fábrica concreta: familia "real" → persistencia en MongoDB + caché en Redis. */
public class MongoRedisInfraestructuraFactory implements InfraestructuraFactory {

    private final MongoTemplate mongo;
    private final StringRedisTemplate redis;
    private final ObjectMapper json;
    private final Duration ttlListado;
    private final Duration ttlDestino;

    public MongoRedisInfraestructuraFactory(MongoTemplate mongo, StringRedisTemplate redis, ObjectMapper json,
                                            Duration ttlListado, Duration ttlDestino) {
        this.mongo = mongo;
        this.redis = redis;
        this.json = json;
        this.ttlListado = ttlListado;
        this.ttlDestino = ttlDestino;
    }

    @Override
    public EnlaceRepository crearRepositorio() {
        return new EnlaceRepositoryMongo(mongo);
    }

    @Override
    public EnlaceCache crearCache() {
        return new EnlaceCacheRedis(redis, json, ttlListado, ttlDestino);
    }
}
