package org.thetasinner.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class BookUpdateRequest {
    private String title;
    private String isbn;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> authors = new ArrayList<>();
    private String publisher;
    private Date datePublished;
    private String description;
    private BookMetadataUpdateRequest bookMetadataUpdateRequest;
}
