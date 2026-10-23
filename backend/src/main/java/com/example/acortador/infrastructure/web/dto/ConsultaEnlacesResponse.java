package com.example.acortador.infrastructure.web.dto;

import java.util.List;

/**
 * @param fuente        "MONGODB" (1.ª consulta / caché vencida) o "REDIS" (caché vigente)
 * @param ttlRestanteMs milisegundos que le quedan a la caché; 0 si la fuente fue MongoDB
 * @param ttlCacheMs    duración total configurada de la caché (para la barra del contador)
 */
public record ConsultaEnlacesResponse(List<EnlaceResponse> enlaces, String fuente, boolean desdeCache,
                                      long ttlRestanteMs, long ttlCacheMs) {
}
