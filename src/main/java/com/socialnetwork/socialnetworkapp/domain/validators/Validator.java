package com.socialnetwork.socialnetworkapp.domain.validators;

public interface Validator<T> {
    /**
     *
     * @param entity
     *          the entity to be validated
     * @throws ValidationException
     *          if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}
