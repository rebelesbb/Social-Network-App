package com.socialnetwork.socialnetworkapp.utils.observer;

import com.socialnetwork.socialnetworkapp.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> observer);
    void removeObserver(Observer<E> observer);
    void notifyObservers(E event);
}
