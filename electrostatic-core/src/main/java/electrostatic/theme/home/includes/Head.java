package electrostatic.theme.home.includes;

import electrostatic.engine.RenderModel;
import electrostatic.utils.Utils;
import electrostatic.feed.FeedPlugin;
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
            .withHref(FeedPlugin.INSTANCE.getFeed().getPath()) // TODO: need to make available via the theme and plugins
            .withTitle(renderModel.getContext().getSite().getTitle())
    );
  }
}
