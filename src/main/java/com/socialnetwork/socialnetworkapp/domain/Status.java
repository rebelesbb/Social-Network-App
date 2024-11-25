package com.socialnetwork.socialnetworkapp.domain;

public enum Status {
    PENDING, ACCEPTED, DECLINED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
