package org.thetasinner.web.model;

import lombok.Data;

@Data
public class ReportMetricsModel {
    private int numberOfBooks;
    private int numberOfWebLinkBooks;
    private int numberOfLocalManagedBooks;
    private int numberOfLocalUnmanagedBooks;
}
