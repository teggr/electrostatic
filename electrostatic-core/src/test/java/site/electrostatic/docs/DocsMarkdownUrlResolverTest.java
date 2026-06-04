package site.electrostatic.docs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocsMarkdownUrlResolverTest {

    @Test
    void resolve_shouldKeepProtocolRelativeUrlUnchanged() {
        assertEquals("//cdn.example.com/lib.js", DocsMarkdownUrlResolver.resolve("//cdn.example.com/lib.js", "/ci-ready-maven"));
    }

    @Test
    void resolve_shouldKeepAlreadyPrefixedPathUnchanged() {
        assertEquals("/ci-ready-maven/guides/index.html", DocsMarkdownUrlResolver.resolve("/ci-ready-maven/guides/index.html", "/ci-ready-maven"));
    }

    @Test
    void resolve_shouldPrefixRootPath() {
        assertEquals("/ci-ready-maven/", DocsMarkdownUrlResolver.resolve("/", "/ci-ready-maven"));
    }

    @Test
    void resolve_shouldReplaceTokenAndPrefixPath() {
        assertEquals("/ci-ready-maven/plugins/index.html", DocsMarkdownUrlResolver.resolve("{{site.baseurl}}/plugins/index.html", "/ci-ready-maven"));
    }

    @Test
    void resolve_shouldPrefixRootRelativeDestination() {
        assertEquals("/ci-ready-maven/guides/index.html", DocsMarkdownUrlResolver.resolve("/guides/index.html", "/ci-ready-maven"));
    }

    @Test
    void docsBasePath_shouldReturnNormalizedPathForValidBaseUrl() {
        assertEquals("/ci-ready-maven", DocsMarkdownUrlResolver.docsBasePath("https://teggr.github.io/ci-ready-maven/"));
    }

    @Test
    void docsBasePath_shouldReturnEmptyForInvalidBaseUrl() {
        assertEquals("", DocsMarkdownUrlResolver.docsBasePath("https://teggr.github.io/ci ready maven/"));
    }
}
