package com.socialnetwork.socialnetworkapp.controller;

import com.socialnetwork.socialnetworkapp.domain.Message;
import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import com.socialnetwork.socialnetworkapp.utils.events.ObjectChangeEvent;
import com.socialnetwork.socialnetworkapp.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

public class ChatController implements Observer<ObjectChangeEvent> {
    MenuBarController menuBarController;
    SocialNetworkService service;
    User user;
    User selectedFriend;
    ObservableList<Message> messagesList = FXCollections.observableArrayList();
    ObservableList<User> friendsOfUser = FXCollections.observableArrayList();

    @FXML
    TextArea chatArea;

    @FXML
    ListView<Message> messagesListView;

    @FXML
    ListView<User> friendsListView;

    @FXML
    Button sendButton;

    @FXML
    TextField searchTextField;

    public void setService(SocialNetworkService service, User user, MenuBarController menuBarController) {
        this.service = service;
        this.user = user;
        this.menuBarController = menuBarController;
        this.service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        friendsListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getFirstName() + " " + item.getLastName());
                } else {
                    setText(null);
                }
            }
        });

        friendsListView.setOnMouseClicked(_ -> {
            selectedFriend = friendsListView.getSelectionModel().getSelectedItem();
            if(selectedFriend != null) {
                openChatOfFriend();
            }
            else {
                messagesList.clear();
            }
        });

        friendsListView.setItems(friendsOfUser);

        messagesListView.setCellFactory(_ -> new ListCell<>() {
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);
                } else {
                    String senderName;
                    if (message.getSentFrom().equals(user.getId()))
                        senderName = "You";
                    else senderName = selectedFriend.getFirstName() + " " + selectedFriend.getLastName();

                    String formattedMessage = String.format(
                            "From: %s (%s)\n%s",
                            senderName,
                            message.getDate().toString(),
                            message.getMessage()
                    );
                    setText(formattedMessage);
                }
            }
        });

        searchTextField.textProperty().addListener((_, _, newValue) -> {
            if(newValue != null) {
                handleSearch();
            }
            else {
                searchTextField.setText("");
            }
        });

        messagesListView.setItems(messagesList);
    }

    private void handleSearch() {
        String searchText = searchTextField.getText();
        Iterable<User> friends = service.getFriendsOfUser(user);
        List<User> friendsList = StreamSupport.stream(friends.spliterator(), false)
                .filter(u -> u.getFirstName().startsWith(searchText) || u.getLastName().startsWith(searchText))
                .toList();
        friendsOfUser.setAll(friendsList);
    }

    private void openChatOfFriend() {
        List<Message> messagesWithFriend = service.getMessagesOfUsers(user.getId(), selectedFriend.getId())
                .stream()
                .sorted((m1, m2) -> {
                    if(m1.getDate().isBefore(m2.getDate()))
                        return -1;
                    return 1;
                })
                .toList();
        System.out.println(messagesWithFriend);
        messagesList.clear();
        messagesList.addAll(messagesWithFriend);
    }

    @FXML
    private void handleSend() {
        String message = chatArea.getText();
        service.addMessage(user.getId(), selectedFriend.getId(), LocalDateTime.now(), message);
        chatArea.clear();
    }

    public void initModel(){
        Iterable<User> friends = service.getFriendsOfUser(user);
        List<User> friendsList = StreamSupport.stream(friends.spliterator(), false).toList();
        friendsOfUser.setAll(friendsList);
    }

    @Override
    public void update(ObjectChangeEvent event) {
        switch (event.getType()){
            case MESSAGE_SENT -> {
                Message message = (Message) event.getData();
                if(message.getSentTo().equals(user.getId()) && message.getSentFrom().equals(selectedFriend.getId())
                || message.getSentTo().equals(selectedFriend.getId()) && message.getSentFrom().equals(user.getId())){
                    messagesList.add(messagesList.size(), message);
                    messagesListView.scrollTo(messagesList.size() - 1);
                }
            }
        }
    }

    public void goToUserProfile() throws IOException {
        menuBarController.goToUserProfile();
    }

    public void goToRequests() throws IOException {
        menuBarController.goToRequests();
    }

    public void goToSearchMenu() throws IOException {
        menuBarController.goToSearchMenu();
    }
}
