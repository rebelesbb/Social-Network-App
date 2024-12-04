package com.socialnetwork.socialnetworkapp.controller;

import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;

    private SocialNetworkService service;

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void handleLogin(){

        String emailText = emailTextField.getText();
        String passwordText = passwordTextField.getText();

        Optional<User> user = service.getUserByCredentials(emailText, passwordText);
        if (user.isPresent()) {
            // Get the current stage (window) and set the new scene
            try {
                showUserProfile(user.get());
            }catch (IOException e){
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Log In Error!",
                "Email or password is not correct!");

    }

    private void showUserProfile(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/user-profile-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) emailTextField.getScene().getWindow();
        stage.setScene(scene);

        MenuBarController menuBarController = new MenuBarController();
        menuBarController.setStageServiceUser(stage, user, service);

        UserProfileController controller = loader.getController();
        controller.setService(service, user, menuBarController);

        stage.show();
    }

}
