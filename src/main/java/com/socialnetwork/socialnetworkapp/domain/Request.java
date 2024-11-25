package com.socialnetwork.socialnetworkapp.domain;

import java.time.LocalDateTime;

public class Request extends Entity<Tuple<Long, Long>> {
    private Status status;
    private LocalDateTime timeSent;

    public Request(Long senderId, Long receiverId, LocalDateTime timeSent) {
        setId(new Tuple<>(senderId, receiverId));
        this.status = Status.PENDING;
        this.timeSent = timeSent;
    }

    public Request(Long senderId, Long receiverId, Status status, LocalDateTime timeSent) {
        setId(new Tuple<>(senderId, receiverId));
        this.status = status;
        this.timeSent = timeSent;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

}
