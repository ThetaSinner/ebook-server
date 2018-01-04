package org.thetasinner.web.model;

public class BookAddResponse {
    private ResponseStatus responseStatus;

    public BookAddResponse(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public enum ResponseStatus {
        INVALID_PAYLOAD,
        FILE_NOT_FOUND,
        SUCCESS
    }
}
