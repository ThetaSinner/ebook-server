package org.thetasinner.data.storage;

import lombok.Data;
import org.thetasinner.data.model.TypedUrl;

@Data
public class StorageResult {
  private String id;
  private String fileName;
  private TypedUrl url;
}
