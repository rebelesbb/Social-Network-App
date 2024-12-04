package com.socialnetwork.socialnetworkapp.repository.database;

import com.socialnetwork.socialnetworkapp.domain.Message;
import com.socialnetwork.socialnetworkapp.domain.validators.Validator;
import com.socialnetwork.socialnetworkapp.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDatabaseRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> messageValidator;

    public MessageDatabaseRepository(String url, String username, String password, Validator<Message> messageValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.messageValidator = messageValidator;
    }

    public Message createMessageFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long from_uid = resultSet.getLong("from_uid");
        Long to_uid = resultSet.getLong("to_uid");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
        String messageText = resultSet.getString("text");
        Long reply = resultSet.getObject("reply", Long.class);

        Message msg = new Message(from_uid, to_uid, date, messageText);
        msg.setId(id);
        msg.setReply(reply);

        return msg;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
        ResultSet resultSet = connection.createStatement().executeQuery(String.format("SELECT * FROM messages WHERE id = %d", id));){

            if(resultSet.next()) {
                return Optional.of(createMessageFromResultSet(resultSet));
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        String sql = "SELECT * FROM messages";
        Set<Message> messages = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                messages.add(createMessageFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return messages;
    }

    @Override
    public Integer size() {
        return ((Collection<Message>) findAll()).size();
    }

    @Override
    public Optional<Message> save(Message entity) {
        String sql = "INSERT INTO messages(from_uid, to_uid, date, text, reply) VALUES (?, ?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setLong(1, entity.getSentFrom());
            statement.setLong(2, entity.getSentTo());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setString(4, entity.getMessage());
            statement.setObject(5, entity.getReply(), Types.INTEGER);

            statement.executeUpdate();
            return Optional.empty();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Long id) {
        String sql = "DELETE FROM messages WHERE id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)){

            Optional<Message> message = findOne(id);
            if(message.isPresent()) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
            return message;

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        if(entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        messageValidator.validate(entity);

        String sql = "UPDATE messages SET from_uid = ?, to_uid = ?, date = ?, text = ?, reply = ? WHERE id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setLong(1, entity.getSentFrom());
            statement.setLong(2, entity.getSentTo());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setString(4, entity.getMessage());
            statement.setObject(5, entity.getReply(), Types.BIGINT);

            int executionStatus = statement.executeUpdate();

            if(executionStatus > 0)
                return Optional.empty();
            else return Optional.of(entity);

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }
}
