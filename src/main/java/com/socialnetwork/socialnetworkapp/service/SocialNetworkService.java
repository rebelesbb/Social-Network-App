package com.socialnetwork.socialnetworkapp.service;

import com.socialnetwork.socialnetworkapp.domain.Friendship;
import com.socialnetwork.socialnetworkapp.domain.Tuple;
import com.socialnetwork.socialnetworkapp.domain.User;
import com.socialnetwork.socialnetworkapp.repository.DataManagerStructure;
import com.socialnetwork.socialnetworkapp.repository.repository_exceptions.FriendshipAlreadyExistsException;
import com.socialnetwork.socialnetworkapp.repository.repository_exceptions.InvalidDataProvidedException;
import com.socialnetwork.socialnetworkapp.repository.repository_exceptions.UserAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SocialNetworkService {
    private DataManagerStructure dataManager;

    public SocialNetworkService(DataManagerStructure dataManager){
        this.dataManager = dataManager;
    }

    private final Function<Friendship, String> friendshipStringFunction = f -> {
        Optional<User> u1 = dataManager.getUserRepository().findOne(f.getId().getFirst());
        Optional<User> u2 = dataManager.getUserRepository().findOne(f.getId().getSecond());
        if (u1.isPresent() && u2.isPresent()) {
            return u1.get().getFirstName() + " " + u1.get().getLastName()
                    + "(ID" + f.getId().getFirst() + ") - " +
                    u2.get().getFirstName() + " " + u2.get().getLastName()
                    + "(ID" + f.getId().getSecond() + ")";
        }
        return "";
    };

    /**
     *
     * @return The list of users
     */
    public Iterable<User> getUsers(){
        return dataManager.getUserRepository().findAll();
    }

    /**
     * Each friendship is made into a string having the format friend1(ID1) - friends2(ID2)
     * @return The list of frinedships, as strings
     */
    public Iterable<String> getPrintableFriendships(){
        List<Friendship> friendships = new ArrayList<>();



        // method reference
        dataManager.getFriendshipRepository().findAll().forEach(friendships::add);

        return friendships.stream()
                .map(friendshipStringFunction)
                .collect(Collectors.toList());
    }


    /**
     * Adds a new user
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param email the email of the user
     * @param password the password of the user
     * @throws UserAlreadyExistsException if there is a user with the same email
     */
    public void addUser(String firstName, String lastName, String email, String password){
        User newUser = new User(firstName, lastName, email, password);

        Optional<User> u = dataManager.getUserRepository().save(newUser);
        if(u.isPresent()){
            throw new UserAlreadyExistsException();
        }
    }

    /**
     * Deletes a user (and the friendships related to them)
     * @param userId the id of the user
     * @throws InvalidDataProvidedException if the id does not exist
     */
    public void deleteUser(Long userId){
        Optional<User> deletedUser = dataManager.getUserRepository().findOne(userId);

        if(deletedUser.isPresent()) {
            List<Friendship> friendsToDelete = new ArrayList<>();

            dataManager.getFriendshipRepository().findAll().forEach(f -> {
                Tuple<Long, Long> friends = f.getId();
                if (friends.getFirst().equals(userId) || friends.getSecond().equals(userId))
                    friendsToDelete.add(f);
            });

            friendsToDelete.forEach(f -> dataManager.getFriendshipRepository().delete(f.getId()));
            dataManager.getUserRepository().delete(userId);
        }
        else{
            throw new InvalidDataProvidedException();
        }
    }

    public Optional<User> getUserByCredentials(String email, String password){
        for(User user : dataManager.getUserRepository().findAll()){
            if(user.getEmail().equals(email) && user.getPassword().equals(password)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

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



}
