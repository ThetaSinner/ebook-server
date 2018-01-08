package org.thetasinner.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.BookMetadataUpdateRequest;
import org.thetasinner.web.model.BookUpdateRequest;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class EBookController {
    @Autowired
    private EBookDataService eBookDataService;

    @RequestMapping(value = "/load", method=RequestMethod.GET)
    public ResponseEntity load(@RequestParam(name="name", defaultValue = "default") String name) {
        eBookDataService.load(name);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method=RequestMethod.GET)
    public ResponseEntity save(@RequestParam(name="name", defaultValue = "default") String name) {
        eBookDataService.save(name);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public @ResponseBody List<Book> getBooks() {
        return eBookDataService.getBooks();
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.GET)
    public @ResponseBody Book getBook(@RequestParam(name="id") String id) {
        return eBookDataService.getBook(id);
    }

    @RequestMapping(value = "/books", method = RequestMethod.POST)
    public @ResponseBody Book createBook(@RequestBody BookAddRequest bookAddRequest) {
        return eBookDataService.createBook(bookAddRequest);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.PATCH)
    public @ResponseBody Book updateBook(@RequestParam(name="id") String id, @RequestBody BookUpdateRequest bookUpdateRequest) {
        return eBookDataService.updateBook(id, bookUpdateRequest);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteBook(@RequestParam(name="id") String id) {
        eBookDataService.deleteBook(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/books/{id}/metadata", method = RequestMethod.GET)
    public @ResponseBody BookMetadata getBookMetadata(@RequestParam(name="id") String id) {
        return eBookDataService.getBookMetadata(id);
    }

    @RequestMapping(value = "/books/{id}/metadata", method = RequestMethod.PUT)
    public @ResponseBody BookMetadata createBookMetadata(@RequestParam(name="id") String id, @RequestBody BookMetadata bookMetadata) {
        return eBookDataService.createBookMetadata(id, bookMetadata);
    }

    @RequestMapping(value = "/books/{id}/metadata", method = RequestMethod.DELETE)
    public ResponseEntity deleteBookMetadata(@RequestParam(name="id") String id) {
        eBookDataService.deleteBookMetadata(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/books/{id}/metadata", method = RequestMethod.PATCH)
    public @ResponseBody BookMetadata updateBookMetadata(@RequestParam(name="id") String id, @RequestBody BookMetadataUpdateRequest bookMetadataUpdateRequest) {
        return eBookDataService.updateBookMetadata(id, bookMetadataUpdateRequest);
    }
}
