package org.thetasinner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.file.FileCache;
import org.thetasinner.data.storage.file.FileLibraryStorage;

@Configuration
public class Config {
  @Bean
  public CommonsMultipartResolver multipartResolver() {
    CommonsMultipartResolver resolver = new CommonsMultipartResolver();
    resolver.setMaxUploadSize(-1);

    return resolver;
  }

  @Bean
  public ILibraryStorage libraryStorage(FileCache<Library> cache) {
    return new FileLibraryStorage(cache);
  }
}
