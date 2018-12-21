package org.thetasinner.web.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReportCurationMetricsModel {
    private List<BookWithMissingData> booksWithMissingTitles = new ArrayList<>();
    private List<BookWithMissingData> booksWithMissingPublisher = new ArrayList<>();
    private List<BookWithMissingData> booksWithMissingAuthors = new ArrayList<>();
}
