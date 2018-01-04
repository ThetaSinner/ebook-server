package org.thetasinner.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.data.model.Book;

import java.util.List;

@RestController
public class Service {
    @Autowired
    private EBookDataService eBookDataService;

    @RequestMapping("/load")
    public ResponseEntity load(@RequestParam(name="name", defaultValue = "default") String name) {
        if (eBookDataService.load(name)) {
            return new ResponseEntity(HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping("/books")
    public @ResponseBody List<Book> books() {
        return eBookDataService.getBooks();
    }
}
