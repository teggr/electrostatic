package electrostatic.utils.prism.j2html;

import j2html.tags.specialized.LinkTag;
import j2html.tags.specialized.ScriptTag;

import static j2html.TagCreator.link;
import static j2html.TagCreator.script;

public class PrismTagGenerator {

    public static LinkTag tomorrowStylesheet() {
        return link()
                .attr("rel", "stylesheet")
                .withHref("https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism-tomorrow.min.css")
                .attr("integrity", "sha512-vswe+cgvic/XBoF1OcM/TeJ2FW0OofqAVdCZiEYkd6dwGXthvkSFWOoGGJgS2CW70VK5dQM5Oh+7ne47s74VTg==")
                .attr("crossorigin", "anonymous")
                .attr("referrerpolicy", "no-referrer");
    }

    public static ScriptTag minCore() {
        return script()
                .attr("src", "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-core.min.js")
                .attr("integrity", "sha512-9khQRAUBYEJDCDVP2yw3LRUQvjJ0Pjx0EShmaQjcHa6AXiOv6qHQu9lCAIR8O+/D8FtaCoJ2c0Tf9Xo7hYH01Q==")
                .attr("crossorigin", "anonymous")
                .attr("referrerpolicy", "no-referrer");
    }

    public static ScriptTag minAutoloader() {
        return script()
                .attr("src", "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/plugins/autoloader/prism-autoloader.min.js")
                .attr("integrity", "sha512-SkmBfuA2hqjzEVpmnMt/LINrjop3GKWqsuLSSB3e7iBmYK7JuWw4ldmmxwD9mdm2IRTTi0OxSAfEGvgEi0i2Kw==")
                .attr("crossorigin", "anonymous")
                .attr("referrerpolicy", "no-referrer");
    }

}
