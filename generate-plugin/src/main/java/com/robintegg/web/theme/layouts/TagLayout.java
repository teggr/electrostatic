package com.robintegg.web.theme.layouts;

import com.robintegg.web.engine.Layout;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.tags.TagPlugin;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class TagLayout {

  public static Layout create() {
    return Layout.builder()
        .data(Map.of("layout", List.of("default")))
        .renderFunction(TagLayout::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {

    return div()
        .withClass("home")
        .with(
            h2()
                .withClass("post-list-heading")
                .withId(renderModel.getPage().getTag())
                .withText(Utils.capitalize(renderModel.getPage().getTag())),
            ul()
                .withClass("post-list")
                .with(
                    each(TagPlugin.INSTANCE.getTaggedContent(renderModel.getPage().getTag()), taggedContent -> {
                      return each(
                          span()
                              .withClass("post-meta")
                              .withText(Utils.format(taggedContent.getDate())),
                          h3()
                              .with(
                                  a()
                                      .withClass("post-link")
                                      .withHref(Utils.relativeUrl(taggedContent.getUrl()))
                                      .withText(Utils.escape(taggedContent.getTitle()))
                              ),
                          iff(
                              renderModel.getContext().getSite().showExcerpts(),
                              taggedContent.getExcerpt(renderModel)
                          )
                      );
                    })
                )
        );

  }

}
