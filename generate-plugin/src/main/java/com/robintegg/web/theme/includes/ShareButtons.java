package com.robintegg.web.theme.includes;

import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static j2html.TagCreator.*;

public class ShareButtons {
    
    public static DomContent create(RenderModel renderModel) {
        String pageUrl = renderModel.getContext().getSite().resolveUrl(renderModel.getPage().getUrl());
        String pageTitle = renderModel.getPage().getTitle();
        
        String encodedUrl = encodeValue(pageUrl);
        String encodedTitle = encodeValue(pageTitle);
        String encodedText = encodeValue(pageTitle + " " + pageUrl);
        
        // X (Twitter) intent URL
        String twitterUrl = "https://twitter.com/intent/tweet?url=" + encodedUrl + "&text=" + encodedTitle;
        
        // Bluesky intent URL
        String blueskyUrl = "https://bsky.app/intent/compose?text=" + encodedText;
        
        // Mastodon share (opens a simple form)
        String mastodonUrl = "https://mastodonshare.com/?url=" + encodedUrl + "&text=" + encodedTitle;
        
        return div()
                .withClass("share-buttons")
                .with(
                        h4().withText("Share this article"),
                        div()
                                .withClass("share-buttons-list")
                                .with(
                                        a()
                                                .withClass("share-button share-x")
                                                .withHref(twitterUrl)
                                                .withTarget("_blank")
                                                .withRel("noopener noreferrer")
                                                .attr("aria-label", "Share on X")
                                                .withText("Share on X"),
                                        a()
                                                .withClass("share-button share-mastodon")
                                                .withHref(mastodonUrl)
                                                .withTarget("_blank")
                                                .withRel("noopener noreferrer")
                                                .attr("aria-label", "Share on Mastodon")
                                                .withText("Share on Mastodon"),
                                        a()
                                                .withClass("share-button share-bluesky")
                                                .withHref(blueskyUrl)
                                                .withTarget("_blank")
                                                .withRel("noopener noreferrer")
                                                .attr("aria-label", "Share on Bluesky")
                                                .withText("Share on Bluesky"),
                                        button()
                                                .withClass("share-button share-native")
                                                .withId("native-share-button")
                                                .attr("aria-label", "Share via native dialog")
                                                .withText("Share")
                                                .withStyle("display: none;")
                                ),
                        rawHtml("""
                                <script>
                                  // Show native share button if Web Share API is supported
                                  if (navigator.share) {
                                    const btn = document.getElementById('native-share-button');
                                    if (btn) {
                                      btn.style.display = 'inline-block';
                                      btn.addEventListener('click', async () => {
                                        try {
                                          await navigator.share({
                                            title: '%s',
                                            url: '%s'
                                          });
                                        } catch (err) {
                                          // User cancelled or error occurred
                                        }
                                      });
                                    }
                                  }
                                </script>
                                """.formatted(
                                        escapeJavaScript(pageTitle),
                                        escapeJavaScript(pageUrl)
                                ))
                );
    }
    
    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
    
    private static String escapeJavaScript(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
