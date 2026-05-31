package com.robintegg.web.theme.v2.layouts;

import com.robintegg.web.content.IndexContent;
import com.robintegg.web.engine.Layout;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.github.GitHubRepository;
import com.robintegg.web.github.GithubActivityPlugin;
import com.robintegg.web.index.IndexPlugin;
import com.robintegg.web.theme.layouts.PagedContent;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class V2HomeLayout {

  public static Layout create() {
    return Layout.builder()
        .data(Map.of("layout", List.of("default")))
        .renderFunction(V2HomeLayout::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {

    PagedContent<IndexContent> indexedContent = IndexPlugin.INSTANCE.getHomePagedContent(renderModel.getPage().getPageable());
    IndexContent featuredContent = IndexPlugin.INSTANCE.getLatestIndexedContent().orElse(null);

    List<GitHubRepository> activeRepos = GithubActivityPlugin.INSTANCE.getActiveRepositories();

    return div()
        .withClass("home")
        .with(
            iff(
                renderModel.getPage().getPageable().getPage() == 1 && featuredContent != null,
                renderFeaturedPost(featuredContent, renderModel)
            ),
            // GitHub Activity
            iff(
                activeRepos != null && !activeRepos.isEmpty(),
                each(
                    h2()
                        .withClass("github-activity-heading")
                        .withText("Latest GitHub Activity"),
                    div()
                        .withClass("github-activity-container")
                        .with(
                            each(activeRepos, repo ->
                                div()
                                    .withClass("github-activity-box")
                                    .with(
                                        h3()
                                            .withClass("github-repo-name")
                                            .with(
                                                a()
                                                    .withHref(repo.getHtmlUrl())
                                                    .withTarget("_blank")
                                                    .withRel("noopener noreferrer")
                                                    .withText(repo.getName())
                                            ),
                                        iff(
                                            repo.getDescription() != null && !repo.getDescription().isEmpty(),
                                            p()
                                                .withClass("github-repo-description")
                                                .withText(repo.getDescription())
                                        )
                                    )
                            )
                        )
                )
            ),
            // Posts section
            iff(
                indexedContent.getContent().size() > 0,
                ul()
                    .withClass("post-list")
                    .with(
                        each(indexedContent.getContent(), indexContent ->
                            li()
                                .with(renderPostCard(indexContent, renderModel))
                        )
                    )
            ),
            iff(
                indexedContent.getTotalPages() > 1,
                div()
                    .withClass("pagination")
                    .with(
                        iffElse(
                            indexedContent.isPrevious(),
                            a()
                                .withHref(Utils.relativeUrl(indexedContent.getPreviousPagePath()))
                                .withRel("previous")
                                .withClass("previous")
                                .withText("← Newer"),
                            span()
                                .withClass("previous")
                                .withText("← Newer")
                        ),
                        span()
                            .withClass("page_number")
                            .withText(String.format("Page %s of %s", indexedContent.getPage(), indexedContent.getTotalPages())),
                        iffElse(
                            indexedContent.isNext(),
                            a()
                                .withHref(Utils.relativeUrl(indexedContent.getNextPagePath()))
                                .withRel("next")
                                .withClass("next")
                                .withText("Older →"),
                            span()
                                .withClass("next")
                                .withText("Older →")
                        )
                    )
            )
        );

  }

  private static DomContent renderFeaturedPost(IndexContent indexContent, RenderModel renderModel) {
    String categoryLabel = getCategoryLabel(indexContent);

    return article()
        .withClass("featured-post")
        .with(
            iffElse(
                indexContent.getImage() != null && !indexContent.getImage().isEmpty(),
                a()
                    .withClass("featured-post-image-link")
                    .withHref(Utils.relativeUrl(indexContent.getUrl()))
                    .with(
                        img()
                            .withClass("featured-post-image")
                            .withSrc(Utils.relativeUrl(indexContent.getImage()))
                            .withAlt(Utils.escape(indexContent.getTitle()))
                    ),
                div()
                    .withClass("featured-post-image-link featured-post-image-fallback")
                    .with(
                        span()
                            .withClass("featured-post-fallback-label")
                            .withText("Latest work")
                    )
            ),
            div()
                .withClass("featured-post-body")
                .with(
                    span()
                        .withClass("featured-post-kicker")
                        .withText("Featured"),
                    iff(
                        categoryLabel != null,
                        span()
                            .withClass("featured-post-category")
                            .withText(Utils.escape(categoryLabel))
                    ),
                    a()
                        .withClass("featured-post-title")
                        .withHref(Utils.relativeUrl(indexContent.getUrl()))
                        .withText(Utils.escape(indexContent.getTitle())),
                    div()
                        .withClass("featured-post-excerpt")
                        .with(indexContent.getExcerpt(renderModel)),
                    div()
                        .withClass("featured-post-footer")
                        .with(
                            time()
                                .withClass("featured-post-date")
                                .withText(Utils.format(indexContent.getDate())),
                            a()
                                .withClass("featured-post-link")
                                .withHref(Utils.relativeUrl(indexContent.getUrl()))
                                .withText("Read article →")
                        )
                )
        );
  }

  private static DomContent renderPostCard(IndexContent indexContent, RenderModel renderModel) {
    String categoryLabel = getCategoryLabel(indexContent);

    // Remaining tags (after the first, which is used as category label)
    List<String> remainingTags = indexContent.getTags().size() > 1
        ? indexContent.getTags().subList(1, indexContent.getTags().size())
        : List.of();

    return article()
        .withClass("post-card")
        .with(
            // Image at top of card
            iff(
                indexContent.getImage() != null && !indexContent.getImage().isEmpty(),
                a()
                    .withClass("post-card-image-link")
                    .withHref(Utils.relativeUrl(indexContent.getUrl()))
                    .with(
                        img()
                            .withClass("post-card-image")
                            .withSrc(Utils.relativeUrl(indexContent.getImage()))
                            .withAlt(Utils.escape(indexContent.getTitle()))
                    )
            ),
            // Card body
            div()
                .withClass("post-card-body")
                .with(
                    // Category label
                    iff(
                        categoryLabel != null,
                        span()
                            .withClass("post-card-category")
                            .withText(Utils.escape(categoryLabel))
                    ),
                    // Title
                    a()
                        .withClass("post-card-title")
                        .withHref(Utils.relativeUrl(indexContent.getUrl()))
                        .withText(Utils.escape(indexContent.getTitle())),
                    // Excerpt
                    iff(
                        renderModel.getContext().getSite().showExcerpts() && !"reading-list".equals(indexContent.getCategory()),
                        div()
                            .withClass("post-card-excerpt")
                            .with(indexContent.getExcerpt(renderModel))
                    ),
                    iff(
                        "reading-list".equals(indexContent.getCategory()),
                        div()
                            .withClass("post-card-excerpt")
                            .with(indexContent.getContent(renderModel))
                    ),
                    // Remaining tags
                    iff(
                        !remainingTags.isEmpty(),
                        ul()
                            .withClass("post-card-tags")
                            .with(
                                each(remainingTags, tag ->
                                    li().with(
                                        a()
                                            .withHref(Utils.relativeUrl("/tags/" + tag))
                                            .withText(Utils.escape(tag))
                                    )
                                )
                            )
                    ),
                    // Footer: date
                    div()
                        .withClass("post-card-footer")
                        .with(
                            time()
                                .withClass("post-card-date")
                                .withText(Utils.format(indexContent.getDate()))
                        )
                )
        );
  }

    private static String getCategoryLabel(IndexContent indexContent) {
        return indexContent.getCategory() != null && !indexContent.getCategory().isEmpty()
            ? indexContent.getCategory()
            : (!indexContent.getTags().isEmpty() ? indexContent.getTags().get(0) : null);
      }

}
