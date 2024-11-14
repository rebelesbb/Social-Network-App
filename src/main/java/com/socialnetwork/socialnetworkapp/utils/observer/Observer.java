package com.socialnetwork.socialnetworkapp.utils.observer;

import com.socialnetwork.socialnetworkapp.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E event);
}
