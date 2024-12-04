package com.socialnetwork.socialnetworkapp.controller;

import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import com.socialnetwork.socialnetworkapp.utils.events.ObjectChangeEvent;
import com.socialnetwork.socialnetworkapp.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendController implements Observer<ObjectChangeEvent> {
    @FXML
    private Label nameTextField;
    @FXML
    private ListView<User> friendsListView;
    @FXML
    private Button removeButton;
    @FXML
    private Button requestsButton;


    private MenuBarController menuBarController;
    ObservableList<User> model = FXCollections.observableArrayList();


    private User user;
    private User selectedUser;
    private SocialNetworkService service;

    public void setService(SocialNetworkService service, User user, User selectedUser, MenuBarController menuBarController) {
        this.service = service;
        this.user = user;
        this.selectedUser = selectedUser;
        this.menuBarController = menuBarController;

        service.addObserver(this);

        int notifsCount = menuBarController.getNotificationsCount();
        if(notifsCount > 0)
            requestsButton.setText("Requests" + "(" + menuBarController.getNotificationsCount() + ")");
        else requestsButton.setText("Requests");

        initModel();
    }

    private void initModel(){
        nameTextField.setText(selectedUser.getFirstName() + " " + selectedUser.getLastName());

        Iterable<User> users = service.getFriendsOfUser(selectedUser);
        List<User> usersList = StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
        model.setAll(usersList);
    }

    @FXML
    public void initialize() {
        nameTextField.setText("");

        friendsListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getFirstName() + " " + item.getLastName());
                }
                else {
                    setText(null);
                }
            }
        });

        friendsListView.setItems(model);
    }

    public void handleRemove(){
        service.removeFriend(user.getId(), selectedUser.getId());
        removeButton.setDisable(true);
        model.clear();
        initModel();
    }

    public void goToUserProfile() throws IOException{
        menuBarController.goToUserProfile();
    }

    public void goToSearchMenu() throws IOException{
        menuBarController.goToSearchMenu();
    }

    public void goToRequests() throws IOException{
        menuBarController.goToRequests();
    }

    public void goToChats() throws IOException{
        menuBarController.goToChats();
    }

    @Override
    public void update(ObjectChangeEvent event) {

    }
}
