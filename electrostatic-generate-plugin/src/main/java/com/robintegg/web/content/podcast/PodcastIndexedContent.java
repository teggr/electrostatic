package com.robintegg.web.content.podcast;

import com.robintegg.web.content.IndexContent;
import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.engine.RenderModel;
import j2html.tags.DomContent;

import java.time.LocalDate;
import java.util.List;

class PodcastIndexedContent {
  public static IndexContent map(Podcast podcast) {
    return new IndexContent() {

      @Override
      public LocalDate getDate() {
        return podcast.getDate();
      }

      @Override
      public String getUrl() {
        return podcast.getUrl();
      }

      @Override
      public String getTitle() {
        return podcast.getTitle() + " [New Podcast]";
      }

      @Override
      public DomContent getExcerpt(RenderModel renderModel) {
        return podcast.getExcerpt(renderModel);
      }

      @Override
      public String getCategory() {
        return null;
      }

      @Override
      public DomContent getContent(RenderModel renderModel) {
        return podcast.getContent(renderModel);
      }

      @Override
      public List<String> getTags() {
        return podcast.getTags();
      }

      @Override
      public String getAuthor() {
        return null;
      }

      @Override
      public String getImage() {
        return null;
      }
    };
  }
}
