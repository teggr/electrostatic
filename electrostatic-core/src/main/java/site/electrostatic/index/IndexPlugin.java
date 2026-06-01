package site.electrostatic.index;

import site.electrostatic.content.IndexContent;
import site.electrostatic.content.IndexedContent;
import site.electrostatic.content.post.Post;
import site.electrostatic.engine.ContentItem;
import site.electrostatic.engine.ContentModelVisitor;
import site.electrostatic.engine.Pageable;
import site.electrostatic.plugins.AggregatorPlugin;
import site.electrostatic.plugins.Plugins;
import site.electrostatic.theme.layouts.PagedContent;
import site.electrostatic.theme.pages.IndexPage;
import site.electrostatic.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
public class IndexPlugin implements AggregatorPlugin {

    public static final IndexPlugin INSTANCE = new IndexPlugin();

    public static final IndexPlugin create() {
        return INSTANCE;
    }

    private List<IndexedContent> indexedContentList = new ArrayList<>();

    private final int pageSize = 10;

    @Override
    public void visit(ContentModelVisitor visitor) {

        int pages = indexedContentList.size() / pageSize;
        if (indexedContentList.size() % pageSize != 0) pages++;

        log.info("content={},pageSize={},pages={}", indexedContentList.size(), pageSize, pages);

        for (int page = 1; page <= pages; page++) {
            Pageable pageable = new Pageable(page, pageSize);
            visitor.page(IndexPage.create(Utils.getPathForPage(pageable.getPage()), pageable));
        }

    }

    @Override
    public void add(ContentItem contentItem) {

        if (contentItem instanceof Post ic) {

            indexedContentList.add(ic);

        }

    }

    public List<IndexContent> getIndexedContent() {
        return getSortedIndexedContent();
    }

    public Optional<IndexContent> getLatestIndexedContent() {
        return getSortedIndexedContent().stream().findFirst();
    }

    public void registerPlugins() {
        Plugins.aggregatorPlugins.add(this);
    }

    public PagedContent<IndexContent> getIndexedContent(Pageable pageable) {

        List<IndexContent> sortedContent = getSortedIndexedContent();

        int skip = (pageable.getPage() - 1) * pageable.getPageSize();
        int limit = pageable.getPageSize() != 0 ? pageSize : sortedContent.size();

        int totalPages = calculateTotalPages(sortedContent.size(), limit);

        int nextPage = pageable.getPage() + 1;
        int previousPage = pageable.getPage() - 1;

        return new PagedContent<IndexContent>(
            sortedContent.stream()
                        .skip(skip)
                        .limit(limit)
                        .toList(),
                pageable.getPage(),
                pageable.getPageSize(),
                nextPage <= totalPages,
                nextPage,
                Utils.getPathForPage(nextPage),
                pageable.getPage() > 1,
                previousPage,
                Utils.getPathForPage(previousPage),
                totalPages
        );

    }

    public PagedContent<IndexContent> getHomePagedContent(Pageable pageable) {

        List<IndexContent> remainingContent = getSortedIndexedContent().stream()
                .skip(1)
                .toList();

        int skip = (pageable.getPage() - 1) * pageable.getPageSize();
        int limit = pageable.getPageSize() != 0 ? pageSize : remainingContent.size();
        int totalPages = calculateTotalPages(remainingContent.size(), limit);

        int nextPage = pageable.getPage() + 1;
        int previousPage = pageable.getPage() - 1;

        return new PagedContent<>(
                remainingContent.stream()
                        .skip(skip)
                        .limit(limit)
                        .toList(),
                pageable.getPage(),
                pageable.getPageSize(),
                nextPage <= totalPages,
                nextPage,
                Utils.getPathForPage(nextPage),
                pageable.getPage() > 1,
                previousPage,
                Utils.getPathForPage(previousPage),
                totalPages
        );

    }

    private List<IndexContent> getSortedIndexedContent() {
        return indexedContentList.stream()
                .map(IndexedContent::getIndexContent)
                .sorted(Comparator.comparing(IndexContent::getDate).reversed())
                .toList();
    }

    private int calculateTotalPages(int contentSize, int limit) {
        if (limit == 0) {
            return 1;
        }

        int totalPages = contentSize / limit;
        if (contentSize % limit != 0) {
            totalPages++;
        }

        return Math.max(1, totalPages);
    }

}
