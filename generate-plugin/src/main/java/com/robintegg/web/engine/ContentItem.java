package com.robintegg.web.engine;

import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

public interface ContentItem {

  Map<String, List<String>> getData();

  String getUrl();

  DomContent getContent(RenderModel renderModel);

}
