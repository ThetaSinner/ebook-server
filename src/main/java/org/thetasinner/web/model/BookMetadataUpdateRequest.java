package org.thetasinner.web.model;

import java.util.List;

public class BookMetadataUpdateRequest {
    private List<String> tags;
    private Byte rating;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Byte getRating() {
        return rating;
    }

    public void setRating(Byte rating) {
        this.rating = rating;
    }
}
