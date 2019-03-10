package org.thetasinner.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.web.model.CommitRequest;
import org.thetasinner.web.model.EmptyJsonResponse;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/libraries")
public class LibraryController {
  private EBookDataService dataService;

  @Autowired
  LibraryController(EBookDataService dataService) {
    this.dataService = dataService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody
  List<String> getLibraries() {
    return dataService.getLibraries();
  }

  @RequestMapping(method = RequestMethod.POST)
  public @ResponseBody
  EmptyJsonResponse create(@RequestParam(name = "name") String name) {
    dataService.createLibrary(name);
    return new EmptyJsonResponse();
  }

  @RequestMapping(value = "/commit", method = RequestMethod.POST)
  public @ResponseBody
  EmptyJsonResponse commit(@RequestBody CommitRequest commitRequest) {
    dataService.commit(commitRequest);
    return new EmptyJsonResponse();
  }
}
