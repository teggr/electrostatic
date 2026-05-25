package com.robintegg.web.pages;

import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.engine.Page;
import com.robintegg.web.theme.pages._404Page;
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