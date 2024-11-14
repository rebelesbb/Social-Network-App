package com.socialnetwork.socialnetworkapp.utils.events;

import com.socialnetwork.socialnetworkapp.domain.User;

public class UserEntityChangeData implements Event{
    private final ChangeEventType type;
    private final User data;
    private User oldData;

    public UserEntityChangeData(ChangeEventType type, User data) {
        this.type = type;
        this.data = data;
    }

    public UserEntityChangeData(ChangeEventType type, User data, User oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}
