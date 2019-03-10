package org.thetasinner.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.web.model.UploadResponse;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/videos")
public class VideoController {
  private EBookDataService dataService;

  @Autowired
  VideoController(EBookDataService dataService) {
    this.dataService = dataService;
  }


  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public @ResponseBody
  UploadResponse upload(@RequestParam(name = "name") String name, @RequestParam(name = "files") MultipartFile[] files) {
    List<Integer> failedUploadIndices = dataService.storeAll(name, files);

    return new UploadResponse(failedUploadIndices);
  }
}
