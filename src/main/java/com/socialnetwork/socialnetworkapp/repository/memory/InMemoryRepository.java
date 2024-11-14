package com.socialnetwork.socialnetworkapp.repository.memory;

import com.socialnetwork.socialnetworkapp.domain.Entity;
import com.socialnetwork.socialnetworkapp.domain.validators.Validator;
import com.socialnetwork.socialnetworkapp.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private final Validator<E> validator;
    protected Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator){
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        if(id ==  null)
            throw new IllegalArgumentException("Id cannot be null!");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Integer size() {
        return entities.size();
    }

    @Override
    public Optional<E> save(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> delete(ID id) {
        if(id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");

        validator.validate(entity);

        if(entities.containsKey(entity.getId())){
            entities.replace(entity.getId(), entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}
