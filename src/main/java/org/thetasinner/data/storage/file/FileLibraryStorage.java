package org.thetasinner.data.storage.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.thetasinner.data.exception.EBookDataServiceException;
import org.thetasinner.data.model.Book;
import org.thetasinner.data.model.Library;
import org.thetasinner.data.model.TypedUrl;
import org.thetasinner.data.model.Video;
import org.thetasinner.data.storage.ILibraryStorage;
import org.thetasinner.data.storage.StorageException;
import org.thetasinner.data.storage.StorageResult;
import org.thetasinner.data.storage.VideoContent;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class FileLibraryStorage implements ILibraryStorage {
  private static final Logger LOG = LoggerFactory.getLogger(FileLibraryStorage.class);

  private final ObjectMapper mapper = new ObjectMapper();
  private final ReentrantLock createLibraryLock = new ReentrantLock();
  private String dataPath;

  private static final String COVER_FILE = "cover.png";

  public FileLibraryStorage() {

    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  @Value("${es.data.path:esdata}")
  private void setDataPath(String param) {
    if (param.charAt(param.length() - 1) != File.separatorChar) {
      param += File.separator;
    }
    dataPath = param;

    if (!Files.exists(Paths.get(dataPath))) {
      File f = new File(dataPath);
      if (!f.mkdirs()) {
        throw new IllegalStateException("The directory specified by es.data.path does not exist and cannot be created");
      }
    }
  }

  @Override
  public Library load(String name) {
    try {
      return loadLibraryFromFile(name);
    } catch (IOException e) {
      LOG.error(String.format("Failed to load library [%s}", name), e);
    }

    return null;
  }

  private Library loadLibraryFromFile(String libraryName) throws IOException {
    var libraryData = Files.readString(Paths.get(getLibraryPath(libraryName)));
    return mapper.readValue(libraryData, Library.class);
  }

  @Override
  public void save(String libraryName, Library library) {
    try (var writer = new FileWriter(getLibraryPath(libraryName), Charset.forName("UTF-8"))) {
      mapper.writeValue(writer, library);
    } catch (IOException e) {
      var msg = "Failed to save e-book library";
      LOG.error(msg, e);
      throw new EBookDataServiceException(msg, e);
    }
  }

  @Override
  public void create(String libraryName) {
    try {
      createLibraryLock.lock();

      createDirectoryForLibrary(libraryName);
      save(libraryName, new Library());
    } finally {
      createLibraryLock.unlock();
    }
  }

  private void createDirectoryForLibrary(String libraryName) {
    var libraryDirectory = getLibraryDirectory(libraryName);
    checkLibraryDirectoryNotAlreadyInUse(libraryDirectory, libraryName);
    createLibraryDirectory(libraryDirectory, libraryName);
  }

  private void createLibraryDirectory(String libraryDirectory, String libraryName) {
    var dir = new File(libraryDirectory);
    if (!dir.mkdirs()) {
      throw new EBookDataServiceException(String.format("Unable to create library directory for name [%s], " +
              "check this is a valid directory name and you have permission to createLibrary it", libraryName));
    }
  }

  private void checkLibraryDirectoryNotAlreadyInUse(String libraryDirectory, String libraryName) {
    Path libraryPath = Paths.get(libraryDirectory);
    if (Files.exists(libraryPath)) {
      throw new EBookDataServiceException(String.format("Will not create library new library with name [%s] because the name is already in use", libraryName));
    }
  }

  @Override
  public StorageResult store(String libraryName, MultipartFile file) throws StorageException {
    var fileName = validateUploadFile(file);

    UUID uuid = UUID.randomUUID();
    String storagePath = getLibraryDirectory(libraryName) + uuid + File.separator;
    if (!new File(storagePath).mkdirs()) {
      throw new StorageException(String.format("Could not createLibrary a directory to store the new file in [%s]", fileName));
    }

    String fileStoragePath = storagePath + fileName;

    try {
      Files.copy(file.getInputStream(), Paths.get(fileStoragePath));
    } catch (IOException e) {
      throw new StorageException("Error saving the file", e);
    }

    var result = new StorageResult();
    result.setId(uuid.toString());
    result.setFileName(fileName);
    result.setUrl(new TypedUrl(fileStoragePath, TypedUrl.Type.LocalManaged));
    return result;
  }

  @Override
  public Book recover(String libraryName, String id) {
    File[] files = Paths.get(getLibraryDirectory(libraryName), id).toFile().listFiles((dir, name) -> "pdf".equals(FilenameUtils.getExtension(name)));
    if (files == null || files.length != 1) {
      throw new IllegalStateException("Failed to recover book because no PDF was found");
    }

    File bookToRecover = files[0];

    Book book = new Book();
    book.setId(id);
    book.setUrl(new TypedUrl(bookToRecover.getPath(), TypedUrl.Type.LocalManaged));
    book.setTitle(bookToRecover.getName());
    return book;
  }

  @Override
  public void storeCover(String libraryName, String id, RenderedImage image) throws IOException {
    String storagePath = getLibraryDirectory(libraryName) + id;
    var outPath = Paths.get(storagePath, COVER_FILE);

    ImageIO.write(image, "PNG", outPath.toFile());
  }

  @Override
  public void deleteBook(String id, String name) throws IOException {
    FileUtils.deleteDirectory(Paths.get(getLibraryDirectory(name), id).toFile());
  }

  @Override
  public List<String> getLibraries() {
    List<String> libraries = new ArrayList<>();
    String libraryDirectory = getLibraryDirectory("");
    try (var paths = Files.newDirectoryStream(Paths.get(libraryDirectory))) {
      for (var path : paths) {
        var filePath = path.getFileName();
        if (filePath == null) continue;
        libraries.add(filePath.toString());
      }
    } catch (IOException e) {
      throw new EBookDataServiceException("Failed to get a list of library directories", e);
    }

    return libraries;
  }

  @Override
  public FileInputStream getBookInputStream(Book book) throws FileNotFoundException {
    TypedUrl typedUrl = book.getUrl();
    if (typedUrl.getType() != TypedUrl.Type.LocalManaged && typedUrl.getType() != TypedUrl.Type.LocalUnmanaged) {
      throw new EBookDataServiceException("Not a local book");
    }

    File file = new File(typedUrl.getValue());
    if (!file.canRead()) {
      throw new EBookDataServiceException("File is not accessible");
    }

    return new FileInputStream(file);
  }

  @Override
  public VideoContent getVideoInputStream(Video video) throws IOException {
    TypedUrl typedUrl = video.getUrl();
    if (typedUrl.getType() != TypedUrl.Type.LocalManaged && typedUrl.getType() != TypedUrl.Type.LocalUnmanaged) {
      throw new EBookDataServiceException("Not a local video");
    }

    File file = new File(typedUrl.getValue());
    if (!file.canRead()) {
      throw new EBookDataServiceException("Video file is not accessible");
    }

    var contentType = Files.probeContentType(file.toPath());

    VideoContent videoContent = new VideoContent();
    videoContent.setContentType(contentType);
    videoContent.setInputStream(new FileInputStream(file));
    return videoContent;
  }

  @Override
  public void storeCover(Book book, MultipartFile cover) throws StorageException {
    var originalFileName = validateUploadFile(cover);

    int lastDot = originalFileName.lastIndexOf('.');
    String fileName = "cover-" + UUID.randomUUID().toString() + originalFileName.substring(lastDot);

    Path path = Paths.get(book.getUrl().getValue());

    var storageFolder = path.getParent();
    if (storageFolder == null) {
      return;
    }
    Path savePath = Paths.get(storageFolder.toString(), fileName);

    try {
      Files.copy(cover.getInputStream(), savePath);
    } catch (IOException e) {
      throw new StorageException("Error saving the file", e);
    }

    book.getCovers().add(new TypedUrl(savePath.toString(), TypedUrl.Type.LocalManaged));
  }

  private String validateUploadFile(MultipartFile file) throws StorageException {
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
      throw new StorageException("Cannot get original filename from uploaded file.");
    }

    String originalFileName = org.springframework.util.StringUtils.cleanPath(originalFilename);
    if (originalFileName.contains("..") || originalFileName.contains("\\") || originalFileName.contains("/")) {
      // Security check to make sure a path embedded in the filename can't escape the storage location.
      // Or accidentally orphan itself by having a path the server can't find again.
      throw new StorageException("Won't store file which contains path component.");
    }

    return originalFilename;
  }

  @Override
  public FileInputStream getCoverInputStream(Book book) throws FileNotFoundException {
    TypedUrl typedUrl = book.getUrl();
    if (typedUrl.getType() != TypedUrl.Type.LocalManaged) {
      throw new EBookDataServiceException("Can only get cover for local managed book");
    }

    var bookPath = Paths.get(typedUrl.getValue());
    var bookFolder = bookPath.getParent();
    if (bookFolder == null) {
      return null;
    }
    File file = Paths.get(bookFolder.toString(), COVER_FILE).toFile();
    if (!file.canRead()) {
      throw new EBookDataServiceException("Cover file is not accessible or does not exist");
    }

    return new FileInputStream(file);
  }

  private String getLibraryPath(String name) {
    return getLibraryDirectory(name) + "library.json";
  }

  private String getLibraryDirectory(String name) {
    return dataPath + name + File.separator;
  }
}
