package io.github.teggr.electrostatic.theme.includes;

import io.github.teggr.electrostatic.site.PostHog;
import io.github.teggr.electrostatic.site.Site;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostHogTagCreatorTest {

    @Test
    void doesNotRenderWhenProjectKeyIsMissing() {
        assertNull(PostHogTagCreator.create(new Site()));
    }

    @Test
    void rendersPageviewOnlyPostHogScript() {
        Site site = new Site();
        site.setPosthog(new PostHog("phc_test", "https://eu.i.posthog.com"));

        String html = PostHogTagCreator.create(site).render().replace("&#39;", "'");

        assertTrue(html.contains("https://eu.i.posthog.com/static/array.js"));
        assertTrue(html.contains("posthog.init"));
        assertTrue(html.contains("phc_test"));
        assertTrue(html.contains("autocapture: false"));
        assertTrue(html.contains("disable_session_recording: true"));
        assertTrue(html.contains("person_profiles: 'identified_only'"));
    }

    @Test
    void rejectsNonHttpsApiHosts() {
        Site site = new Site();
        site.setPosthog(new PostHog("phc_test", "http://example.test"));

        assertThrows(IllegalArgumentException.class, () -> PostHogTagCreator.create(site));
    }
}
