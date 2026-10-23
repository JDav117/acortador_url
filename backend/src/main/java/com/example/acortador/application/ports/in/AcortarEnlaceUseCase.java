package com.example.acortador.application.ports.in;

import com.example.acortador.domain.model.Enlace;

/** Puerto de entrada: acortar un enlace de cualquier tipo. */
public interface AcortarEnlaceUseCase {

    Enlace acortar(String enlaceOriginal);
}
