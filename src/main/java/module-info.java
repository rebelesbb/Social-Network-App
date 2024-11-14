module com.socialnetwork.socialnetworkapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.socialnetwork.socialnetworkapp to javafx.fxml;
    exports com.socialnetwork.socialnetworkapp;
}