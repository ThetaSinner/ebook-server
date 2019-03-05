package org.thetasinner.web.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thetasinner.maintenance.MaintenanceService;
import org.thetasinner.web.model.EmptyJsonResponse;
import org.thetasinner.web.model.ReportFixModel;
import org.thetasinner.web.model.ReportModel;

import java.io.FileNotFoundException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {
  private final MaintenanceService maintenanceService;

  public MaintenanceController(MaintenanceService maintenanceService) {
    this.maintenanceService = maintenanceService;
  }

  @RequestMapping(path = "/report", method = RequestMethod.GET)
  public @ResponseBody ReportModel createReport(@RequestParam(name = "libraryName") String libraryName) throws FileNotFoundException {
    return maintenanceService.createReport(libraryName);
  }

  @RequestMapping(path = "/report/{reportId}", method = RequestMethod.PUT)
  public @ResponseBody ReportModel createReport(@PathVariable String reportId, @RequestParam(name = "libraryName") String libraryName, @RequestBody ReportFixModel reportFixModel) throws FileNotFoundException {
    return maintenanceService.applyReportFixes(libraryName, reportId, reportFixModel);
  }
}
