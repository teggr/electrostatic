package com.robintegg.web.content.book;

import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.plugins.ContentTypePlugin;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.site.Site;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
public class BookPlugin implements ContentTypePlugin {

  public static BookPlugin create() {
    return new BookPlugin();
  }

  private static JAXBContext jaxbContext = null;

  static {
    try {
      jaxbContext = JAXBContext.newInstance(BookEntry.class);
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  @SneakyThrows
  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {
    // load books from folder with xml
    var booksDirectory = sourceDirectory.resolve("_books");
    log.info("books directory: " + booksDirectory.toAbsolutePath());

    try (Stream<Path> paths = Files.walk(booksDirectory)) {
      paths
          .filter(Files::isRegularFile)
          .peek(f -> log.info("{}", f))
          .map(BookPlugin::readBook)
          .forEach(contentModel::add);
    }

  }

  private static Book readBook(Path path) {

    try {
      // Extract filename, filename without extension, and extension using Path methods
      String filename = path.getFileName().toString();
      int dotIndex = filename.lastIndexOf('.');
      String filenameWithoutExtension = (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
      String fileExtension = (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);

      if (fileExtension.equals("xml")) {

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        BookEntry book = (BookEntry) unmarshaller.unmarshal(Files.newInputStream(path));

        return new Book(filenameWithoutExtension, book);

      }

      return null;

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public void registerPlugins() {
    Plugins.contentTypePlugins.add(this);
  }

}
