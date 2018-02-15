package org.thetasinner.web.model;

import lombok.Data;

import java.util.List;

@Data
public class CommitRequest {
    private Boolean commitAll;
    private Boolean commitAndUnloadAll;

    private List<CommitLibrary> commitLibraries;

}
