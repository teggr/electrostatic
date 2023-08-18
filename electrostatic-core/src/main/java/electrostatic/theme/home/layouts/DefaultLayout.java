package electrostatic.theme.home.layouts;

import electrostatic.engine.Layout;
import electrostatic.engine.RenderModel;
import electrostatic.mermaid.MermaidJsTagCreator;
import electrostatic.theme.home.includes.Footer;
import electrostatic.theme.home.includes.Head;
import electrostatic.theme.home.includes.Header;
import electrostatic.theme.home.includes.PiwikPro;
import j2html.TagCreator;
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
