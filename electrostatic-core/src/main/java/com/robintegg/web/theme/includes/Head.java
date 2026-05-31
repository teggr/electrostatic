package com.robintegg.web.theme.includes;

import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.feed.FeedPlugin;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class Head {
  public static DomContent create(RenderModel renderModel) {
    return head(
        meta()
            .withCharset("utf-8"),
        meta()
            .attr("http-equiv", "X-UA-Compatible")
            .withContent("IE=edge"),
        meta().
            withName("viewport")
            .withContent("width=device-width, initial-scale=1"),
        SEO.render(renderModel),
        link().
            withRel("stylesheet")
            .withHref(Utils.relativeUrl("/css/main.css")),
        link().
            withRel("stylesheet")
            .withHref(Utils.relativeUrl("/css/style.css")),
        link()
            .withType("application/atom+xml")
            .withRel("alternate")
            .withHref(renderModel.getContext().getSite().resolveUrl("/" + FeedPlugin.INSTANCE.getFeed().getPath()))
            .withTitle(renderModel.getContext().getSite().getTitle())
    );
  }
}
