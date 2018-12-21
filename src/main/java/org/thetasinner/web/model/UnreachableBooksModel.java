package org.thetasinner.web.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UnreachableBooksModel {
    private List<UnreachableWebLink> webLinks = new ArrayList<>();
    private List<UnreachableLocalBook> localBooks = new ArrayList<>();
}
