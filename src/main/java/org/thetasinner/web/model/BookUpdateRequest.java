package org.thetasinner.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookUpdateRequest {
    private String title;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> authors = new ArrayList<>();
    private String publisher;
    private Date datePublished;
    private BookMetadataUpdateRequest bookMetadataUpdateRequest;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public BookMetadataUpdateRequest getBookMetadataUpdateRequest() {
        return bookMetadataUpdateRequest;
    }

    public void setBookMetadataUpdateRequest(BookMetadataUpdateRequest bookMetadataUpdateRequest) {
        this.bookMetadataUpdateRequest = bookMetadataUpdateRequest;
    }
}
