package com.socialnetwork.socialnetworkapp.domain.validators;

import com.socialnetwork.socialnetworkapp.domain.Request;

public class RequestValidator implements Validator<Request> {
    @Override
    public void validate(Request entity) throws ValidationException {
        if(entity.getId() == null)
            throw new ValidationException("Request id is required");

        if(entity.getId().getFirst() == null || entity.getId().getSecond() == null)
            throw new ValidationException("The id of both of the two users is required");

        if(entity.getId().getFirst().equals(entity.getId().getSecond()))
            throw new ValidationException("The id of both of the two users is the same");

    }
}
