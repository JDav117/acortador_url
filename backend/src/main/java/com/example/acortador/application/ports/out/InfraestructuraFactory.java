package com.example.acortador.application.ports.out;

/**
 * ABSTRACT FACTORY (fábrica abstracta).
 *
 * <p>Declara cómo crear una <b>familia de objetos relacionados</b> —los adaptadores de salida
 * que el núcleo necesita— sin decir qué clases concretas los implementan:
 * <ul>
 *   <li>{@link EnlaceRepository}: dónde se guardan los enlaces de forma permanente.</li>
 *   <li>{@link EnlaceCache}: dónde se guardan temporalmente las consultas.</li>
 * </ul>
 *
 * <p>Las fábricas concretas viven en la capa de infraestructura y garantizan que los productos
 * sean compatibles entre sí:
 * <ul>
 *   <li>{@code MongoRedisInfraestructuraFactory}: MongoDB + Redis (ejecución real).</li>
 *   <li>{@code MemoriaInfraestructuraFactory}: mapas en memoria (pruebas / demo sin Docker).</li>
 * </ul>
 *
 * <p>El cliente de la fábrica es {@code EnlaceService}: solo conoce esta interfaz y las
 * interfaces de los productos, por lo que cambiar toda la familia de infraestructura
 * no exige tocar una sola línea del núcleo.
 */
public interface InfraestructuraFactory {

    EnlaceRepository crearRepositorio();

    EnlaceCache crearCache();
}
