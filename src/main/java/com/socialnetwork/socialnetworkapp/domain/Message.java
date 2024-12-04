package com.socialnetwork.socialnetworkapp.domain;

import java.time.LocalDateTime;

public class Message extends Entity<Long>{
    private final Long sentFrom;
    private final Long sentTo;
    private final LocalDateTime date;
    private final String message;
    private Long reply;

    public Message(Long sentFrom, Long sentTo, LocalDateTime date, String message) {
        this.sentFrom = sentFrom;
        this.sentTo = sentTo;
        this.date = date;
        this.message = message;
        reply = null;
    }

    public Long getSentFrom() {
        return sentFrom;
    }

    public Long getSentTo() {
        return sentTo;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    public Long getReply() {
        return reply;
    }

}
