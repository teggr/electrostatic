package electrostatic.theme.home.layouts;

import electrostatic.engine.RenderModel;
import electrostatic.engine.Layout;
import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class DefaultLayout {

    public static Layout create() {
        return Layout.builder()
                .renderFunction(DefaultLayout::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {

        return html()
                .with(
                        body(
                                renderModel.getContent()
                        )
                );

    }

}
