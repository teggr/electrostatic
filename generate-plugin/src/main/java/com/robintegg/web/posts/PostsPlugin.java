package com.robintegg.web.posts;

import com.robintegg.web.content.IndexContent;
import com.robintegg.web.content.IndexedContent;
import com.robintegg.web.engine.ContentItem;
import com.robintegg.web.engine.ContentModelVisitor;
import com.robintegg.web.engine.Pageable;
import com.robintegg.web.plugins.AggregatorPlugin;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.theme.layouts.PagedContent;
import com.robintegg.web.theme.pages.PostsPage;
import com.robintegg.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class PostsPlugin implements AggregatorPlugin {

    public static final PostsPlugin INSTANCE = new PostsPlugin();

    public static final PostsPlugin create() {
        return INSTANCE;
    }

    private List<IndexedContent> indexedContentList = new ArrayList<>();

    private final int pageSize = 10;

    @Override
    public void visit(ContentModelVisitor visitor) {

        int pages = indexedContentList.size() / pageSize;
        if (indexedContentList.size() % pageSize != 0) pages++;

        log.info("posts content={},pageSize={},pages={}", indexedContentList.size(), pageSize, pages);

        // Start from page 2 since page 1 is added as a static page in DefaultThemePlugin
        for (int page = 2; page <= pages; page++) {
            Pageable pageable = new Pageable(page, pageSize);
            visitor.page(PostsPage.create(getPathForPostsPage(pageable.getPage()), pageable));
        }

    }

    @Override
    public void add(ContentItem contentItem) {

        if (contentItem instanceof IndexedContent ic) {

            indexedContentList.add(ic);

        }

    }

    public List<IndexContent> getIndexedContent() {
        return indexedContentList.stream()
                .map(IndexedContent::getIndexContent)
                .sorted(Comparator.comparing(IndexContent::getDate).reversed())
                .toList();
    }

    public void registerPlugins() {
        Plugins.aggregatorPlugins.add(this);
    }

    public PagedContent<IndexContent> getIndexedContent(Pageable pageable) {

        int skip = (pageable.getPage() - 1) * pageable.getPageSize();
        int limit = pageable.getPageSize() != 0 ? pageSize : indexedContentList.size();

        int totalPages = indexedContentList.size() / limit;
        if (indexedContentList.size() % limit != 0) totalPages++;

        int nextPage = pageable.getPage() + 1;
        int previousPage = pageable.getPage() - 1;

        return new PagedContent<IndexContent>(
                indexedContentList.stream()
                        .map(IndexedContent::getIndexContent)
                        .sorted(Comparator.comparing(IndexContent::getDate).reversed())
                        .skip(skip)
                        .limit(limit)
                        .toList(),
                pageable.getPage(),
                pageable.getPageSize(),
                nextPage <= totalPages,
                nextPage,
                getPathForPostsPage(nextPage),
                pageable.getPage() > 1,
                previousPage,
                getPathForPostsPage(previousPage),
                totalPages
        );

    }

    private String getPathForPostsPage(int pageForPath) {
        if (pageForPath == 1) {
            return "/posts/index.html";
        } else {
            return "/posts/page/" + pageForPath + "/index.html";
        }
    }

}
