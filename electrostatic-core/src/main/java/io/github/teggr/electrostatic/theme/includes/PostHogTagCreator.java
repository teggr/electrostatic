package io.github.teggr.electrostatic.theme.includes;

import io.github.teggr.electrostatic.site.PostHog;
import io.github.teggr.electrostatic.site.Site;
import j2html.tags.DomContent;

import static j2html.TagCreator.script;

public class PostHogTagCreator {

    private static final String DEFAULT_API_HOST = "https://eu.i.posthog.com";

    public static DomContent create(Site site) {
        PostHog posthog = site.getPosthog();
        if (posthog == null || posthog.getProjectKey() == null || posthog.getProjectKey().isBlank()) {
            return null;
        }

        String apiHost = posthog.getApiHost();
        if (apiHost == null || apiHost.isBlank()) {
            apiHost = DEFAULT_API_HOST;
        }
        apiHost = apiHost.replaceAll("/+$", "");

        if (!apiHost.startsWith("https://")) {
            throw new IllegalArgumentException("PostHog API host must use HTTPS");
        }

        String onload = "posthog.init('" + escapeJs(posthog.getProjectKey()) + "', {"
                + "api_host: '" + escapeJs(apiHost) + "',"
                + "defaults: '2026-05-30',"
                + "person_profiles: 'identified_only',"
                + "autocapture: false,"
                + "disable_session_recording: true"
                + "});";

        return script()
                .withSrc(apiHost + "/static/array.js")
                .withAsync(true)
                .attr("onload", onload);
    }

    private static String escapeJs(String value) {
        return value.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
