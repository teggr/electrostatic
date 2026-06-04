package site.electrostatic.utils;

import org.junit.jupiter.api.Test;
import site.electrostatic.core.GenerationOptions;
import site.electrostatic.core.GenerationOptionsContext;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void urlFromKey() {

        String urlFromKey = Utils.urlFromKey("2018-01-07-first-look-at-java-support-in-visual-studio-code");

        assertEquals("/2018/01/07/first-look-at-java-support-in-visual-studio-code.html", urlFromKey);

    }

    @Test
    void urlFromKeyWithoutDatePrefix() {

        String urlFromKey = Utils.urlFromKey("hello-world");

        assertEquals("/hello-world.html", urlFromKey);

    }

    @Test
    void relativeUrl_shouldPrefixRootRelativePathsWithBaseUrlSubpath() {
        GenerationOptionsContext.set(GenerationOptions.fromBaseUrl("https://teggr.github.io/ci-ready-maven/"));
        try {
            assertEquals("/ci-ready-maven/css/main.css", Utils.relativeUrl("/css/main.css"));
            assertEquals("/ci-ready-maven/", Utils.relativeUrl("/"));
        } finally {
            GenerationOptionsContext.clear();
        }
    }

    @Test
    void relativeUrl_shouldLeaveAlreadyAbsoluteUrlsUnchanged() {
        GenerationOptionsContext.set(GenerationOptions.fromBaseUrl("https://teggr.github.io/ci-ready-maven/"));
        try {
            assertEquals("https://cdn.example.test/style.css", Utils.relativeUrl("https://cdn.example.test/style.css"));
        } finally {
            GenerationOptionsContext.clear();
        }
    }
}