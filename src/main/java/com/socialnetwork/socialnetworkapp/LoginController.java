package com.socialnetwork.socialnetworkapp;

import com.socialnetwork.socialnetworkapp.controller.MessageAlert;
import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import com.socialnetwork.socialnetworkapp.utils.events.UserEntityChangeData;
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
import java.util.Optional;

public class LoginController implements Observer<UserEntityChangeData> {
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;

    private SocialNetworkService service;

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void handleLogin() throws IOException {

        /*
        URL url1 = getClass().getResource("home-view.fxml");
        URL url2 = getClass().getResource("/home-view.fxml");
        URL url3 = getClass().getResource("./home-view.fxml");
        URL url8 = getClass().getResource("../home-view.fxml");
        URL url4 = getClass().getResource("views/home-view.fxml");
        URL url7 = getClass().getResource("/views/home-view.fxml");
        URL url5 = getClass().getResource("./views/home-view.fxml");
        URL url6 = getClass().getResource("../views/home-view.fxml");

        Vector<URL> urls = new Vector<>();
        urls.add(url1);
        urls.add(url2);
        urls.add(url3);
        urls.add(url8);
        urls.add(url4);
        urls.add(url7);
        urls.add(url5);
        urls.add(url6);

        for(var url : urls) {
            System.out.println(url);
        }
*/

        String emailText = emailTextField.getText();
        String passwordText = passwordTextField.getText();

        Optional<User> user = service.getUserByCredentials(emailText, passwordText);
        if (user.isPresent()) {
            // Get the current stage (window) and set the new scene
            showUserProfile(user.get());
        } else MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Log In Error!",
                "Email or password is not correct!");

    }

    private void showUserProfile(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/user-profile-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) emailTextField.getScene().getWindow();
        stage.setScene(scene);

        UserProfileController controller = loader.getController();
        controller.setService(service, user);

        stage.show();
    }

    @Override
    public void update(UserEntityChangeData event) {

    }
}
