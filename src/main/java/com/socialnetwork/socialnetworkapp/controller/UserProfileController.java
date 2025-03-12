package com.socialnetwork.socialnetworkapp.controller;

import com.socialnetwork.socialnetworkapp.domain.Friendship;
import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.service.SocialNetworkService;
import com.socialnetwork.socialnetworkapp.utils.dto.FriendshipFilterDTO;
import com.socialnetwork.socialnetworkapp.utils.events.ObjectChangeEvent;
import com.socialnetwork.socialnetworkapp.utils.observer.Observer;
import com.socialnetwork.socialnetworkapp.utils.paging.Page;
import com.socialnetwork.socialnetworkapp.utils.paging.Pageable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserProfileController implements Observer<ObjectChangeEvent> {
    @FXML
    private Label nameTextField;
    @FXML
    private Label emailTextField;
    @FXML
    private ListView<User> friendsListView;
    @FXML
    Button requestsButton;

    @FXML
    Button previousButton;
    @FXML
    Button nextButton;
    @FXML
    Label currentPageLabel;

    private MenuBarController menuBarController;
    ObservableList<User> model = FXCollections.observableArrayList();

    private int pageSize = 3;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    private User user;
    private SocialNetworkService service;

    public void setService(SocialNetworkService service, User user, MenuBarController menuBarController) {
        this.service = service;
        this.user = user;
        this.menuBarController = menuBarController;

        service.addObserver(this);

        menuBarController.setNotificationsCount(service.getRequestsCount(user));

        int notificationsCount = menuBarController.getNotificationsCount();
        if(notificationsCount > 0)
            requestsButton.setText("Requests" + "(" + menuBarController.getNotificationsCount() + ")");
        else requestsButton.setText("Requests");

        nameTextField.setText(user.getFirstName() + " " + user.getLastName());
        emailTextField.setText(user.getEmail());

        initModel();
    }

    private void initModel(){
        FriendshipFilterDTO filter = new FriendshipFilterDTO();
        filter.setUserId(Optional.of(user.getId()));
        Page<Friendship> page = service.findAllOnPage(new Pageable(currentPage, pageSize), filter);

        int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
        if(maxPage == -1){
            maxPage = 0;
        }
        if(currentPage > maxPage){
            currentPage = maxPage;
            page = service.findAllOnPage(new Pageable(currentPage, pageSize), filter);
        }

        totalNumberOfElements = page.getTotalNumberOfElements();

        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);

        List<User> friendsList = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .map(friendship -> {
                    Optional<User> friend;
                    if(friendship.getId().getFirst().equals(user.getId())){
                        friend = service.getUserById(friendship.getId().getSecond());
                    }
                    else friend = service.getUserById(friendship.getId().getFirst());
                    return friend.orElse(null);
                })
                .toList();

        model.setAll(friendsList);
        currentPageLabel.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }

    @FXML
    public void initialize() {

        friendsListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getFirstName() + " " + item.getLastName());
                }
                else {
                    setText(null);
                }
            }
        });

        friendsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                User selectedItem = friendsListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    try {
                        goToFriendProfile(selectedItem);
                    } catch (IOException e) {
                        MessageAlert.showErrorMessage(null, "Can't open friend profile");
                    }
                }
            }
        });

        friendsListView.setItems(model);
    }

    public void goToFriendProfile(User selectedUser) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialnetwork/socialnetworkapp/views/friend-view.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) friendsListView.getScene().getWindow();
        stage.setScene(new Scene(root));

        FriendController controller = loader.getController();
        controller.setService(service, user, selectedUser, menuBarController);

        stage.show();
    }

    @Override
    public void update(ObjectChangeEvent event) {
        switch (event.getType()){
            case ACCEPT:{
                Friendship newFriendship = (Friendship) event.getData();
                if(newFriendship.getId().getFirst().equals(user.getId()) ||
                newFriendship.getId().getSecond().equals(user.getId())) {
                    Long newFriendId;

                    if (Objects.equals(newFriendship.getId().getFirst(), user.getId())) {
                        newFriendId = newFriendship.getId().getSecond();
                    } else newFriendId = newFriendship.getId().getFirst();

                    Optional<User> newFriend = service.getUserById(newFriendId);
                    newFriend.ifPresent(value -> model.add(value));
                }
            }
        }
    }

    public void goToSearchMenu() throws IOException {
        menuBarController.goToSearchMenu();
    }

    public void goToRequests() throws IOException {
        menuBarController.goToRequests();
    }

    public void goToChats() throws IOException {
        menuBarController.goToChats();
    }

    public void handleLogout() throws IOException {
        menuBarController.goToLogInMenu();
    }

    public void handleUpdate(ActionEvent event){
        //service.updateUser();
    }

    public void onNextPage(ActionEvent event) {
        currentPage++;
        initModel();
    }

    public void onPreviousPage(ActionEvent event) {
        currentPage--;
        initModel();
    }
}
