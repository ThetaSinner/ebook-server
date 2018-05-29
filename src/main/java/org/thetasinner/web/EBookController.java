package org.thetasinner.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.EBookDataService;
import org.thetasinner.data.model.Book;
import org.thetasinner.web.model.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class EBookController {
    @Autowired
    private EBookDataService eBookDataService;

    @RequestMapping(value = "/libraries/commit", method = RequestMethod.POST)
    public @ResponseBody EmptyJsonResponse commit(@RequestBody CommitRequest commitRequest) {
        eBookDataService.commit(commitRequest);
        return new EmptyJsonResponse();
    }

    @RequestMapping(value = "/libraries", method = RequestMethod.POST)
    public @ResponseBody EmptyJsonResponse create(@RequestParam(name = "name") String name) {
        eBookDataService.createLibrary(name);
        return new EmptyJsonResponse();
    }

    @RequestMapping(value = "/libraries/upload", method = RequestMethod.POST)
    public @ResponseBody UploadResponse upload(@RequestParam(name = "name") String name, @RequestParam(name = "files") MultipartFile[] files) {
        List<Integer> failedUploadIndices = eBookDataService.storeAll(name, files);

        return new UploadResponse(failedUploadIndices);
    }

    @RequestMapping(value = "/libraries", method = RequestMethod.GET)
    public @ResponseBody List<String> getLibraries() {
        return eBookDataService.getLibraries();
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public @ResponseBody List<Book> getBooks(@RequestParam(name = "name") String name) {
        return eBookDataService.getBooks(name);
    }

    @RequestMapping(value = "/books", method = RequestMethod.POST)
    public @ResponseBody Book createBook(@RequestBody RequestBase<BookAddRequest> request) {
        return eBookDataService.createBook(request.getName(), request.getRequest());
    }

    // Note that the title is used by the browser as a page title - therefore you can display any name you like from the
    // browser by using the path variable.
    @RequestMapping(value = "/books/{title}", method = RequestMethod.GET)
    public void getBook(@RequestParam("id") String id, @RequestParam(name = "name") String name, HttpServletResponse response) {
        try {
            final String contentType = eBookDataService.getBook(id, name, response.getOutputStream());
            response.setContentType(contentType);
            response.flushBuffer();
        }
        catch (IOException e) {
            throw new EbookControllerException("Failed to get book", e);
        }
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.PATCH)
    public @ResponseBody Book updateBook(@PathVariable("id") String id, @RequestBody RequestBase<BookUpdateRequest> bookUpdateRequest) {
        return eBookDataService.updateBook(id, bookUpdateRequest.getName(), bookUpdateRequest.getRequest());
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.DELETE)
    public @ResponseBody EmptyJsonResponse deleteBook(@PathVariable("id") String id, @RequestParam(name = "name") String name) {
        eBookDataService.deleteBook(id, name);
        return new EmptyJsonResponse();
    }

    @RequestMapping(value = "/books/{id}/covers", method = RequestMethod.POST)
    public @ResponseBody UploadResponse uploadCover(@PathVariable("id") String id, @RequestParam(name = "name") String name, @RequestParam(name = "cover") MultipartFile cover) {
        List<Integer> failedUploadIndices = eBookDataService.uploadCover(id, name, cover);

        return new UploadResponse(failedUploadIndices);
    }
}
