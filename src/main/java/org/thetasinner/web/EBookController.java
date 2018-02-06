package org.thetasinner.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
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
    public @ResponseBody UploadResponse upload(@RequestParam(name = "files") MultipartFile[] files) {
        System.out.println("Got a request");

        for (MultipartFile file : files) {
            System.out.println(file.getOriginalFilename());
        }

        return new UploadResponse(UploadResponse.Status.Ok);
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public @ResponseBody List<Book> getBooks(@RequestParam(name = "token") String token, @RequestParam(name = "name") String name) {
        return eBookDataService.getBooks(token, name);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.GET)
    public @ResponseBody Book getBook(@RequestParam(name = "id") String id) {
        return eBookDataService.getBook(id);
    }

    @RequestMapping(value = "/books", method = RequestMethod.POST)
    public @ResponseBody Book createBook(@RequestBody RequestBase<BookAddRequest> request) {
        return eBookDataService.createBook(request.getToken(), request.getName(), request.getRequest());
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.PATCH)
    public @ResponseBody Book updateBook(@RequestParam(name = "id") String id, @RequestBody BookUpdateRequest bookUpdateRequest) {
        return eBookDataService.updateBook(id, bookUpdateRequest);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteBook(@RequestParam(name = "id") String id) {
        eBookDataService.deleteBook(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/books/{id}/metadata", method = RequestMethod.GET)
    public @ResponseBody BookMetadata getBookMetadata(@RequestParam(name = "id") String id) {
        return eBookDataService.getBookMetadata(id);
    }

    @RequestMapping(value = "/books/{id}/metadata", method = RequestMethod.PUT)
    public @ResponseBody BookMetadata createBookMetadata(@RequestParam(name = "id") String id, @RequestBody BookMetadata bookMetadata) {
        return eBookDataService.createBookMetadata(id, bookMetadata);
    }

    @RequestMapping(value = "/books/{id}/metadata", method = RequestMethod.DELETE)
    public ResponseEntity deleteBookMetadata(@RequestParam(name = "id") String id) {
        eBookDataService.deleteBookMetadata(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/books/{id}/metadata", method = RequestMethod.PATCH)
    public @ResponseBody BookMetadata updateBookMetadata(@RequestParam(name = "id") String id, @RequestBody BookMetadataUpdateRequest bookMetadataUpdateRequest) {
        return eBookDataService.updateBookMetadata(id, bookMetadataUpdateRequest);
    }
}
