package com.socialnetwork.socialnetworkapp.domain.validators;

import com.socialnetwork.socialnetworkapp.domain.Friendship;

import java.time.LocalDateTime;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getId() == null)
            throw new ValidationException("Friendship id is required");

        if(entity.getDate() == null)
            throw new ValidationException("Friendship date is required");

        if(LocalDateTime.now().isBefore(entity.getDate()))
            throw new ValidationException("Friendship date can't be in the future");

        if(entity.getId().getFirst() == null || entity.getId().getSecond() == null)
            throw new ValidationException("The id of both of the two users is required");

        if(entity.getId().getFirst().equals(entity.getId().getSecond()))
            throw new ValidationException("The id of both of the two users is the same");
    }
}
