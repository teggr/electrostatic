package com.robintegg.web.content.book;

import com.robintegg.web.content.IndexContent;
import com.robintegg.web.content.IndexedContent;
import com.robintegg.web.content.TaggedContent;
import com.robintegg.web.engine.ContentItem;
import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.engine.RenderModel;
import j2html.TagCreator;
import j2html.tags.DomContent;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.each;

public class Book implements ContentItem, TaggedContent, IndexedContent {
  private final String key;
  @ToString.Exclude
  private final BookEntry bookEntry;
  private final String url;

  public Book(String key, BookEntry bookEntry) {
    this.key = key;
    this.bookEntry = bookEntry;
    this.url = "/books/" + key + ".html";
  }

  public String getUrl() {
    return url;
  }

  public String getTitle() {
    return bookEntry.getBookDetails().getTitle();
  }

  public String getSubtitle() {
    return bookEntry.getBookDetails().getSubtitle();
  }

  public List<String> getTags() {
    return bookEntry.getBookDetails().getTags();
  }

  public String getImageUrl() {
    return bookEntry.getBookDetails().getImageUrl();
  }

  public Map<String, List<String>> getData() {
    return Map.of(
        "layout", List.of("book"),
        "tags", getTags(),
        "title", List.of(getTitle()),
        "subtitle", List.of(getSubtitle()),
        "image", List.of(getImageUrl()),
        "authors", List.of(bookEntry.getBookDetails().getAuthor()),
        "date", List.of(bookEntry.getBookshelf().getAdded().toString())
    );
  }

  public DomContent getExcerpt(RenderModel renderModel) {
    return TagCreator.each();
  }

  public DomContent getContent(RenderModel renderModel) {
    return each(); //BookLayout.render(contentModel);
  }

  public LocalDate getAddedDate() {
    return bookEntry.getBookshelf().getAdded();
  }

  public String getAuthor() {
    return bookEntry.getBookDetails().getAuthor();
  }

  @Override
  public IndexContent getIndexContent() {
    return BookIndexedContent.map(this);
  }
}
