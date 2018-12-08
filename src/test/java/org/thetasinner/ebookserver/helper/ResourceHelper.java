package org.thetasinner.ebookserver.helper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Service
public class ResourceHelper {
  public FileSystemResource getFileSystemResourceFromClasspath(String name) {
    var resource = Thread.currentThread()
            .getContextClassLoader()
            .getResource(name);
    assertNotNull(resource);
    return new FileSystemResource(resource.getFile());
  }
}
