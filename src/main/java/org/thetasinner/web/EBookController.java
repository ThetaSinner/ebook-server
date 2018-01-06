package org.thetasinner.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.BookMetadata;
import org.thetasinner.web.model.BookAddRequest;

import java.util.List;

@RestController
public class EBookController {
    @Autowired
    private EBookDataService eBookDataService;

    @RequestMapping("/load")
    public ResponseEntity load(@RequestParam(name="name", defaultValue = "default") String name) {
        eBookDataService.load(name);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping("/save")
    public ResponseEntity save(@RequestParam(name="name", defaultValue = "default") String name) {
        eBookDataService.save(name);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping("/books")
    public @ResponseBody List<Book> books() {
        return eBookDataService.getBooks();
    }

    @RequestMapping("/book")
    public @ResponseBody Book book(@RequestParam(name="id") String id) {
        return eBookDataService.getBook(id);
    }

    @RequestMapping("/book/add")
    public @ResponseBody Book addBook(@RequestBody BookAddRequest bookAddRequest) {
        return eBookDataService.addBook(bookAddRequest);
    }

    @RequestMapping("book/metadata")
    public @ResponseBody BookMetadata bookMetadata(@RequestParam(name="id") String id) {
        return eBookDataService.getBookMetadata(id);
    }
}
