package org.thetasinner.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize
public class UploadResponse {
    private List<Integer> failedUploadIndices;

    public UploadResponse(List<Integer> failedUploadIndices) {
        this.failedUploadIndices = failedUploadIndices;
    }

    public List<Integer> getFailedUploadIndices() {
        return failedUploadIndices;
    }

    public void setFailedUploadIndices(List<Integer> failedUploadIndices) {
        this.failedUploadIndices = failedUploadIndices;
    }
}
