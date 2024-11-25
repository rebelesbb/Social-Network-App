package com.socialnetwork.socialnetworkapp.repository.repository_exceptions;

public class InvalidDataProvidedException extends RuntimeException {
    public InvalidDataProvidedException() {
      super("Invalid data provided!");
    }

    public InvalidDataProvidedException(String message) {
        super(message);
    }

    public InvalidDataProvidedException(String message, Throwable cause) {
      super(message, cause);
    }

    public InvalidDataProvidedException(Throwable cause) {
      super(cause);
    }

    public InvalidDataProvidedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }
}
