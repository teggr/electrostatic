package com.robintegg.web.engine;


import j2html.TagCreator;
import j2html.tags.DomContent;
import lombok.Data;

@Data
public class RenderModel {

  private Page page;
  // TODO: these values are more in scope of render time
  private DomContent content = TagCreator.text("");
  private Context context;
  private ContentModel contentModel;

  public void reset() {
    this.content = TagCreator.text("");
    this.page = null;
  }

}
