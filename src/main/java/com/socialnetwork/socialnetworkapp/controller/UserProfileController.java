package com.socialnetwork.socialnetworkapp.controller;

import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import com.socialnetwork.socialnetworkapp.utils.events.ObjectChangeEvent;
import com.socialnetwork.socialnetworkapp.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserProfileController implements Observer<ObjectChangeEvent> {
    @FXML
    private Label nameTextField;
    @FXML
    private Label emailTextField;
    @FXML
    private ListView<User> friendsListView;

    private MenuBarController menuBarController;
    ObservableList<User> model = FXCollections.observableArrayList();

    private User user;
    private SocialNetworkService service;



    public void setService(SocialNetworkService service, User user) {
        this.service = service;
        this.user = user;
        service.addObserver(this);
        menuBarController = new MenuBarController();
        menuBarController.setStageServiceUser((Stage) friendsListView.getScene().getWindow(), user, service);
        initModel();
    }

    private void initModel(){
        nameTextField.setText(user.getFirstName() + " " + user.getLastName());
        emailTextField.setText(user.getEmail());

        Iterable<User> users = service.getFriendsOfUser(user);
        List<User> usersList = StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
        model.setAll(usersList);
    }

    @FXML
    public void initialize() {
        nameTextField.setText("");
        emailTextField.setText("");


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

        friendsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                User selectedItem = friendsListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    try {
                        goToFriendProfile(selectedItem);
                    } catch (IOException e) {
                        MessageAlert.showErrorMessage(null, "Can't open friend profile");
                    }
                }
            }
        });

        friendsListView.setItems(model);
    }

    public void goToFriendProfile(User selectedUser) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/friend-view.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) friendsListView.getScene().getWindow();
        stage.setScene(new Scene(root));

        FriendController controller = loader.getController();
        controller.setService(service, user, selectedUser);

        stage.show();
    }

    @Override
    public void update(ObjectChangeEvent event) {
    }

    public void goToSearchMenu() throws IOException {
        menuBarController.goToSearchMenu();
    }

    public void goToRequests() throws IOException {
        menuBarController.goToRequests();
    }

    public void handleLogout() throws IOException {
        menuBarController.goToLogInMenu();
    }

    public void handleUpdate(ActionEvent event){
        //service.updateUser();
    }
}
