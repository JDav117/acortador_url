package com.example.acortador.infrastructure.web.dto;

import java.time.Instant;

/**
 * @param codigo         código corto (8 caracteres)
 * @param enlaceAcortado URL completa que redirecciona: baseUrl/codigo
 */
public record EnlaceResponse(String id, String enlaceOriginal, String codigo, String enlaceAcortado,
                             Instant fechaCreacion) {
}
