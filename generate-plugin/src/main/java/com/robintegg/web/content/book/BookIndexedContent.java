package com.robintegg.web.content.book;

import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.content.IndexContent;
import com.robintegg.web.engine.RenderModel;
import j2html.tags.DomContent;

import java.time.LocalDate;
import java.util.List;

public class BookIndexedContent {
  public static IndexContent map(Book book) {
    return new IndexContent() {
      @Override
      public LocalDate getDate() {
        return book.getAddedDate();
      }

      @Override
      public String getUrl() {
        return book.getUrl();
      }

      @Override
      public String getTitle() {
        return book.getTitle() + " [New Book]";
      }

      @Override
      public DomContent getExcerpt(RenderModel renderModel) {
        return book.getExcerpt(renderModel);
      }

      @Override
      public String getCategory() {
        return null;
      }

      @Override
      public DomContent getContent(RenderModel renderModel) {
        return book.getContent(renderModel);
      }

      @Override
      public List<String> getTags() {
        return book.getTags();
      }

      @Override
      public String getAuthor() {
        return book.getAuthor();
      }

      @Override
      public String getImage() {
        return book.getImageUrl();
      }
    };
  }
}
