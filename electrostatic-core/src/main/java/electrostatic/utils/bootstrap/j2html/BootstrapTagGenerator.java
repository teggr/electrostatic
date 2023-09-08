package electrostatic.utils.bootstrap.j2html;

import j2html.tags.specialized.LinkTag;
import j2html.tags.specialized.MetaTag;
import j2html.tags.specialized.ScriptTag;

import static j2html.TagCreator.*;

public class BootstrapTagGenerator {

    public static MetaTag charset() {
        return meta()
                .attr("charset", "utf-8");
    }

    public static MetaTag viewPort() {
        return meta()
                .withName("viewport")
                .attr("content", "width=device-width, initial-scale=1");
    }

    public static LinkTag iconsStylesheet() {
        return link()
                .attr("rel", "stylesheet")
                .withHref("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css");
    }

    public static LinkTag minStylesheet() {
        return link()
                .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css")
                .attr("rel", "stylesheet")
                .attr("integrity", "sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9")
                .attr("crossorigin", "anonymous");
    }

    public static ScriptTag minBundle() {
        return script()
                .attr("src", "https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js")
                .attr("integrity", "sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm")
                .attr("crossorigin", "anonymous");
    }

}
