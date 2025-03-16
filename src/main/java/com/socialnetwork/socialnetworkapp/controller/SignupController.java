package com.socialnetwork.socialnetworkapp.controller;

import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import com.socialnetwork.socialnetworkapp.utils.events.ObjectChangeEvent;
import com.socialnetwork.socialnetworkapp.utils.observer.Observable;
import com.socialnetwork.socialnetworkapp.utils.observer.Observer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class SignupController{
    private SocialNetworkService service;

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;

    public void handleSignup()
    {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String password = service.hashPassword(passwordTextField.getText());

        try{
            service.addUser(firstName, lastName, email, password);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Account created successfully!", "Try to log in");
            backToLogin();
        }catch (RuntimeException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void backToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/login-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) emailTextField.getScene().getWindow();
        stage.setScene(scene);

        LoginController controller = loader.getController();
        controller.setService(service);

        stage.show();
    }

    public void setService(SocialNetworkService service)
    {
        this.service = service;
    }
}
