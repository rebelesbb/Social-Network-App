package com.socialnetwork.socialnetworkapp.domain;

import java.util.Objects;

/**
 * Define a Tuple o generic type entities
 * @param <E1> - tuple first entity type
 * @param <E2> - tuple second entity type
 */
public class Tuple<E1, E2> {
    private final E1 first;
    private final E2 second;

    public Tuple(E1 first, E2 second) {
        this.first = first;
        this.second = second;
    }

    public E1 getFirst() {
        return first;
    }

    public E2 getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Tuple)) return false;
        Tuple<?, ?> other = (Tuple<?, ?>) obj;
        return this.first.equals(other.first) && this.second.equals(other.second);
    }

    @Override
    public int hashCode(){
        return Objects.hash(first, second);
    }
}
