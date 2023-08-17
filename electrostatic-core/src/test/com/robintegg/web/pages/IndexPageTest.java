package com.robintegg.web.pages;

import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.Pageable;
import com.robintegg.web.theme.pages.IndexPage;
import j2html.TagCreator;
import org.junit.jupiter.api.Test;

class IndexPageTest {

    @Test
    void shouldRender() {

        Page page = IndexPage.create("index.html", new Pageable(1, 10));

        System.out.println(
                TagCreator.html(
                        page.getRenderFunction().apply(new ContentModel())
                ).render()
        );

    }

}