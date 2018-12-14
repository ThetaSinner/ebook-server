package org.thetasinner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.content.LockableItemCache;
import org.thetasinner.data.storage.file.FileLibraryStorage;

@Configuration
@EnableAspectJAutoProxy
public class Config {
  @Bean
  public CommonsMultipartResolver multipartResolver() {
    CommonsMultipartResolver resolver = new CommonsMultipartResolver();
    resolver.setMaxUploadSize(-1);

    return resolver;
  }

  @Bean
  public ILibraryStorage libraryStorage() {
    return new FileLibraryStorage();
  }
}
