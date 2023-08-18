package electrostatic.theme.home.includes;

import electrostatic.engine.RenderModel;
import electrostatic.utils.Utils;
import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class Social {
  public static DomContent create(RenderModel renderModel) {
    return ul()
        .withClass("social-media-list")
        .with(
            iff(
                renderModel.getContext().getSite().getGithubUsername() != null,
                li()
                    .with(
                        a()
                            .withHref(Utils.escape("https://github.com/" + renderModel.getContext().getSite().getGithubUsername()))
                            .with(
                                rawHtml("<svg class=\"svg-icon\"><use xlink:href=\"/images/minima-social-icons.svg#github\"></use></svg>"),
                                span()
                                    .withClass("username")
                                    .withText(Utils.escape(renderModel.getContext().getSite().getGithubUsername()))
                            )
                    )
            ),
            iff(
                renderModel.getContext().getSite().getLinkedinUsername() != null,
                li()
                    .with(
                        a()
                            .withHref(Utils.escape("https://www.linkedin.com/in/" + renderModel.getContext().getSite().getLinkedinUsername()))
                            .with(
                                rawHtml("<svg class=\"svg-icon\"><use xlink:href=\"/images/minima-social-icons.svg#linkedin\"></use></svg>"),
                                span()
                                    .withClass("username")
                                    .withText(Utils.escape(renderModel.getContext().getSite().getLinkedinUsername()))
                            )
                    )
            ),
            iff(
                renderModel.getContext().getSite().getTwitterUsername() != null,
                li()
                    .with(
                        a()
                            .withHref(Utils.escape("https://www.twitter.com/" + renderModel.getContext().getSite().getTwitterUsername()))
                            .with(
                                rawHtml("<svg class=\"svg-icon\"><use xlink:href=\"/images/minima-social-icons.svg#twitter\"></use></svg>"),
                                span()
                                    .withClass("username")
                                    .withText(Utils.escape(renderModel.getContext().getSite().getTwitterUsername()))
                            )
                    )
            )
        );
  }
}
