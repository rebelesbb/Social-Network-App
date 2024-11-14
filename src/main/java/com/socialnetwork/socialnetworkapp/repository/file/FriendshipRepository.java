package com.socialnetwork.socialnetworkapp.repository.file;

import com.socialnetwork.socialnetworkapp.domain.Friendship;
import com.socialnetwork.socialnetworkapp.domain.Tuple;
import com.socialnetwork.socialnetworkapp.domain.validators.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FriendshipRepository extends AbstractFileRepo<Tuple<Long,Long>, Friendship> {

    public FriendshipRepository(Validator<Friendship> validator, String filename) {
        super(validator, filename);
    }

    @Override
    public Friendship createEntity(String line) {
        String[] friendshipData = line.split(";");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Friendship fr = new Friendship(LocalDateTime.parse(friendshipData[2], formatter));

        fr.setId(new Tuple<>(Long.parseLong(friendshipData[0]), Long.parseLong(friendshipData[1])));
        return fr;
    }

    @Override
    public String saveEntity(Friendship entity) {
        return entity.getId().getFirst() + ";" + entity.getId().getSecond() + ";" + entity.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
