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

public class UpdateProfileController {
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;

    private SocialNetworkService service;
    private User user;
    private MenuBarController menuBarController;

    public void setService(SocialNetworkService socialNetworkService, User user, MenuBarController menuBarController) {
        this.service = socialNetworkService;
        this.user = user;
        this.menuBarController = menuBarController;
    }

    public void backToProfile() throws IOException {
        Optional<User> updatedUser = service.getUserById(user.getId());
        if (updatedUser.isPresent()) {
            menuBarController.setUser(user);
            menuBarController.goToUserProfile();
        }
    }

    public void handleUpdate(){
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordField.getText();
        if(!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            try{
                service.updateUser(user.getId(), firstName, lastName, email, service.hashPassword(password));
                backToProfile();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
        else MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "All fields need to be filled!");
    }
}
