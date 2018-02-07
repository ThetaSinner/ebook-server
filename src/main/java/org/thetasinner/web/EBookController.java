package org.thetasinner.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.data.model.Book;
import org.thetasinner.web.model.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class EBookController {
    @Autowired
    private EBookDataService eBookDataService;

    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public @ResponseBody LoadResponse load(@RequestParam(name = "token") String token, @RequestParam(name = "name") String name) {
        String newToken = eBookDataService.load(token, name);
        return new LoadResponse(newToken);
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    public @ResponseBody EmptyJsonResponse save(@RequestParam(name = "token") String token, @RequestParam(name = "name") String name) {
        eBookDataService.save(token, name);
        return new EmptyJsonResponse();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody LoadResponse create(@RequestParam(name = "token") String token, @RequestParam(name = "name") String name) {
        String newToken = eBookDataService.create(token, name);
        return new LoadResponse(newToken);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody UploadResponse upload(@RequestParam(name = "token") String token, @RequestParam(name = "name") String name, @RequestParam(name = "files") MultipartFile[] files) {
        List<Integer> failedUploadIndices = eBookDataService.storeAll(token, name, files);

        return new UploadResponse(failedUploadIndices);
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public @ResponseBody List<Book> getBooks(@RequestParam(name = "token") String token, @RequestParam(name = "name") String name) {
        return eBookDataService.getBooks(token, name);
    }

    @RequestMapping(value = "/books", method = RequestMethod.POST)
    public @ResponseBody Book createBook(@RequestBody RequestBase<BookAddRequest> request) {
        return eBookDataService.createBook(request.getToken(), request.getName(), request.getRequest());
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.PATCH)
    public @ResponseBody Book updateBook(@PathVariable("id") String id, @RequestBody RequestBase<BookUpdateRequest> bookUpdateRequest) {
        return eBookDataService.updateBook(id, bookUpdateRequest.getToken(), bookUpdateRequest.getName(), bookUpdateRequest.getRequest());
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.DELETE)
    public @ResponseBody EmptyJsonResponse deleteBook(@PathVariable("id") String id, @RequestParam(name = "token") String token, @RequestParam(name = "name") String name) {
        eBookDataService.deleteBook(id, token, name);
        return new EmptyJsonResponse();
    }
}
