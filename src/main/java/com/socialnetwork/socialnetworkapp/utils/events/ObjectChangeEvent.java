package com.socialnetwork.socialnetworkapp.utils.events;

public class ObjectChangeEvent implements Event{
    private final ObjectChangeEventType type;
    private final Object data;
    private Object oldData;

    public ObjectChangeEvent(ObjectChangeEventType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public ObjectChangeEvent(ObjectChangeEventType type, Object data, Object oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ObjectChangeEventType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public Object getOldData() {
        return oldData;
    }
}
