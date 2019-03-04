package org.thetasinner.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@JsonSerialize
@Data
public class ReportFixFieldModel {
  public String reportFixFieldId;
  public FixAction fixAction;

  public enum FixAction {
    RemoveUnlisted,
    AddUnlisted
  }
}
