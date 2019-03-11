package org.thetasinner.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentTypeService {
  @Value("#{'${es.content-types.video}'.split(',')}")
  private List<String> videoContentTypes;

  @Value("#{'${es.content-types.book}'.split(',')}")
  private List<String> bookContentTypes;

  public boolean isVideoContentType(String contentType) {
    return videoContentTypes.contains(contentType);
  }

  public boolean isBookContentType(String contentType) {
    return bookContentTypes.contains(contentType);
  }
}
