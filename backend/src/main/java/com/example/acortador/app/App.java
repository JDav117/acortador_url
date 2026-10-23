package com.example.acortador.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de arranque. Spring escanea todo el paquete base para encontrar
 * la configuración de infraestructura que conecta los puertos con sus adaptadores.
 */
@SpringBootApplication(scanBasePackages = "com.example.acortador")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
