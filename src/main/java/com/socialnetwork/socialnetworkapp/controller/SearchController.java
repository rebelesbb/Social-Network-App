package com.socialnetwork.socialnetworkapp.controller;

import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import com.socialnetwork.socialnetworkapp.utils.events.ObjectChangeEvent;
import com.socialnetwork.socialnetworkapp.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SearchController implements Observer<ObjectChangeEvent> {
    @FXML
    private TextField searchTextField;
    @FXML
    private ListView<User> foundUsersListView;
    @FXML
    private Button requestsButton;

    private SocialNetworkService service;
    private User user;

    MenuBarController menuBarController;
    ObservableList<User> foundUsersList = FXCollections.observableArrayList();

    public void setService(SocialNetworkService service, User user, MenuBarController menuBarController) {
        this.service = service;
        this.user = user;
        this.menuBarController = menuBarController;

        service.addObserver(this);

        int notificationsCount = menuBarController.getNotificationsCount();
        if(notificationsCount > 0)
            requestsButton.setText("Requests" + "(" + menuBarController.getNotificationsCount() + ")");
        else requestsButton.setText("Requests");
    }

    @FXML
    public void initialize() {

        foundUsersListView.setOnMouseClicked(mouseEvent ->{
            if(mouseEvent.getClickCount() == 2) {
                User selectedUser = foundUsersListView.getSelectionModel().getSelectedItem();
                if(selectedUser != null) {
                    try {
                        if(service.isFriendOfUser(user, selectedUser))
                            goToFriendProfile(selectedUser);
                        else goToSearchedUserProfile(selectedUser);
                    } catch (IOException e) {
                        MessageAlert.showErrorMessage(null, "Could not go to user profile. Try later.");
                    }
                }
            }
        });


        foundUsersListView.setCellFactory(_ -> new ListCell<>() {
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

        foundUsersListView.setItems(foundUsersList);

        searchTextField.textProperty().addListener((_, _, newValue) -> {
            if(newValue != null) {
                if(searchTextField.getText().isEmpty())
                    foundUsersList.clear();
                else handleSearch();
            }
            else {
                searchTextField.setText("");
                foundUsersList.clear();
            }
        });

    }

    public void handleSearch(){
        foundUsersList.clear();
        List<User> foundUsers = service.getUsersByName(searchTextField.getText());
        foundUsers.remove(user);
        foundUsersList.addAll(foundUsers);
    }

    private void goToFriendProfile(User selectedUser) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/friend-view.fxml"));
        Parent root = loader.load();

        Stage stage = menuBarController.getStage();
        stage.setScene(new Scene(root));

        FriendController controller = loader.getController();
        controller.setService(service, user, selectedUser, menuBarController);

        stage.show();
    }

    public void goToSearchedUserProfile(User foundUser) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/searched-user-profile-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = menuBarController.getStage();
        stage.setScene(scene);

        SearchedUserProfileController controller = loader.getController();
        controller.setService(service, user, foundUser, menuBarController);

        stage.show();
    }

    public void goToUserProfile() throws IOException {
        menuBarController.goToUserProfile();
    }

    public void goToRequests() throws IOException {
        menuBarController.goToRequests();
    }

    public void goToChats() throws IOException{
        menuBarController.goToChats();
    }

    @Override
    public void update(ObjectChangeEvent event) {

    }
}
