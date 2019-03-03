package org.thetasinner.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnlistedBook {
  private String missingBookId;
  private List<String> documentNames;
  private String issueId;
}
