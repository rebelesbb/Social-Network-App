package com.socialnetwork.socialnetworkapp.repository.database;

import com.socialnetwork.socialnetworkapp.domain.Request;
import com.socialnetwork.socialnetworkapp.domain.Status;
import com.socialnetwork.socialnetworkapp.domain.Tuple;
import com.socialnetwork.socialnetworkapp.domain.validators.RequestValidator;
import com.socialnetwork.socialnetworkapp.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class RequestDatabaseRepository implements Repository<Tuple<Long,Long>, Request> {
    private final String username;
    private final String password;
    private final String url;
    private final RequestValidator requestValidator;

    public RequestDatabaseRepository( String url, String username, String password, RequestValidator requestValidator) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.requestValidator = requestValidator;
    }

    private Request createRequestFromResultSet(ResultSet resultSet) throws SQLException {
        Long senderId = resultSet.getLong("sender_id");
        Long receiverId = resultSet.getLong("receiver_id");
        Status status = switch (resultSet.getString("status")){
            case "accepted" -> Status.ACCEPTED;
            case "declined" -> Status.DECLINED;
            case "pending" -> Status.PENDING;
            default -> null;
        };
        LocalDateTime time_sent = resultSet.getTimestamp("time_sent").toLocalDateTime();
        return new Request(senderId,receiverId,status, time_sent);
    }

    @Override
    public Optional<Request> findOne(Tuple<Long, Long> requestId) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
        ResultSet resultSet = connection.createStatement().executeQuery(String.format("SELECT * FROM requests WHERE sender_id = %d AND receiver_id = %d", requestId.getFirst(), requestId.getSecond()))){

            if(resultSet.next()) {
                Request request = createRequestFromResultSet(resultSet);
                return Optional.of(request);
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Request> findAll() {
        Set<Request> requests = new HashSet<>();

        String sql = "SELECT * FROM requests";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while(resultSet.next()){
                Long senderId = resultSet.getLong("sender_id");
                Long receiverId = resultSet.getLong("receiver_id");
                Status status = switch (resultSet.getString("status")) {
                    case "accepted" -> Status.ACCEPTED;
                    case "declined" -> Status.DECLINED;
                    case "pending" -> Status.PENDING;
                    default -> null;
                };
                LocalDateTime time_sent = resultSet.getTimestamp("time_sent").toLocalDateTime();
                Request request = new Request(senderId, receiverId, status, time_sent);
                requests.add(request);
            }

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return requests;
    }

    @Override
    public Integer size() {
        return ((Collection<Request>) findAll()).size();
    }

    @Override
    public Optional<Request> save(Request entity) {
        String sql = "INSERT INTO requests(sender_id, receiver_id, status, time_sent) VALUES(?,?,?,?)";
        requestValidator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, entity.getId().getFirst());
            statement.setLong(2, entity.getId().getSecond());
            statement.setString(3, entity.getStatus().toString());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            statement.executeUpdate();

            return Optional.empty();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
            return Optional.of(entity);
        }
    }

    @Override
    public Optional<Request> delete(Tuple<Long, Long> requestId) {
        String sql = "DELETE FROM requests WHERE sender_id = ? AND receiver_id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(sql)){

            Optional<Request> request = findOne(requestId);
            if(request.isPresent()) {
                statement.setLong(1, requestId.getFirst());
                statement.setLong(2, requestId.getSecond());
                statement.executeUpdate();
            }
            return request;
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Request> update(Request entity) {
        String sql = "UPDATE requests SET sender_id = ?, receiver_id = ?, status = ?, time_sent = ? WHERE sender_id = ? AND receiver_id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setLong(1, entity.getId().getFirst());
            statement.setLong(2, entity.getId().getSecond());
            statement.setString(3, entity.getStatus().toString());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getTimeSent()));

            statement.setLong(5, entity.getId().getFirst());
            statement.setLong(6, entity.getId().getSecond());
            int executionStatus = statement.executeUpdate();

            if(executionStatus > 0)
                return Optional.empty();
            else return Optional.of(entity);

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(entity);
    }
}
