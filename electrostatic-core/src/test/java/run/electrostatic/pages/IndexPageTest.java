package run.electrostatic.pages;

import run.electrostatic.engine.RenderModel;
import run.electrostatic.engine.Page;
import run.electrostatic.engine.Pageable;
import run.electrostatic.theme.pages.IndexPage;
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