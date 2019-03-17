package org.thetasinner.data.storage;

import lombok.Data;

import java.io.InputStream;

@Data
public class VideoContent {
    private String contentType;
    private InputStream inputStream;
}
