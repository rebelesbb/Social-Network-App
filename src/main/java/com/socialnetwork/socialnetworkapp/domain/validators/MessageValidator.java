package com.socialnetwork.socialnetworkapp.domain.validators;

import com.socialnetwork.socialnetworkapp.domain.Friendship;
import com.socialnetwork.socialnetworkapp.domain.Message;

import java.time.LocalDateTime;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getId() == null)
            throw new ValidationException("Message id is required");

        if(entity.getDate() == null)
            throw new ValidationException("Message date is required");

        if(LocalDateTime.now().isBefore(entity.getDate()))
            throw new ValidationException("Message date can't be in the future");

        if(entity.getSentFrom() == null || entity.getSentTo() == null)
            throw new ValidationException("The id of both of the two users is required");
    }
}
