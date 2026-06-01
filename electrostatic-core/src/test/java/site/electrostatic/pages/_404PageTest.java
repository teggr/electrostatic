package site.electrostatic.pages;

import site.electrostatic.engine.RenderModel;
import site.electrostatic.engine.Page;
import site.electrostatic.theme.pages._404Page;
import j2html.TagCreator;
import org.junit.jupiter.api.Test;

class _404PageTest {

    @Test
    void shouldRender() {

        Page page = _404Page.create();

        System.out.println(
                TagCreator.html(
                        page.getRenderFunction().apply(new RenderModel())
                ).render()
        );

    }

}