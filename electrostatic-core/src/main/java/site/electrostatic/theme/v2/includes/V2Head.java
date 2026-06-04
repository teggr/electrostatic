package site.electrostatic.theme.v2.includes;

import site.electrostatic.engine.RenderModel;
import site.electrostatic.feed.FeedPlugin;
import site.electrostatic.theme.includes.SEO;
import site.electrostatic.utils.Utils;
import j2html.tags.DomContent;

import java.util.Set;

import static j2html.TagCreator.*;

public class V2Head {
  public static DomContent create(RenderModel renderModel) {
    return head(
        meta()
            .withCharset("utf-8"),
        meta()
            .attr("http-equiv", "X-UA-Compatible")
            .withContent("IE=edge"),
        meta()
            .withName("viewport")
            .withContent("width=device-width, initial-scale=1"),
        SEO.render(renderModel),
        // Google Fonts: Inter (body) + Playfair Display (headings)
        link()
            .withRel("preconnect")
            .withHref("https://fonts.googleapis.com"),
        link()
            .withRel("preconnect")
            .withHref("https://fonts.gstatic.com")
            .attr("crossorigin"),
        link()
            .withRel("stylesheet")
            .withHref("https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Playfair+Display:ital,wght@0,700;0,800;1,700&display=swap"),
        link()
            .withRel("stylesheet")
            .withHref(Utils.relativeUrl("/css/theme.css")),
        each(renderModel.getContentModel().getLocalCssPaths().stream()
            .filter(path -> !Set.of("/css/theme.css").contains(path))
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
