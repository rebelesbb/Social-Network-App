package com.socialnetwork.socialnetworkapp.repository.database;

import com.socialnetwork.socialnetworkapp.domain.Friendship;
import com.socialnetwork.socialnetworkapp.domain.Tuple;
import com.socialnetwork.socialnetworkapp.domain.validators.Validator;
import com.socialnetwork.socialnetworkapp.repository.FriendshipRepository;
import com.socialnetwork.socialnetworkapp.utils.dto.FriendshipFilterDTO;
import com.socialnetwork.socialnetworkapp.utils.paging.Page;
import com.socialnetwork.socialnetworkapp.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipPagingRepository implements FriendshipRepository {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> friendshipValidator;

    public FriendshipPagingRepository(String url, String username, String password, Validator<Friendship> friendshipValidator) {
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
                    String.format("SELECT * FROM friendships WHERE user1 = %d AND user2 = %d OR user1 = %d AND user2 = %d", id.getFirst(), id.getSecond(), id.getSecond(), id.getFirst()))){

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

    //----------------------------------PAGING------------------------------------------

    private Tuple<String, List<Object>> toSql(FriendshipFilterDTO filter) {
        if(filter == null){
            return new Tuple<>("", Collections.emptyList());
        }
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        filter.getUserId().ifPresent(userIdFilter -> {
            conditions.add("user1 = ? OR user2 = ?");
            params.add(userIdFilter);
            params.add(userIdFilter);
        });

        String sql = String.join(" AND ", conditions);
        return new Tuple<>(sql, params);
    }

    private int count(Connection connection, FriendshipFilterDTO filter) {
        String sql = "SELECT COUNT(*) AS count FROM friendships";
        Tuple<String, List<Object>> sqlFilter = toSql(filter);
        if(!sqlFilter.getFirst().isEmpty()){
            sql += " WHERE " + sqlFilter.getFirst();
        }

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            int paramIndex = 0;
            for(Object param: sqlFilter.getSecond()){
                statement.setObject(++paramIndex, param);
            }
            try(ResultSet resultSet = statement.executeQuery()){
                int totalNumberOfFriendships = 0;
                if(resultSet.next()){
                    totalNumberOfFriendships = resultSet.getInt("count");
                }
                return totalNumberOfFriendships;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private List<Friendship> findAllOnPage(Connection connection, Pageable pageable, FriendshipFilterDTO filter) {
        List<Friendship> friendshipsOnPage = new ArrayList<>();
        String sql = "SELECT * FROM friendships";
        Tuple<String, List<Object>> sqlFilter = toSql(filter);

        if(!sqlFilter.getFirst().isEmpty()){
            sql += " WHERE " + sqlFilter.getFirst();
        }
        sql += " LIMIT ? OFFSET ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            int paramIndex = 0;
            for(Object param: sqlFilter.getSecond()){
                statement.setObject(++paramIndex, param);
            }
            statement.setInt(++paramIndex, pageable.getPageSize());
            statement.setInt(++paramIndex, pageable.getPageSize() * pageable.getPageNumber());
            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    Long user1_id = resultSet.getLong("user1");
                    Long user2_id = resultSet.getLong("user2");
                    LocalDateTime dateTime = resultSet.getTimestamp("friendship_date").toLocalDateTime();
                    Friendship friendship = new Friendship(dateTime);
                    friendship.setId(new Tuple<>(user1_id, user2_id));
                    friendshipsOnPage.add(friendship);
                }

                return friendshipsOnPage;
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable, FriendshipFilterDTO filter) {
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            int totalNumberOfFriendships = count(connection, filter);
            List<Friendship> friendshipsOnPage;
            if(totalNumberOfFriendships > 0){
                friendshipsOnPage = findAllOnPage(connection, pageable, filter);
            }
            else {
                friendshipsOnPage = new ArrayList<>();
            }
            return new Page<>(friendshipsOnPage, totalNumberOfFriendships);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        return null;
    }
}
