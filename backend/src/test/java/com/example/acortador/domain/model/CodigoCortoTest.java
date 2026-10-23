package com.example.acortador.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CodigoCortoTest {

    @Test
    void generaCodigosAlfanumericosEntre8Y10() {
        for (int longitud = 8; longitud <= 10; longitud++) {
            String codigo = CodigoCorto.generar(longitud);
            assertThat(codigo).hasSize(longitud);
            assertThat(CodigoCorto.esValido(codigo)).isTrue();
        }
    }

    @Test
    void rechazaLongitudesFueraDelRango() {
        assertThatThrownBy(() -> CodigoCorto.generar(7)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CodigoCorto.generar(11)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validaElFormato() {
        assertThat(CodigoCorto.esValido("aB3dE6gH")).isTrue();
        assertThat(CodigoCorto.esValido("abc")).isFalse();
        assertThat(CodigoCorto.esValido("abc-defgh")).isFalse();
        assertThat(CodigoCorto.esValido(null)).isFalse();
    }
}
