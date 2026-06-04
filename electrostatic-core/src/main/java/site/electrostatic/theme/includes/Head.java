package site.electrostatic.theme.includes;

import site.electrostatic.engine.RenderModel;
import site.electrostatic.feed.FeedPlugin;
import site.electrostatic.utils.Utils;
import j2html.tags.DomContent;

import java.util.Set;

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
        each(renderModel.getContentModel().getLocalCssPaths().stream()
            .filter(path -> !Set.of("/css/main.css", "/css/style.css").contains(path))
            .map(path -> link()
                .withRel("stylesheet")
                .withHref(Utils.relativeUrl(path)))
            .toArray(DomContent[]::new)),
        link()
            .withType("application/atom+xml")
            .withRel("alternate")
            .withHref(renderModel.getContext().getSite().resolveUrl("/" + FeedPlugin.INSTANCE.getFeed().getPath()))
            .withTitle(renderModel.getContext().getSite().getTitle())
    );
  }
}
