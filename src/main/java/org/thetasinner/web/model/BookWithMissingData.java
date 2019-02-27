package org.thetasinner.web.model;

import lombok.Data;

import java.nio.file.Path;

@Data
public class BookWithMissingData {
    private String bookId;
    private String reportId;
    private String uriFragment;
}
