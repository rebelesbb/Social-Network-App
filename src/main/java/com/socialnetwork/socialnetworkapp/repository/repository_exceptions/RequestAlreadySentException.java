package com.socialnetwork.socialnetworkapp.repository.repository_exceptions;

public class RequestAlreadySentException extends RuntimeException {
    public RequestAlreadySentException() {
        super("Request already sent!");
    }

    public RequestAlreadySentException(String message) {
        super(message);
    }

    public RequestAlreadySentException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestAlreadySentException(Throwable cause) {
        super(cause);
    }

    public RequestAlreadySentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
