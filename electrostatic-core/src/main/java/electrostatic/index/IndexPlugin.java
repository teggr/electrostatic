package electrostatic.index;

import electrostatic.content.IndexContent;
import electrostatic.content.IndexedContent;
import electrostatic.engine.ContentItem;
import electrostatic.engine.ContentModelVisitor;
import electrostatic.engine.Pageable;
import electrostatic.plugins.AggregatorPlugin;
import electrostatic.plugins.Plugins;
import electrostatic.theme.home.layouts.PagedContent;
import electrostatic.theme.home.pages.IndexPage;
import electrostatic.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
                Utils.getPathForPage(nextPage),
                pageable.getPage() > 1,
                previousPage,
                Utils.getPathForPage(previousPage),
                totalPages
        );

    }

}
