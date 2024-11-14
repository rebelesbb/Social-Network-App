package com.socialnetwork.socialnetworkapp.repository.database;

import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.domain.validators.Validator;
import com.socialnetwork.socialnetworkapp.repository.Repository;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDatabaseRepository implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> userValidator;

    public UserDatabaseRepository(String url, String username, String password, Validator<User> userValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userValidator = userValidator;
    }

    private User createUserFromResultSet(ResultSet resultSet){
        try{
            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("firstname");
            String lastName = resultSet.getString("lastname");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");

            User user = new User(firstName, lastName, email, password);
            user.setId(id);

            return user;

        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Optional<User> findOne(Long id) {
        User user;
        try(Connection connection = DriverManager.getConnection(url, username, password);
            ResultSet resultSet = connection.createStatement().executeQuery(String.format("SELECT * FROM users WHERE id='%d'", id))) {

            if(resultSet.next()){
                user = createUserFromResultSet(resultSet);
                return Optional.ofNullable(user);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName, email, password);
                user.setId(id);
                users.add(user);
            }
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
        //return users.stream().sorted((u1,u2)->Long.compare(u1.getId(), u2.getId())).collect(Collectors.toList());
    }

    @Override
    public Integer size() {
        return ((Collection<User>) findAll()).size();
    }

    @Override
    public Optional<User> save(User entity) {
        String sql = "INSERT INTO users (firstname, lastname, email, password) VALUES (?,?,?,?)";
        userValidator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());

            statement.executeUpdate();
        }
        catch(SQLException e) {
            return Optional.ofNullable(entity);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)){
            Optional<User> user = findOne(id);
            if(user.isPresent()){
                statement.setLong(1, id);
                statement.executeUpdate();
            }
            return user;
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity cannot be null");
        userValidator.validate(entity);

        String sql = "UPDATE users SET firstname = ?, lastname = ?, email = ?, password = ? WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());

            statement.setLong(5, entity.getId());


            if(statement.executeUpdate() > 0)
                return Optional.empty();
            return Optional.of(entity);
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
}