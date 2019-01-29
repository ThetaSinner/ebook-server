package org.thetasinner.web.model;

import lombok.Data;

@Data
public class UnreachableWebLink {
    private String bookId;
    private String reportId;
    private int statusCode;
    private String brokenLink;
}
