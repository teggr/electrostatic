package electrostatic.theme.home.includes;

import electrostatic.engine.RenderModel;
import electrostatic.utils.Utils;
import j2html.tags.DomContent;

import static electrostatic.utils.j2html.SvgTagGenerator.svg;
import static electrostatic.utils.j2html.SvgTagGenerator.use;
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
                                    svg()
                                            .withClass("svg-icon")
                                            .with(
                                                    use()
                                                            .attr("xlink:href", "/images/minima-social-icons.svg#github")
                                            ),
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
                                    svg()
                                            .withClass("svg-icon")
                                            .with(
                                                    use()
                                                            .attr("xlink:href", "/images/minima-social-icons.svg#linkedin")
                                            ),
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
                                    svg()
                                            .withClass("svg-icon")
                                            .with(
                                                    use()
                                                            .attr("xlink:href", "/images/minima-social-icons.svg#twitter")
                                            ),
                                span()
                                    .withClass("username")
                                    .withText(Utils.escape(renderModel.getContext().getSite().getTwitterUsername()))
                            )
                    )
            )
        );
  }
}
