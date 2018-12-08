package org.thetasinner.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

@JsonSerialize
@Data
public class UploadResponse {
  private List<Integer> failedUploadIndices;

  public UploadResponse(List<Integer> failedUploadIndices) {
    this.failedUploadIndices = failedUploadIndices;
  }
}
