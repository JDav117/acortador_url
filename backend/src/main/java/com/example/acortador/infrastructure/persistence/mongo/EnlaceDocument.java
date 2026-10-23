package com.example.acortador.infrastructure.persistence.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/** Forma en que se guarda un enlace en la colección "enlaces" de MongoDB. */
@Document(collection = "enlaces")
public class EnlaceDocument {

    @Id
    private String id;
    private String enlaceOriginal;
    @Indexed(unique = true)
    private String enlaceAcortado;
    private Instant fechaCreacion;

    public EnlaceDocument() {
    }

    public EnlaceDocument(String id, String enlaceOriginal, String enlaceAcortado, Instant fechaCreacion) {
        this.id = id;
        this.enlaceOriginal = enlaceOriginal;
        this.enlaceAcortado = enlaceAcortado;
        this.fechaCreacion = fechaCreacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnlaceOriginal() {
        return enlaceOriginal;
    }

    public void setEnlaceOriginal(String enlaceOriginal) {
        this.enlaceOriginal = enlaceOriginal;
    }

    public String getEnlaceAcortado() {
        return enlaceAcortado;
    }

    public void setEnlaceAcortado(String enlaceAcortado) {
        this.enlaceAcortado = enlaceAcortado;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
