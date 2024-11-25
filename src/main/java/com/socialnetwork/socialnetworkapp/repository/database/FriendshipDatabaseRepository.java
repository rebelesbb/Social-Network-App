package com.socialnetwork.socialnetworkapp.repository.database;

import com.socialnetwork.socialnetworkapp.domain.Friendship;
import com.socialnetwork.socialnetworkapp.domain.Tuple;
import com.socialnetwork.socialnetworkapp.domain.validators.Validator;
import com.socialnetwork.socialnetworkapp.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipDatabaseRepository implements Repository<Tuple<Long, Long>, Friendship>{
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> friendshipValidator;

    public FriendshipDatabaseRepository(String url, String username, String password, Validator<Friendship> friendshipValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.friendshipValidator = friendshipValidator;
    }

    private Friendship createFriendshipFromResultSet(ResultSet resultSet){
        try{
            Tuple<Long, Long> id = new Tuple<>(resultSet.getLong("user1"), resultSet.getLong("user2"));
            LocalDateTime dateTime = resultSet.getTimestamp("friendship_date").toLocalDateTime();
            Friendship friendship = new Friendship(dateTime);
            friendship.setId(id);
            return friendship;
        }
        catch (SQLException e){
            return null;
        }
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        Friendship friendship;
        try(Connection connection = DriverManager.getConnection(url, username, password);
            ResultSet resultSet = connection.createStatement().executeQuery(
                    String.format("SELECT * FROM friendships WHERE user1 = %d AND user2 = %d", id.getFirst(), id.getSecond()))){

            if(resultSet.next()){
                friendship = createFriendshipFromResultSet(resultSet);
                return Optional.ofNullable(friendship);
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        String sql = "SELECT * FROM friendships";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {

            while(resultSet.next()) {
                Tuple<Long, Long> id = new Tuple<>(resultSet.getLong("user1"), resultSet.getLong("user2"));
                LocalDateTime dateTime = resultSet.getTimestamp("friendship_date").toLocalDateTime();
                Friendship friendship = new Friendship(dateTime);
                friendship.setId(id);

                friendships.add(friendship);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friendships;
    }

    @Override
    public Integer size() {
        return ((Collection<Friendship>) findAll()).size();
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        String sql = "INSERT INTO friendships (user1, user2, friendship_date) VALUES (?, ?, ?)";
        friendshipValidator.validate(entity);
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setLong(1, entity.getId().getFirst());
            statement.setLong(2, entity.getId().getSecond());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            statement.executeUpdate();
        }
        catch (SQLException e) {
            return Optional.ofNullable(entity);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        String sql = "DELETE FROM friendships WHERE user1 = ? AND user2 = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)){

            Optional<Friendship> friendship = findOne(id);
            if(friendship.isPresent()){
                statement.setLong(1, id.getFirst());
                statement.setLong(2, id.getSecond());
                statement.executeUpdate();
            }

            return friendship;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity cannot be null");
        friendshipValidator.validate(entity);

        String sql = "UPDATE friendships SET friendship_date = ? WHERE user1 = ? AND user2 = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setLong(2, entity.getId().getFirst());
            statement.setLong(3, entity.getId().getSecond());

            if(statement.executeUpdate() > 0){
                return Optional.empty();
            }
            return Optional.of(entity);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
