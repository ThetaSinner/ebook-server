package org.thetasinner.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class UploadResponse {
    public enum Status {
        Ok,
        Err
    }

    private Status status;

    public UploadResponse(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
