package org.thetasinner.data.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.exception.EBookNotFoundException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Video;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;
import org.thetasinner.data.storage.StorageResult;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    long duration = getDuration(storageResult);

    var video = new Video();
    video.setId(storageResult.getId());
    video.setUrl(storageResult.getUrl());
    video.setTitle(storageResult.getFileName());
    video.setDuration(duration);

    libraryService.getLibrary(libraryName).getItem().getVideos().add(video);

    return video;
  }

  private long getDuration(StorageResult storageResult) {
    // Xuggler does not appear to work with OpenJDK 11. Find something else!
    return 0;
  }

  public Video getVideo(String libraryName, String id) {
    LOG.trace("Getting video with id [{}] from library [{}]", id, libraryName);

    var library = libraryService.getLibrary(libraryName).getItem();
    Optional<Video> video = library.getVideos()
            .stream()
            .filter(b -> id.equals(b.getId()))
            .findFirst();

    // TODO Find first is not necessarily safe to use if multiple videos were to have the same id.

    if (video.isPresent()) {
      return video.get();
    } else {
      throw new EBookNotFoundException("Video not found");
    }
  }

  public List<Video> getVideos(String libraryName) {
    LOG.trace("Getting videos for library [{}]", libraryName);

    var library = libraryService.getLibrary(libraryName).getItem();
    return library.getVideos();
  }

  public Video updateVideo(String id, String libraryName, String videoPatch) throws IOException, JsonPatchException {
    LOG.info("Updating video with id [{}] in library [{}]", id, libraryName);

    Video video = getVideo(libraryName, id);

    var mapper = new ObjectMapper();
    var patch = mapper.readValue(videoPatch, JsonPatch.class);
    var patchedNode = patch.apply(mapper.convertValue(video, JsonNode.class));
    var updatedVideo = mapper.convertValue(patchedNode, Video.class);
    putVideo(libraryName, updatedVideo);

    return updatedVideo;
  }

  private void putVideo(String libraryName, Video video) {
    LOG.trace("Putting video with id [{}] from library [{}]", video.getId(), libraryName);

    var library = libraryService.getLibrary(libraryName).getItem();

    Optional<Video> first = library.getVideos().stream().filter(b -> video.getId().equals(b.getId())).findFirst();
    if (!first.isPresent()) {
      throw new EBookNotFoundException("Missing video for put operation");
    }

    var index = library.getVideos().indexOf(first.get());
    library.getVideos().set(index, video);
  }
}
