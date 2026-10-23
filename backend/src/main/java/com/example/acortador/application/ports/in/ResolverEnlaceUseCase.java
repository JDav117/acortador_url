package com.example.acortador.application.ports.in;

/** Puerto de entrada: obtener el enlace original a partir del código corto (para redireccionar). */
public interface ResolverEnlaceUseCase {

    String resolver(String codigo);
}
