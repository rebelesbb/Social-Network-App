package com.socialnetwork.socialnetworkapp;

import com.socialnetwork.socialnetworkapp.domain.Friendship;
import com.socialnetwork.socialnetworkapp.domain.Tuple;
import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.domain.validators.FriendshipValidator;
import com.socialnetwork.socialnetworkapp.domain.validators.UserValidator;
import com.socialnetwork.socialnetworkapp.repository.DataManagerStructure;
import com.socialnetwork.socialnetworkapp.repository.Repository;
import com.socialnetwork.socialnetworkapp.repository.database.FriendshipDatabaseRepository;
import com.socialnetwork.socialnetworkapp.repository.database.UserDatabaseRepository;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    DataManagerStructure dataManager;
    Repository<Long, User> userRepository;
    Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    SocialNetworkService service;

    @Override
    public void start(Stage primaryStage) throws IOException {
        String username = "socialNetworkAppUsername";
        String password = "socialNetworkAppPassword";
        String url = "jdbc:postgresql://localhost:5432/SocialNetworkDatabase";

        userRepository = new UserDatabaseRepository(url, username, password, new UserValidator());
        friendshipRepository = new FriendshipDatabaseRepository(url, username, password, new FriendshipValidator());

        dataManager = new DataManagerStructure(userRepository, friendshipRepository);

        service = new SocialNetworkService(dataManager);

        initView(primaryStage);
        primaryStage.setTitle("Social Network App");
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/login-view.fxml"));
        AnchorPane layout = fxmlLoader.load();
        primaryStage.setScene(new Scene(layout));

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
