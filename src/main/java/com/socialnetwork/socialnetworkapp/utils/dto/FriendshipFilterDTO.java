package com.socialnetwork.socialnetworkapp.utils.dto;

import java.util.Optional;

public class FriendshipFilterDTO {
    private Optional<Long> userId;

    public Optional<Long> getUserId() {
        return userId;
    }
    public void setUserId(Optional<Long> userId) {
        this.userId = userId;
    }
}
