package electrostatic.utils.googlefonts.j2html;

import j2html.tags.specialized.LinkTag;

import static j2html.TagCreator.link;

public class GoogleFontsTagGenerator {

    public static LinkTag fontsGoogleApis() {
        return link()
                .attr("rel", "preconnect")
                .withHref("https://fonts.googleapis.com");
    }

    public static LinkTag fontsGStatic() {
        return link()
                .attr("rel", "preconnect")
                .withHref("https://fonts.gstatic.com")
                .attr("crossorigin");
    }

    public static LinkTag fontStylesheet(String family) {
        return link()
                .withHref("https://fonts.googleapis.com/css2?family=" + family + "&display=swap")
                .attr("rel", "stylesheet");
    }

}
