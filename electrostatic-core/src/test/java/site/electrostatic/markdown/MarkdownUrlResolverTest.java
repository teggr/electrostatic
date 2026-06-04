package site.electrostatic.markdown;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarkdownUrlResolverTest {

    private final MarkdownUrlResolver resolver = new MarkdownUrlResolver();

    @Test
    void resolveDestination_shouldKeepProtocolRelativeUrlUnchanged() {
        assertEquals("//cdn.example.com/lib.js", resolver.resolveDestination("//cdn.example.com/lib.js", "https://teggr.github.io/ci-ready-maven/", "/index.html"));
    }

    @Test
    void resolveDestination_shouldKeepAlreadyPrefixedPathUnchanged() {
        assertEquals("/ci-ready-maven/guides/index.html", resolver.resolveDestination("/ci-ready-maven/guides/index.html", "https://teggr.github.io/ci-ready-maven/", "/index.html"));
    }

    @Test
    void resolveDestination_shouldPrefixRootPath() {
        assertEquals("/ci-ready-maven/", resolver.resolveDestination("/", "https://teggr.github.io/ci-ready-maven/", "/index.html"));
    }

    @Test
    void resolveDestination_shouldPrefixRootRelativeDestination() {
        assertEquals("/ci-ready-maven/guides/index.html", resolver.resolveDestination("/guides/index.html", "https://teggr.github.io/ci-ready-maven/", "/plugins/plugin-overview.html"));
    }

    @Test
    void resolveDestination_shouldResolvePageRelativeDestinationAgainstPagePath() {
        assertEquals("/ci-ready-maven/plugins/docs-collection-plugin.html", resolver.resolveDestination("docs-collection-plugin.html", "https://teggr.github.io/ci-ready-maven/", "/plugins/plugin-overview.html"));
    }

    @Test
    void resolveDestination_shouldResolveParentRelativeDestinationAgainstPagePath() {
        assertEquals("/ci-ready-maven/guides/first-guide.html", resolver.resolveDestination("../guides/first-guide.html", "https://teggr.github.io/ci-ready-maven/", "/plugins/plugin-overview.html"));
    }

    @Test
    void resolveDestination_shouldKeepAnchorLinkUnchanged() {
        assertEquals("#quick-start", resolver.resolveDestination("#quick-start", "https://teggr.github.io/ci-ready-maven/", "/index.html"));
    }

    @Test
    void siteBasePath_shouldReturnNormalizedPathForValidBaseUrl() {
        assertEquals("/ci-ready-maven", resolver.siteBasePath("https://teggr.github.io/ci-ready-maven/"));
    }

    @Test
    void siteBasePath_shouldReturnEmptyForInvalidBaseUrl() {
        assertEquals("", resolver.siteBasePath("https://teggr.github.io/ci ready maven/"));
    }
}
