package com.robintegg.web.content.feed;

import com.robintegg.web.content.TaggedContent;
import com.robintegg.web.engine.ContentItem;
import com.robintegg.web.engine.RenderModel;
import j2html.tags.DomContent;
import lombok.ToString;
import org.commonmark.node.Node;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class FeedSubscription implements ContentItem, TaggedContent {

  private final String key;
  private final Map<String, List<String>> data;
  @ToString.Exclude
  private final Node document;
  private final String url;

  public FeedSubscription(String key, Map<String, List<String>> data, Node document) {
    this.key = key;
    this.data = data;
    this.document = document;
    this.url = "/feeds/" + key + ".html";
  }

  public List<String> getTags() {
    return data.getOrDefault("tags", Collections.emptyList());
  }

  public Map<String, List<String>> getData() {
    return data;
  }

  public String getUrl() {
    return url;
  }

  public String getTitle() {
    List<String> stringList = data.get("title");
    if (stringList == null || stringList.isEmpty()) {
      return "";
    }
    return stringList.get(0);
  }

  public String getDescription() {
    List<String> stringList = data.get("description");
    if (stringList == null || stringList.isEmpty()) {
      return "";
    }
    return stringList.get(0);
  }

  public String getThumbnail() {
    List<String> stringList = data.get("thumbnail");
    if (stringList == null || stringList.isEmpty()) {
      return null;
    }
    return stringList.get(0);
  }

  public String getSiteUrl() {
    List<String> stringList = data.get("site_url");
    if (stringList == null || stringList.isEmpty()) {
      return null;
    }
    return stringList.get(0);
  }

  public String getRssUrl() {
    List<String> stringList = data.get("rss_url");
    if (stringList == null || stringList.isEmpty()) {
      return null;
    }
    return stringList.get(0);
  }

  public LocalDate getDate() {
    List<String> dates = data.get("date");
    if (dates == null || dates.isEmpty()) {
      return LocalDate.MAX;
    }
    return LocalDate.parse(dates.get(0));
  }

  public DomContent getExcerpt(RenderModel renderModel) {
    return each();
  }

  public DomContent getContent(RenderModel renderModel) {
    return each();
  }
}
