package org.thetasinner.ebookserver.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.thetasinner.data.model.Book;
import org.thetasinner.ebookserver.EmptyRequest;
import org.thetasinner.web.model.BookAddRequest;
import org.thetasinner.web.model.CommitLibrary;
import org.thetasinner.web.model.CommitRequest;
import org.thetasinner.web.model.EmptyJsonResponse;
import org.thetasinner.web.model.ReportModel;
import org.thetasinner.web.model.RequestBase;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class EBookTestClient {
  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ResourceHelper resourceHelper;

  @Autowired
  private UrlHelper urlHelper;

  @Bean
  public TestRestTemplate eBookRestTemplate() {
    var builder = new RestTemplateBuilder()
            .requestFactory(HttpComponentsClientHttpRequestFactory.class);
    return new TestRestTemplate(builder);
  }

  public ResponseEntity<List> getLibraries(int port) {
    return restTemplate.getForEntity(urlHelper.buildRequestUrl("/libraries", port), List.class, new EmptyRequest());
  }

  public ResponseEntity<String> createLibrary(String libraryName, int port) {
    var uriParams = new HashMap<String, String>();
    uriParams.put("name", libraryName);

    return restTemplate.postForEntity(urlHelper.buildRequestUrl("/libraries?name={name}", port), new EmptyRequest(), String.class, uriParams);
  }

  public ResponseEntity<String> uploadBook(String libraryName, String name, int port) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    var body = new LinkedMultiValueMap<String, Object>();
    body.add("name", libraryName);
    body.add("files", resourceHelper.getFileSystemResourceFromClasspath(name));

    var requestEntity = new HttpEntity<>(body, headers);

    return restTemplate.postForEntity(urlHelper.buildRequestUrl("/books/upload", port), requestEntity, String.class);
  }

  public ResponseEntity<EmptyJsonResponse> commitLibrary(String libraryName, boolean unload, int port) {
    var commitRequest = new CommitRequest();
    CommitLibrary commitLibrary = new CommitLibrary();
    commitLibrary.setLibraryName(libraryName);
    commitLibrary.setUnload(unload);
    commitRequest.getCommitLibraries().add(commitLibrary);
    return restTemplate.postForEntity(urlHelper.buildRequestUrl("/libraries/commit", port), commitRequest, EmptyJsonResponse.class);
  }

  public void deleteBook(String libraryName, String bookId, int port) {
    restTemplate.delete(urlHelper.buildRequestUrl("/books/{id}?name={name}", port), bookId, libraryName);
  }

  public ResponseEntity<Book> updateBook(JsonNode request, String bookId, String libraryName, int port) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    var gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();

    var requestEntity = new HttpEntity<>(request, headers);

    return restTemplate.exchange(urlHelper.buildRequestUrl("/books/{id}?libraryName={libraryName}", port), HttpMethod.PATCH, requestEntity, Book.class, bookId, libraryName);
  }

  public ResponseEntity<List<Book>> getBookList(String libraryName, int port) {
    var uriParams = new HashMap<String, String>();
    uriParams.put("name", libraryName);
    return restTemplate.exchange(
            urlHelper.buildRequestUrl("/books?name={name}", port),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Book>>() {
            },
            uriParams
    );
  }

  public ResponseEntity<byte[]> downloadBook(String libraryName, String bookId, int port) {
    var headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

    var entity = new HttpEntity<>(headers);

    return restTemplate.exchange(
            urlHelper.buildRequestUrl("/books/any-title-i-like?id={id}&name={name}", port),
            HttpMethod.GET, entity, byte[].class, bookId, libraryName);
  }

  public ResponseEntity<byte[]> downloadCover(String libraryName, String bookId, int port) {
    var headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

    var entity = new HttpEntity<>(headers);

    return restTemplate.exchange(
            urlHelper.buildRequestUrl("/books/{id}/covers?name={name}", port),
            HttpMethod.GET, entity, byte[].class, bookId, libraryName);
  }

  public ResponseEntity<Book> createBook(RequestBase<BookAddRequest> request, int port) {
    return restTemplate.postForEntity(urlHelper.buildRequestUrl("/books", port), request, Book.class);
  }

  public ResponseEntity<String> uploadCover(String libraryName, String bookId, int port) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    var body = new LinkedMultiValueMap<String, Object>();
    body.add("name", libraryName);
    body.add("cover", resourceHelper.getFileSystemResourceFromClasspath("test-ebook/cover.png"));

    var requestEntity = new HttpEntity<>(body, headers);

    return restTemplate.postForEntity(urlHelper.buildRequestUrl("/books/{id}/covers", port), requestEntity, String.class, bookId);
  }

  public ResponseEntity<EmptyJsonResponse> commitAllLibraries(int port) {
    var commitRequest = new CommitRequest();
    commitRequest.setCommitAll(true);
    commitRequest.setCommitAndUnloadAll(true);
    return restTemplate.postForEntity(urlHelper.buildRequestUrl("/libraries/commit", port), commitRequest, EmptyJsonResponse.class);
  }

  public ResponseEntity<ReportModel> getReport(String libraryName, int port) {
    return restTemplate.getForEntity(urlHelper.buildRequestUrl("/maintenance/report?libraryName={libraryName}", port), ReportModel.class, libraryName);
  }
}
