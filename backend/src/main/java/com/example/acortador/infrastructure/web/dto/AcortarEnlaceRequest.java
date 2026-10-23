package com.example.acortador.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AcortarEnlaceRequest(@NotBlank(message = "El enlace no puede estar vacío") String enlaceOriginal) {
}
