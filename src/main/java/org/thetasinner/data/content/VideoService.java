package org.thetasinner.data.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.model.Video;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;

import java.io.IOException;

@Service
public class VideoService {
  private static final Logger LOG = LoggerFactory.getLogger(BookService.class);

  private final LibraryService libraryService;

  private final ILibraryStorage libraryStorage;

  public VideoService(LibraryService libraryService, ILibraryStorage libraryStorage) {
    this.libraryService = libraryService;
    this.libraryStorage = libraryStorage;
  }

  public Video storeVideo(String libraryName, MultipartFile file) throws IOException, StorageException {
    LOG.trace("Storing video from file in library with name [{}]", libraryName);

    var storageResult = libraryStorage.store(libraryName, file);

    var video = new Video();
    video.setId(storageResult.getId());
    video.setUrl(storageResult.getUrl());
    video.setTitle(storageResult.getFileName());

    libraryService.getLibrary(libraryName).getItem().getVideos().add(video);

    return video;
  }
}
