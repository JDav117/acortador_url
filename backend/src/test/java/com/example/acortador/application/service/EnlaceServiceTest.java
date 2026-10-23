package com.example.acortador.application.service;

import com.example.acortador.application.ports.in.ConsultaEnlaces;
import com.example.acortador.domain.exception.EnlaceInvalidoException;
import com.example.acortador.domain.exception.EnlaceNoEncontradoException;
import com.example.acortador.domain.model.Enlace;
import com.example.acortador.infrastructure.factory.MemoriaInfraestructuraFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Gracias a la Abstract Factory, el servicio se prueba con la familia en memoria, sin Docker. */
class EnlaceServiceTest {

    private RelojManual reloj;
    private EnlaceService servicio;

    @BeforeEach
    void setUp() {
        reloj = new RelojManual(Instant.parse("2026-10-22T12:00:00Z"));
        servicio = new EnlaceService(
                new MemoriaInfraestructuraFactory(reloj, Duration.ofSeconds(30), Duration.ofMinutes(10)), reloj);
    }

    @Test
    void acortaConUnCodigoDeOchoCaracteres() {
        Enlace enlace = servicio.acortar("https://www.youtube.com/?gl=CO&hl=es-419");

        assertThat(enlace.id()).isNotNull();
        assertThat(enlace.codigo()).matches("[0-9A-Za-z]{8}");
        assertThat(enlace.enlaceOriginal()).isEqualTo("https://www.youtube.com/?gl=CO&hl=es-419");
    }

    @Test
    void agregaHttpsSiFaltaElEsquema() {
        assertThat(servicio.acortar("meet.google.com/oap-upko-ibm").enlaceOriginal())
                .isEqualTo("https://meet.google.com/oap-upko-ibm");
    }

    @Test
    void rechazaEnlacesInvalidos() {
        assertThatThrownBy(() -> servicio.acortar("   ")).isInstanceOf(EnlaceInvalidoException.class);
        assertThatThrownBy(() -> servicio.acortar("asdfsadfhkjl")).isInstanceOf(EnlaceInvalidoException.class);
    }

    @Test
    void laPrimeraConsultaVaALaBaseDeDatosYLaSegundaALaCache() {
        servicio.acortar("https://example.com");

        ConsultaEnlaces primera = servicio.consultar();
        reloj.avanzar(Duration.ofSeconds(4));
        ConsultaEnlaces segunda = servicio.consultar();

        assertThat(primera.desdeCache()).isFalse();
        assertThat(primera.ttlRestante()).isZero();
        assertThat(segunda.desdeCache()).isTrue();
        assertThat(segunda.ttlRestante()).isEqualTo(Duration.ofSeconds(26));
        assertThat(segunda.enlaces()).isEqualTo(primera.enlaces());
    }

    @Test
    void laCacheExpiraALos30Segundos() {
        servicio.consultar();
        reloj.avanzar(Duration.ofSeconds(30));

        assertThat(servicio.consultar().desdeCache()).isFalse();
    }

    @Test
    void acortarInvalidaLaCacheDelHistorial() {
        servicio.consultar();
        servicio.acortar("https://example.com");

        ConsultaEnlaces consulta = servicio.consultar();

        assertThat(consulta.desdeCache()).isFalse();
        assertThat(consulta.enlaces()).hasSize(1);
    }

    @Test
    void resuelveElEnlaceOriginal() {
        Enlace enlace = servicio.acortar("https://example.com/ruta");

        assertThat(servicio.resolver(enlace.codigo())).isEqualTo("https://example.com/ruta");
        assertThatThrownBy(() -> servicio.resolver("NOEXISTE1")).isInstanceOf(EnlaceNoEncontradoException.class);
        assertThatThrownBy(() -> servicio.resolver("corto")).isInstanceOf(EnlaceNoEncontradoException.class);
    }

    private static final class RelojManual extends Clock {

        private Instant ahora;

        RelojManual(Instant ahora) {
            this.ahora = ahora;
        }

        void avanzar(Duration duracion) {
            ahora = ahora.plus(duracion);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return ahora;
        }
    }
}
