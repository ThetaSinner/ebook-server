package org.thetasinner.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.thetasinner.web.error.EBookControllerException;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.BookUpdateRequest;
import org.thetasinner.web.model.EmptyJsonResponse;
import org.thetasinner.web.model.RequestBase;
import org.thetasinner.web.model.UploadResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/books")
public class BookController {
  private EBookDataService dataService;

  @Autowired
  BookController(EBookDataService dataService) {
    this.dataService = dataService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody
  List<Book> getBooks(@RequestParam(name = "name") String name) {
    return dataService.getBooks(name);
  }

  @RequestMapping(method = RequestMethod.POST)
  public @ResponseBody
  Book createBook(@RequestBody RequestBase<BookAddRequest> request) {
    return dataService.createBook(request.getName(), request.getRequest());
  }

  // Note that the title is used by the browser as a page title - therefore you can display any name you like from the
  // browser by using the path variable.
  @RequestMapping(value = "/{title}", method = RequestMethod.GET)
  public void getBook(@RequestParam("id") String id, @RequestParam(name = "name") String name, HttpServletResponse response) {
    try {
      final String contentType = dataService.getBook(id, name, response.getOutputStream());
      response.setContentType(contentType);
      response.flushBuffer();
    } catch (IOException e) {
      throw new EBookControllerException("Failed to get book", e);
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
  public @ResponseBody
  Book updateBook(@PathVariable("id") String id, @RequestBody RequestBase<BookUpdateRequest> bookUpdateRequest) {
    return dataService.updateBook(id, bookUpdateRequest.getName(), bookUpdateRequest.getRequest());
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public @ResponseBody
  EmptyJsonResponse deleteBook(@PathVariable("id") String id, @RequestParam(name = "name") String name) {
    dataService.deleteBook(id, name);
    return new EmptyJsonResponse();
  }

  @RequestMapping(value = "/{id}/covers", method = RequestMethod.POST)
  public @ResponseBody
  UploadResponse uploadCover(@PathVariable("id") String id, @RequestParam(name = "name") String name, @RequestParam(name = "cover") MultipartFile cover) {
    List<Integer> failedUploadIndices = dataService.uploadCover(id, name, cover);

    return new UploadResponse(failedUploadIndices);
  }

  // TODO This is not restful but some work is needed to make covers work in general. i.e. on other books types and using urls rather than a single default cover
  @RequestMapping(value = "/{id}/covers", method = RequestMethod.GET)
  public void getCover(@PathVariable("id") String bookId, @RequestParam(name = "name") String libraryName, HttpServletResponse response) {
    try {
      var contentType = dataService.getCover(bookId, libraryName, response.getOutputStream());
      response.setContentType(contentType);
      response.flushBuffer();
    } catch (IOException e) {
      throw new EBookControllerException("Failed to get cover", e);
    }
  }
}
