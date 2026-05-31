package com.robintegg.web.engine;

import j2html.tags.DomContent;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Builder
@Getter
public class Layout {

  private final Map<String, List<String>> data;
  private final Function<RenderModel, DomContent> renderFunction;

}
