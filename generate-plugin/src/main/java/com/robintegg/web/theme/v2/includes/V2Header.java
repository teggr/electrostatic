package com.robintegg.web.theme.v2.includes;

import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class V2Header {
  public static DomContent create(RenderModel renderModel) {
    return header()
        .withClass("site-header")
        .with(
            div()
                .withClass("wrapper")
                .with(
                    a()
                        .withClass("site-title")
                        .withRel("author")
                        .withHref(Utils.relativeUrl("/"))
                        .withText(Utils.escape(renderModel.getContext().getSite().getTitle())),
                    iff(
                        renderModel.getContentModel().getPages().size() > 0,
                        nav()
                            .withClass("site-nav")
                            .with(
                                input()
                                    .withType("checkbox")
                                    .withId("nav-trigger")
                                    .withClass("nav-trigger"),
                                label()
                                    .withFor("nav-trigger")
                                    .with(
                                        span()
                                            .withClass("menu-icon")
                                            .with(
                                                rawHtml("""
                                                    <svg viewBox="0 0 24 24" width="22px" height="22px" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                      <line x1="3" y1="6" x2="21" y2="6"/>
                                                      <line x1="3" y1="12" x2="21" y2="12"/>
                                                      <line x1="3" y1="18" x2="21" y2="18"/>
                                                    </svg>
                                                    """)
                                            )
                                    ),
                                div()
                                    .withClass("trigger")
                                    .with(
                                        each(renderModel.getContentModel().getPages(), myPage ->
                                            iff(
                                                myPage.isIncludeMenu(),
                                                a()
                                                    .withClass("page-link")
                                                    .withHref(Utils.relativeUrl(myPage.getUrl()))
                                                    .withText(Utils.escape(myPage.getTitle()))
                                            )
                                        ),
                                        a()
                                            .withClass("page-link")
                                            .withHref("#follow-me")
                                            .withText("Follow me")
                                    )
                            )
                    )
                )
        );
  }
}
