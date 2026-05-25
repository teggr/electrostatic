package com.robintegg.web.theme.includes;

import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class Footer {
  public static DomContent create(RenderModel renderModel) {
    return footer()
        .withClass("site-footer h-card")
        .with(
            data()
                .withClass("u-url")
                .attr("href", Utils.relativeUrl("/")),
            div()
                .withClass("wrapper")
                .with(
                    h2()
                        .withClass("footer-heading")
                        .withText(Utils.escape(renderModel.getContext().getSite().getTitle())),
                    div()
                        .withClass("footer-col-wrapper")
                        .with(
                            div()
                                .withClass("footer-col footer-col-1")
                                .with(
                                    ul()
                                        .withClass("contact-list")
                                        .with(
                                            li()
                                                .withClass("p-name")
                                                .with(
                                                    iffElse(
                                                        renderModel.getContext().getSite().getAuthor().getName() != null,
                                                        text(Utils.escape(renderModel.getContext().getSite().getAuthor().getName())),
                                                        text(Utils.escape(renderModel.getContext().getSite().getTitle()))
                                                    )
                                                ),
                                            iff(
                                                renderModel.getContext().getSite().getEmail() != null,
                                                li().with(
                                                    a()
                                                        .withClass("u-email")
                                                        .withHref("mailto:" + renderModel.getContext().getSite().getEmail())
                                                        .withText(renderModel.getContext().getSite().getEmail()))
                                            )
                                        )
                                ),
                            div()
                                .withClass("footer-col footer-col-2")
                                .with(
                                    div()
                                        .withClass("social-links")
                                        .withId("follow-me")
                                        .with(
                                            Social.create(renderModel))
                                ),
                            div()
                                .withClass("footer-col footer-col-3")
                                .with(
                                    p()
                                        .withText(Utils.escape(renderModel.getContext().getSite().getDescription()))
                                )

                        )
                )
        );
  }
}
