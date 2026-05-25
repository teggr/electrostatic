package com.robintegg.web.content;

import com.robintegg.web.engine.ContentItem;
import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.engine.RenderModel;
import j2html.tags.DomContent;

import java.time.LocalDate;
import java.util.List;

public interface IndexContent {
  
  LocalDate getDate();

  String getUrl();

  String getTitle();

  DomContent getExcerpt(RenderModel renderModel);

  String getCategory();

  DomContent getContent(RenderModel renderModel);

  List<String> getTags();

  String getAuthor();

  String getImage();

}
