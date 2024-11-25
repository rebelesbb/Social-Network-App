package com.socialnetwork.socialnetworkapp.controller;

import com.socialnetwork.socialnetworkapp.domain.Request;
import com.socialnetwork.socialnetworkapp.domain.Status;
import com.socialnetwork.socialnetworkapp.domain.Tuple;
import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import com.socialnetwork.socialnetworkapp.utils.events.ObjectChangeEvent;
import com.socialnetwork.socialnetworkapp.utils.objects.RequestDTO;
import com.socialnetwork.socialnetworkapp.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestsController implements Observer<ObjectChangeEvent> {
    ObservableList<RequestDTO> model = FXCollections.observableArrayList();
    Map<Integer, Tuple<Button, Button>> buttons = new HashMap<>();

    @FXML
    private TableView<RequestDTO> requestTableView;
    @FXML
    private TableColumn<RequestDTO, String> nameColumn;
    @FXML
    private TableColumn<RequestDTO, String> timeColumn;
    @FXML
    private TableColumn<RequestDTO, String> statusColumn;
    @FXML
    private TableColumn<RequestDTO, Void> actionColumn;

    MenuBarController menuBarController;
    SocialNetworkService service;
    User user;

    public void setService(SocialNetworkService service, User user) {
        this.service = service;
        this.user = user;
        service.addObserver(this);
        menuBarController = new MenuBarController();
        menuBarController.setStageServiceUser((Stage) requestTableView.getScene().getWindow(), user, service);
        initModel();
    }

    public void initModel() {
        List<Request> requests = service.getRequestsOfUser(user);
        System.out.println(requests);
        model.setAll(getRequestsDTO());
    }


    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        actionColumn.setCellFactory(column -> new TableCell<RequestDTO, Void>() {
                    private final Button acceptButton = new Button("Accept");
                    private final Button declineButton = new Button("Decline");
                    {
                        // Accept button action
                        acceptButton.setOnAction(event -> {
                            RequestDTO request = getTableView().getItems().get(getIndex());
                            handleAccept(request);
                            getTableView().getItems().remove(getIndex());
                        });

                        // Decline button action
                        declineButton.setOnAction(event -> {
                            RequestDTO request = getTableView().getItems().get(getIndex());
                            handleDecline(request);
                            getTableView().getItems().remove(getIndex());
                        });
                    }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null); // Clear content for empty cells
                } else {
                    // Set the buttons in the same cell, you can use HBox to organize them
                    HBox hbox = new HBox(acceptButton, declineButton);
                    setGraphic(hbox);
                }
            }
        });

        requestTableView.setItems(model);
    }

    @Override
    public void update(ObjectChangeEvent event) {

    }

    public void handleAccept(RequestDTO request) {
        service.addFriend(request.getSenderID(), request.getReceiverID());
        Optional<Request> r = service.getRequestById(request.getSenderID(), request.getReceiverID());
        if(r.isPresent()) {
            Request rec = r.get();
            service.updateRequest(rec.getId().getFirst(), rec.getId().getSecond(), Status.ACCEPTED,
                    rec.getTimeSent());
            model.clear();
            initModel();
        }
    }

    public void handleDecline(RequestDTO request) {
        Optional<Request> r = service.getRequestById(request.getSenderID(), request.getReceiverID());
        r.ifPresent(value -> value.setStatus(Status.DECLINED));
        initModel();
    }

    public List<RequestDTO> getRequestsDTO() {
        return service.getRequestsOfUser(user)
                .stream()
                .map(request -> {
                    Optional<User> sender = service.getUserById(request.getId().getFirst());
                    String name = "";
                    if(sender.isPresent()) {
                        name = sender.get().getFirstName() + " " + sender.get().getLastName();
                    }
                    return new RequestDTO(name, request.getId().getFirst(), request.getId().getSecond(),
                            request.getTimeSent().toString(), request.getStatus().toString());
                })
                .toList();
    }

    public void goToUserProfile() throws IOException {
        menuBarController.goToUserProfile();
    }

    public void goToSearchMenu() throws IOException {
        menuBarController.goToSearchMenu();
    }
}
