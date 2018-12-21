package org.thetasinner.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonSerialize
@Data
public class ReportModel {
  private final String reportId;
  private List<MissingBook> missingBooks = new ArrayList<>();
  private List<MissingBook> unlistedBooks = new ArrayList<>();
  private ReportMetricsModel metrics;
  private UnreachableBooksModel unreachableBooksModel;
  private ReportCurationMetricsModel curationMetrics;

  public ReportModel() {
    this.reportId = UUID.randomUUID().toString();
  }

  public String getReportId() {
    return reportId;
  }
}
