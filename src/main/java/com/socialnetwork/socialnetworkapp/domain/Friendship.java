package com.socialnetwork.socialnetworkapp.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Tuple<Long,Long>>{
    LocalDateTime date;

    public Friendship(){
    }

    public Friendship(LocalDateTime date){
        this.date = date;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate(){
        return date;
    }

    @Override
    public String toString() {
        return this.getId().getFirst() + "-" + this.getId().getSecond() + "-" + this.getDate();
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof Friendship)) return false;
        Friendship other = (Friendship)o;
        Tuple<Long, Long> thisId = this.getId();
        Tuple<Long, Long> otherId = other.getId();
        return Math.min(thisId.getFirst(), thisId.getSecond()) == Math.min(otherId.getFirst(), otherId.getSecond()) &&
                Math.max(thisId.getFirst(), thisId.getSecond()) == Math.max(otherId.getFirst(), otherId.getSecond());
    }

    @Override
    public int hashCode(){
        return Objects.hash(
                Math.min(this.getId().getFirst(), this.getId().getSecond()),
                Math.max(this.getId().getFirst(), this.getId().getSecond())
        );
    }
}
