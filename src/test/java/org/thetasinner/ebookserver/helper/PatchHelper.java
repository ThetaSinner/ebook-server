package org.thetasinner.ebookserver.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;
import org.springframework.stereotype.Service;
import org.thetasinner.data.model.Book;

import java.io.IOException;
import java.io.StringWriter;

@Service
public class PatchHelper {
  public JsonNode getJsonPatch(Book originalBook, Book modifiedBook) throws IOException {
    JsonNode beforeNode = bookToJsonNode(originalBook);
    JsonNode afterNode = bookToJsonNode(modifiedBook);

    return JsonDiff.asJson(beforeNode, afterNode);
  }

  private JsonNode bookToJsonNode(Book book) throws IOException {
    var mapper = new ObjectMapper();
    StringWriter stringWriter = new StringWriter();
    mapper.writeValue(stringWriter, book);

    return mapper.readValue(stringWriter.toString(), JsonNode.class);
  }
}
