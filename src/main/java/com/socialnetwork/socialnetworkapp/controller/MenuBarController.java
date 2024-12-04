package com.socialnetwork.socialnetworkapp.controller;

import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuBarController {
    private Stage stage;
    private User user;
    private SocialNetworkService service;
    private int notificationsCount = 0;

    public void setStageServiceUser(Stage stage, User user, SocialNetworkService service) {
        this.stage = stage;
        this.user = user;
        this.service = service;
    }

    public Stage getStage() {
        return stage;
    }

    public void setNotificationsCount(int notificationsCount) {
        this.notificationsCount = notificationsCount;
    }

    public int getNotificationsCount() {
        return notificationsCount;
    }

    public void goToSearchMenu() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/search-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        this.stage.setScene(scene);

        SearchController controller = fxmlLoader.getController();
        controller.setService(service, user, this);

        stage.show();
    }

    public void goToUserProfile() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/user-profile-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        this.stage.setScene(scene);

        UserProfileController controller = fxmlLoader.getController();
        controller.setService(service, user, this);

        stage.show();
    }

    public void goToRequests() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/requests-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        this.stage.setScene(scene);

        RequestsController controller = fxmlLoader.getController();
        controller.setService(service, user, this);

        stage.show();
    }

    public void goToLogInMenu() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/login-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        this.stage.setScene(scene);

        LoginController controller = fxmlLoader.getController();
        controller.setService(service);

        stage.show();
    }

    public void goToChats() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/chat-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        this.stage.setScene(scene);

        ChatController controller = fxmlLoader.getController();
        controller.setService(service, user, this);

        stage.show();
    }

}
