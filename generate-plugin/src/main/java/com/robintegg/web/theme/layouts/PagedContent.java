package com.robintegg.web.theme.layouts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class PagedContent<T> {

    private final List<T> content;
    private final int page;
    private final int pageSize;
    private final boolean next;
    private final int nextPage;
    private final String nextPagePath;
    private final boolean previous;
    private final int previousPage;
    private final String previousPagePath;
    private final int totalPages;

}
