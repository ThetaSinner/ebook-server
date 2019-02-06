package org.thetasinner.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnlistedBook {
  private String missingBookId;
  private String issueId;
}
