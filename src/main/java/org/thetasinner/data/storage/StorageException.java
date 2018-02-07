package org.thetasinner.data.storage;

public class StorageException extends Exception {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Exception cause) {
        super(message, cause);
    }
}
