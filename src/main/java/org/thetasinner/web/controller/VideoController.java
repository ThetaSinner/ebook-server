package org.thetasinner.web.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Video;
import org.thetasinner.web.error.EBookControllerException;
import org.thetasinner.web.model.UploadResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody
  List<Video> getVideos(@RequestParam(name = "name") String name) {
    return dataService.getVideos(name);
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public @ResponseBody
  UploadResponse upload(@RequestParam(name = "name") String name, @RequestParam(name = "files") MultipartFile[] files) {
    List<Integer> failedUploadIndices = dataService.storeAll(name, files);

    return new UploadResponse(failedUploadIndices);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public void getVideo(@PathVariable("id") String id, @RequestParam(name = "name") String name, HttpServletResponse response) {
    try {
      final String contentType = dataService.getVideo(id, name, response.getOutputStream());
      response.setContentType(contentType);
      response.flushBuffer();
    } catch (IOException e) {
      throw new EBookControllerException("Failed to get video", e);
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
  public @ResponseBody
  Video updateVideo(@PathVariable("id") String id, @RequestParam("libraryName") String libraryName, @RequestBody String videoPatch) throws IOException, JsonPatchException {
    return dataService.updateVideo(id, libraryName, videoPatch);
  }
}
