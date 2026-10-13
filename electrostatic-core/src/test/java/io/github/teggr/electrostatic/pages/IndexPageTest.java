package io.github.teggr.electrostatic.pages;

import io.github.teggr.electrostatic.engine.RenderModel;
import io.github.teggr.electrostatic.engine.Page;
import io.github.teggr.electrostatic.engine.Pageable;
import io.github.teggr.electrostatic.theme.pages.IndexPage;
import j2html.TagCreator;
import org.junit.jupiter.api.Test;

class IndexPageTest {

    @Test
    void shouldRender() {

        Page page = IndexPage.create("index.html", new Pageable(1, 10));

        System.out.println(
                TagCreator.html(
                        page.getRenderFunction().apply(new RenderModel())
                ).render()
        );

    }

}