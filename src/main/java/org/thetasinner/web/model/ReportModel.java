package org.thetasinner.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonSerialize
public class ReportModel {
  private final String reportId;

  public ReportModel() {
    this.reportId = UUID.randomUUID().toString();
  }

  public String getReportId() {
    return reportId;
  }
}
