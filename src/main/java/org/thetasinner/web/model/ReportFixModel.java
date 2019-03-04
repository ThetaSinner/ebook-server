package org.thetasinner.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize
@Data
public class ReportFixModel {
  public ReportModel reportModel;
  public List<ReportFixFieldModel> reportFixFieldModels = new ArrayList<>();
}
