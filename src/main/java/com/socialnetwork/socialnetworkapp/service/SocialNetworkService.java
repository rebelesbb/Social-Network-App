package com.socialnetwork.socialnetworkapp.service;

import com.socialnetwork.socialnetworkapp.domain.*;
import com.socialnetwork.socialnetworkapp.domain.validators.ValidationException;
import com.socialnetwork.socialnetworkapp.repository.DataManagerStructure;
import com.socialnetwork.socialnetworkapp.repository.repository_exceptions.FriendshipAlreadyExistsException;
import com.socialnetwork.socialnetworkapp.repository.repository_exceptions.InvalidDataProvidedException;
import com.socialnetwork.socialnetworkapp.repository.repository_exceptions.RequestAlreadySentException;
import com.socialnetwork.socialnetworkapp.repository.repository_exceptions.UserAlreadyExistsException;
import com.socialnetwork.socialnetworkapp.utils.dto.FriendshipFilterDTO;
import com.socialnetwork.socialnetworkapp.utils.events.ObjectChangeEventType;
import com.socialnetwork.socialnetworkapp.utils.events.ObjectChangeEvent;
import com.socialnetwork.socialnetworkapp.utils.observer.Observable;
import com.socialnetwork.socialnetworkapp.utils.observer.Observer;
import com.socialnetwork.socialnetworkapp.utils.paging.Page;
import com.socialnetwork.socialnetworkapp.utils.paging.Pageable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SocialNetworkService implements Observable<ObjectChangeEvent> {
    private final DataManagerStructure dataManager;
    private final List<Observer<ObjectChangeEvent>> observers = new ArrayList<>();

    public SocialNetworkService(DataManagerStructure dataManager){
        this.dataManager = dataManager;
    }

    /**
     *
     * @return The list of users
     */
    public Iterable<User> getUsers(){
        return dataManager.getUserRepository().findAll();
    }

    /**
     * Finds oll friends of a given user
     * @param user the given user
     * @return the list of friends  of the user
     */
    public Iterable<User> getFriendsOfUser(User user){
        List <User> friends = new ArrayList<>();

        for(Friendship friendship : dataManager.getFriendshipRepository().findAll()){

            if(friendship.getId().getFirst().equals(user.getId())){
                Optional<User> friend = dataManager.getUserRepository().findOne(friendship.getId().getSecond());
                friend.ifPresent(friends::add);
            }
            else if(friendship.getId().getSecond().equals(user.getId())){
                Optional<User> friend = dataManager.getUserRepository().findOne(friendship.getId().getFirst());
                friend.ifPresent(friends::add);
            }

        }

        return friends;
    }


    /**
     * Checks if there is any user with the given credentials: email and password
     * @param email the email of the supposed user
     * @param password the password of the supposed user
     * @return optional - of user, if they exist
     *                  - empty otherwise
     */
    public Optional<User> getUserByCredentials(String email, String password){
        for(User user : dataManager.getUserRepository().findAll()){
            if(user.getEmail().equals(email) && user.getPassword().equals(password)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds all users whose first name or last name start with a specified string
     * @param name the given string, that needs to be contained by either the first of last name, at the beginning
     * @return a list of user whose first name or last name start with a specified string
     */
    public List<User> getUsersByName(String name){
        return StreamSupport.stream(dataManager.getUserRepository().findAll().spliterator(), false)
                .filter(user -> (user.getFirstName().toLowerCase().startsWith(name.toLowerCase()) ||
                                user.getLastName().toLowerCase().startsWith(name.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * Returns the requests of a user
     * @param user the user whose requests we want to get
     * @return the friend request of the given user
     */
    public List<Request> getRequestsOfUser(User user){
        return StreamSupport.stream(dataManager.getRequestRepository().findAll().spliterator(), false)
                .filter(request -> Objects.equals(request.getId().getSecond(), user.getId())
                        && Objects.equals(request.getStatus(), Status.PENDING))
                .collect(Collectors.toList());
    }

    /**
     * Finds a request based on id
     * @param senderId the id of the person who sent the request
     * @param receiverId the id of the person who received the request
     * @return the friend request object with the id (senderId, receiverId)
     */
    public Optional<Request> getRequestById(Long senderId, Long receiverId){
        return dataManager.getRequestRepository().findOne(new Tuple<>(senderId, receiverId));
    }

    /**
     * Finds a user based on their id
     * @param id the id of the user to be found
     * @return the user found with the given id
     */
    public Optional<User> getUserById(Long id){
        return dataManager.getUserRepository().findOne(id);
    }

    /**
     * Computes the number of friend requests of a given user
     * @param user: the given user
     * @return the number of friend requests
     */
    public Integer getRequestsCount(User user){
        return getRequestsOfUser(user).stream()
                .filter(request -> request.getStatus() == Status.PENDING)
                .toList().size();
    }

    /**
     * Checks if a user is friend with another user
     * @param user one of the users
     * @param selectedUser one of the ussers
     * @return true - if the two users are friends, false - otherwise
     */
    public boolean isFriendOfUser(User user, User selectedUser) {
        return dataManager.getFriendshipRepository().findOne(new Tuple<>(user.getId(), selectedUser.getId())).isPresent();
    }

    //-----------------------------------------------------------------------------

    /**
     * Creates a friendship between two users
     * @param userId the id of the first user
     * @param newFriendId the id of the second user
     * @throws FriendshipAlreadyExistsException if the two users are already friends
     */
    public void addFriend(Long userId, Long newFriendId){
        if(dataManager.getUserRepository().findOne(userId).isEmpty()
        || dataManager.getUserRepository().findOne(newFriendId).isEmpty()){
            throw new InvalidDataProvidedException();
        }

        Friendship newFriendship = new Friendship(LocalDateTime.now());
        newFriendship.setId(new Tuple<>(Math.min(userId, newFriendId),
                                        Math.max(userId, newFriendId)));

        Optional<Friendship> f = dataManager.getFriendshipRepository().save(newFriendship);

        if(f.isPresent()){
            throw new FriendshipAlreadyExistsException();
        }
        else notifyObservers(new ObjectChangeEvent(ObjectChangeEventType.ACCEPT, newFriendship));
    }


    /**
     * Adds a new user
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param email the email of the user
     * @param password the password of the user
     * @throws UserAlreadyExistsException if there is a user with the same email
     * @throws ValidationException if any of the given data isn't valid
     */
    public void addUser(String firstName, String lastName, String email, String password){
        User newUser = new User(firstName, lastName, email, password);

        Optional<User> u = dataManager.getUserRepository().save(newUser);
        if(u.isPresent()){
            throw new UserAlreadyExistsException();
        }else {
            notifyObservers(new ObjectChangeEvent(ObjectChangeEventType.ADD, newUser));
        }
    }

    /**
     * Deletes a user (and the friendships related to them)
     * @param userId the id of the user
     * @throws InvalidDataProvidedException if the id does not exist
     */
    public void deleteUser(Long userId){
        Optional<User> toDeleteUser = dataManager.getUserRepository().findOne(userId);

        if(toDeleteUser.isPresent()) {
            List<Friendship> friendsToDelete = new ArrayList<>();

            dataManager.getFriendshipRepository().findAll().forEach(f -> {
                Tuple<Long, Long> friends = f.getId();
                if (friends.getFirst().equals(userId) || friends.getSecond().equals(userId))
                    friendsToDelete.add(f);
            });

            friendsToDelete.forEach(f -> dataManager.getFriendshipRepository().delete(f.getId()));
            dataManager.getUserRepository().delete(userId);
            notifyObservers(new ObjectChangeEvent(ObjectChangeEventType.REMOVE, toDeleteUser.get()));
        }
        else{
            throw new InvalidDataProvidedException();
        }
    }

    /**
     * Creates a friend request
     * @param userId the id of the user who sent the request
     * @param newFriendId the id of the user receiving the request
     */
    public void addFriendRequest(Long userId, Long newFriendId){
        if(dataManager.getUserRepository().findOne(userId).isEmpty()
                || dataManager.getUserRepository().findOne(newFriendId).isEmpty()){
            throw new InvalidDataProvidedException();
        }

        Request newRequest = new Request(userId, newFriendId, LocalDateTime.now());

        Optional<Request> r = dataManager.getRequestRepository().save(newRequest);

        if(r.isPresent()){
            throw new RequestAlreadySentException();
        }
        else notifyObservers(new ObjectChangeEvent(ObjectChangeEventType.REQUEST, newRequest));
    }

    /**
     * Deletes the friendship between two users
     * @param userId the id of the first user
     * @param oldFriendId the id of the second user
     */
    public void removeFriend(Long userId, Long oldFriendId){
        Tuple<Long, Long> friendshipId =new Tuple<>(Math.min(userId, oldFriendId),
                                                    Math.max(userId, oldFriendId));

        Optional<Friendship> f = dataManager.getFriendshipRepository().delete(friendshipId);
        if(f.isEmpty()){
            throw new InvalidDataProvidedException();
        }
        else {
            notifyObservers(new ObjectChangeEvent(ObjectChangeEventType.REMOVE, f.get()));
        }
    }

    /**
     * Updates the data of a user
     * @param userId the id of the user
     * @param newFirstName the new first name
     * @param newLastName the new last name
     * @param newEmail the new email
     * @param newPassword the new password
     */
    public void updateUser(Long userId, String newFirstName, String newLastName, String newEmail, String newPassword){
        User newUser = new User(newFirstName, newLastName, newEmail, newPassword);
        newUser.setId(userId);
        Optional<User> oldUser = dataManager.getUserRepository().findOne(userId);
        if(oldUser.isPresent()){
            Optional<User> updatedUser = dataManager.getUserRepository().update(newUser);
            if(updatedUser.isEmpty()){
                notifyObservers(new ObjectChangeEvent(ObjectChangeEventType.UPDATE, newUser));
            }
        }
    }

    public void updateRequest(Long senderId, Long receiverId, Status status, LocalDateTime date){
        Optional<Request> oldRequest = dataManager.getRequestRepository().findOne(new Tuple<>(senderId, receiverId));
        if(oldRequest.isPresent()) {
            Request newRequest = new Request(senderId, receiverId, status, date);
            Optional<Request> r = dataManager.getRequestRepository().update(newRequest);
        }

    }

    /**
     * Adds a Message
     * @param from_uid: the id of the user sending the message
     * @param to_uid: the id of the user receiving the message
     * @param date: the date when the message was sent
     * @param text: the text of the message
     */
    public void addMessage(Long from_uid, Long to_uid, LocalDateTime date, String text){
        Message message = new Message(from_uid, to_uid, date, text);
        dataManager.getMessagesRepository().save(message);
        notifyObservers(new ObjectChangeEvent(ObjectChangeEventType.MESSAGE_SENT, message));
    }


    /**
     * Applies the depth first search starting from a given memeber of a community
     * @param source the given member
     * @param friendsLists the list of friends of every user
     * @param checked map of the state of a member of the community (checked = true, not checked = false)
     * @param community list of the so far dicovered members of the current community
     */
    private void searchCommunity(Long source, Map<Long, List<Long>> friendsLists, Map<Long, Boolean> checked,
                                 List<Long> community){
        checked.put(source, true);
        community.add(source);

        friendsLists.get(source).forEach(next -> {
            if(!checked.get(next)){
                searchCommunity(next, friendsLists, checked, community);
            }
        });

    }

    /**
     * Provides the number of communities along with the most sociable community
     * @return the number if communities and a list with the members of the one with the most members
     */
    private Tuple<Long, List<Long>> getCommunitiesData(){
        Map<Long, List<Long>> friendsList = new HashMap<>();
        Map<Long, Boolean> checked = new TreeMap<>();
        List<Long> biggestCommunity = null;
        Long communitiesCount = (long)0;
        long biggestCommunitySize = 0;

        // Initializing the lists
        dataManager.getUserRepository().findAll().forEach(u -> checked.put(u.getId(), false));

        dataManager.getUserRepository().findAll().forEach(u -> friendsList.computeIfAbsent(u.getId(), _ -> new ArrayList<>()));

        dataManager.getFriendshipRepository().findAll().forEach(f -> {
            Tuple<Long, Long> friends = f.getId();
            friendsList.computeIfAbsent(friends.getFirst(), _ -> new ArrayList<>()).add(friends.getSecond());
            friendsList.computeIfAbsent(friends.getSecond(), _ -> new ArrayList<>()).add(friends.getFirst());
        });

        friendsList.forEach((_, friends) -> Collections.sort(friends));

        for(Long userId : checked.keySet()){
            if(!checked.get(userId)){
                communitiesCount++;

                List<Long> currentCommunity = new ArrayList<>();
                searchCommunity(userId, friendsList, checked, currentCommunity);

                if(currentCommunity.size() > biggestCommunitySize){
                    biggestCommunity = currentCommunity;
                    biggestCommunitySize = currentCommunity.size();
                }
            }
        }

        return new Tuple<>(communitiesCount, biggestCommunity);
    }

    /**
     * Computes the number of communities
     * @return the number of communities
     */
    public Long communitiesCount(){
        return getCommunitiesData().getFirst();
    }

    /**
     * Finds the most sociable community
     * @return the community with the most members
     */
    public List<User> biggestCommunity(){
        List<User> communityUsers = new ArrayList<>();
        List<Long> usersId = getCommunitiesData().getSecond();

        usersId.forEach(x -> {
            if(dataManager.getUserRepository().findOne(x).isPresent())
                communityUsers.add(dataManager.getUserRepository().findOne(x).get());
        });

        return communityUsers;
    }

    //----------------------------------------- OBSERVER ---------------------------------------------

    @Override
    public void addObserver(Observer<ObjectChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<ObjectChangeEvent> observer) {
        //obserbers.remove(observer)
    }

    @Override
    public void notifyObservers(ObjectChangeEvent event) {
        observers.forEach(observer -> observer.update(event));
    }


    public List<Message> getMessagesOfUsers(Long user, Long friendOfUser) {
        return StreamSupport.stream(dataManager.getMessagesRepository().findAll().spliterator(), false)
                .filter(message -> (Objects.equals(message.getSentFrom(), user) && Objects.equals(message.getSentTo(), friendOfUser))
                || (Objects.equals(message.getSentFrom(), friendOfUser) && Objects.equals(message.getSentTo(), user)))
                .toList();
    }

    //----------------------------------------- PAGING ---------------------------------------------
    public Page<Friendship> findAllOnPage(Pageable pageable, FriendshipFilterDTO filter){
        return dataManager.getFriendshipRepository().findAllOnPage(pageable, filter);
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
