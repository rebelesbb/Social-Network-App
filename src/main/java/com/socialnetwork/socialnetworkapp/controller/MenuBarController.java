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

    public void setStageServiceUser(Stage stage, User user, SocialNetworkService service) {
        this.stage = stage;
        this.user = user;
        this.service = service;
    }

    public void goToSearchMenu() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/search-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        this.stage.setScene(scene);

        SearchController controller = fxmlLoader.getController();
        controller.setService(service, user);

        stage.show();
    }

    public void goToUserProfile() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/user-profile-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        this.stage.setScene(scene);

        UserProfileController controller = fxmlLoader.getController();
        controller.setService(service, user);

        stage.show();
    }

    public void goToRequests() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/requests-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        this.stage.setScene(scene);

        RequestsController controller = fxmlLoader.getController();
        controller.setService(service, user);

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

}
