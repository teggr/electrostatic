package com.robintegg.web.content.staticfiles;

import com.robintegg.web.engine.ContentItem;
import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.engine.RenderModel;
import j2html.TagCreator;
import j2html.tags.DomContent;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ToString
public class StaticFile implements ContentItem {

  private final String key;
  private final Map<String, List<String>> data;
  @ToString.Exclude
  private Function<RenderModel, byte[]> renderFunction;

  public StaticFile(String key, Map<String, List<String>> data, byte[] content) {
    this.key = key;
    this.data = data;
    this.renderFunction = (contentModel -> content);
  }

  public StaticFile(String key, Map<String, List<String>> data, Function<RenderModel, byte[]> renderFunction) {
    this.key = key;
    this.data = data;
    this.renderFunction = renderFunction;
  }

  public String getPath() {
    return key;
  }

  public Function<RenderModel, byte[]> getRenderFunction() {
    return renderFunction;
  }

  @Override
  public DomContent getContent(RenderModel renderModel) {
    return TagCreator.each();
  }

  @Override
  public String getUrl() {
    return getPath();
  }

  @Override
  public Map<String, List<String>> getData() {
    return data;
  }
}
