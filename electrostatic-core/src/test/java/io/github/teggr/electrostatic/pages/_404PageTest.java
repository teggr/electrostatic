package io.github.teggr.electrostatic.pages;

import io.github.teggr.electrostatic.engine.RenderModel;
import io.github.teggr.electrostatic.engine.Page;
import io.github.teggr.electrostatic.theme.pages._404Page;
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