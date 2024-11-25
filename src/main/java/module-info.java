module com.socialnetwork.socialnetworkapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.socialnetwork.socialnetworkapp to javafx.fxml;
    exports com.socialnetwork.socialnetworkapp;
    exports com.socialnetwork.socialnetworkapp.controller;
    exports com.socialnetwork.socialnetworkapp.service;
    exports com.socialnetwork.socialnetworkapp.domain;
    exports com.socialnetwork.socialnetworkapp.utils.events;
    exports com.socialnetwork.socialnetworkapp.utils.observer;
    exports com.socialnetwork.socialnetworkapp.utils.objects;
    exports com.socialnetwork.socialnetworkapp.repository;
    opens com.socialnetwork.socialnetworkapp.controller to javafx.fxml;
}