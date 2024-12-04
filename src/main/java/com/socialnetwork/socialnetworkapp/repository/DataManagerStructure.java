package com.socialnetwork.socialnetworkapp.repository;

import com.socialnetwork.socialnetworkapp.domain.*;

public class DataManagerStructure {
    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Repository<Tuple<Long, Long>, Request> requestRepository;
    private final Repository<Long, Message> messageRepository;

    public DataManagerStructure(Repository<Long, User> usersRepo, Repository<Tuple<Long,Long>, Friendship> friendshipRepo,
                                Repository<Tuple<Long, Long>, Request> requestRepo, Repository<Long, Message> messageRepo) {
        this.userRepository = usersRepo;
        this.friendshipRepository = friendshipRepo;
        this.requestRepository = requestRepo;
        this.messageRepository = messageRepo;
    }

    /**
     *
     * @return the repository of users
     */
    public Repository<Long, User> getUserRepository(){
        return this.userRepository;
    }

    /**
     *
     * @return the repository of friendships
     */
    public Repository<Tuple<Long, Long>, Friendship> getFriendshipRepository(){
        return this.friendshipRepository;
    }

    /**
     *
     * @return the repository of requests
     */
    public Repository<Tuple<Long, Long>, Request> getRequestRepository(){
        return this.requestRepository;
    }

    /**
     *
     * @return the repository of messages
     */
    public Repository<Long, Message> getMessagesRepository(){
        return this.messageRepository;
    }
}
