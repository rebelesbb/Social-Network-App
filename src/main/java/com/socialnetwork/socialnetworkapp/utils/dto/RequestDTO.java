package com.socialnetwork.socialnetworkapp.utils.dto;

public class RequestDTO {
    private final String name, date, status;
    private final Long senderID, receiverID;

    public RequestDTO(String name, Long senderID, Long receiverID, String date, String status) {
        this.name = name;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.date = date;
        this.status = status;
    }

    public Long getSenderID() {
        return senderID;
    }

    public Long getReceiverID() {
        return receiverID;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
