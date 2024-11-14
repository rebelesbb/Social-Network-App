package com.socialnetwork.socialnetworkapp;

import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserProfileController {
    @FXML
    private Label nameTextField;

    @FXML
    private Label emailTextField;

    private User user;
    private SocialNetworkService service;

    public void setService(SocialNetworkService service, User user) {
        this.service = service;
        this.user = user;
        initView();
    }

    private void initView(){
        nameTextField.setText(user.getFirstName() + " " + user.getLastName());
        emailTextField.setText(user.getEmail());
    }

    @FXML
    public void initialize() {
        nameTextField.setText("");
        emailTextField.setText("");
    }

}
