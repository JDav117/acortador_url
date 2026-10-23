package com.example.acortador.infrastructure.persistence.mongo;

import com.example.acortador.application.ports.out.EnlaceRepository;
import com.example.acortador.domain.model.Enlace;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

/** Adaptador de salida: implementa el contrato {@link EnlaceRepository} usando MongoTemplate. */
public class EnlaceRepositoryMongo implements EnlaceRepository {

    private final MongoTemplate mongo;

    public EnlaceRepositoryMongo(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public Enlace guardar(Enlace enlace) {
        return aDominio(mongo.insert(aDocumento(enlace)));
    }

    @Override
    public List<Enlace> listar() {
        Query query = new Query().with(Sort.by(Sort.Direction.ASC, "fechaCreacion"));
        return mongo.find(query, EnlaceDocument.class).stream().map(this::aDominio).toList();
    }

    @Override
    public Optional<Enlace> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(mongo.findOne(porCodigo(codigo), EnlaceDocument.class)).map(this::aDominio);
    }

    @Override
    public boolean existeCodigo(String codigo) {
        return mongo.exists(porCodigo(codigo), EnlaceDocument.class);
    }

    private Query porCodigo(String codigo) {
        return Query.query(Criteria.where("enlaceAcortado").is(codigo));
    }

    private EnlaceDocument aDocumento(Enlace enlace) {
        return new EnlaceDocument(enlace.id(), enlace.enlaceOriginal(), enlace.codigo(), enlace.fechaCreacion());
    }

    private Enlace aDominio(EnlaceDocument doc) {
        return new Enlace(doc.getId(), doc.getEnlaceOriginal(), doc.getEnlaceAcortado(), doc.getFechaCreacion());
    }
}
