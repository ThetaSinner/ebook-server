package org.thetasinner.web.model;

import lombok.Data;

@Data
public class UnreachableLocalBook {
    private String bookId;
    private String reportId;
    private String brokenPath;
}
