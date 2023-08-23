package electrostatic.engine;


import j2html.TagCreator;
import j2html.tags.DomContent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Data
public class RenderModel {

    private Page page;
    // TODO: these values are more in scope of render time
    private DomContent content = TagCreator.text("");
    private Context context;
    private ContentModel contentModel;

    public void reset() {
        this.content = TagCreator.text("");
        this.page = null;
    }

    public String linkTo(String url) {

        if (url.startsWith("http")) {
            return url;
        } else if (url.startsWith("#")) {
            return url;
        }

        // what is our url
        // what do we need to adjust
        // default to relative to location
        Path targetUrl = Paths.get(url);
        String pageUrl = page.getUrl();
        if(!pageUrl.startsWith("/")) {
            pageUrl = "/" + pageUrl;
        }
        Path currentUrl = Paths.get(pageUrl);

        log.info("current {}, target {}", currentUrl, targetUrl);

        Path relativize = currentUrl.getParent().relativize(targetUrl);

        log.info("rel {}", relativize);

        //context.getSite().getUrl();
        // context.getSite().getBaseUrl();

        String string = relativize.toString();
        if(string.isBlank()) {
            string = "#";
        }

        string = string.replaceAll("\\\\","/");

        return string;

    }
}
