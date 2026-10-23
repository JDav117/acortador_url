package com.example.acortador.domain.model;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Reglas del código corto: alfanumérico (Base62) de 8 a 10 caracteres.
 */
public final class CodigoCorto {

    public static final int LONGITUD_MINIMA = 8;
    public static final int LONGITUD_MAXIMA = 10;

    private static final String ALFABETO =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Pattern FORMATO =
            Pattern.compile("^[0-9A-Za-z]{" + LONGITUD_MINIMA + "," + LONGITUD_MAXIMA + "}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    private CodigoCorto() {
    }

    public static String generar(int longitud) {
        if (longitud < LONGITUD_MINIMA || longitud > LONGITUD_MAXIMA) {
            throw new IllegalArgumentException(
                    "La longitud debe estar entre " + LONGITUD_MINIMA + " y " + LONGITUD_MAXIMA);
        }
        StringBuilder codigo = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            codigo.append(ALFABETO.charAt(RANDOM.nextInt(ALFABETO.length())));
        }
        return codigo.toString();
    }

    public static boolean esValido(String codigo) {
        return codigo != null && FORMATO.matcher(codigo).matches();
    }
}
