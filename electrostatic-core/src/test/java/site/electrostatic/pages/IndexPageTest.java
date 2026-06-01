package site.electrostatic.pages;

import site.electrostatic.engine.RenderModel;
import site.electrostatic.engine.Page;
import site.electrostatic.engine.Pageable;
import site.electrostatic.theme.pages.IndexPage;
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