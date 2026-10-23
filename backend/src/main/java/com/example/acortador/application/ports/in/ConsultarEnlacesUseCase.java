package com.example.acortador.application.ports.in;

/** Puerto de entrada: historial de enlaces (desde Redis si la caché está vigente, si no desde MongoDB). */
public interface ConsultarEnlacesUseCase {

    ConsultaEnlaces consultar();
}
