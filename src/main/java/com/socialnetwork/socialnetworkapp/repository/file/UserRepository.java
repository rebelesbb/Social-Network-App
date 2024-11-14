package com.socialnetwork.socialnetworkapp.repository.file;

import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.domain.validators.Validator;

public class UserRepository extends AbstractFileRepo<Long, User>{
    private Long currentId;

    public UserRepository(Validator<User> validator, String filename) {
        super(validator, filename);
    }

    @Override
    public User createEntity(String line) {
        String[] userData = line.split(";");
        User u = new User(userData[1], userData[2], userData[3], userData[4]);
        u.setId(Long.parseLong(userData[0]));
        return u;
    }

    @Override
    public String saveEntity(User entity) {
        return entity.getId() + ";" +
                entity.getFirstName() + ";" +
                entity.getLastName() + ";" +
                entity.getEmail() + ";" +
                entity.getPassword();
    }
}
