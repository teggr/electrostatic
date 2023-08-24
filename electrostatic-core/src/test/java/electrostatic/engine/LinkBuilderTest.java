package electrostatic.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkBuilderTest {

  @Test
  void linkShouldBeSameWhenPathsMatch() {

    assertEquals("", LinkBuilder.link("/", "/"));
    assertEquals("", LinkBuilder.link("/index.html", "/index.html"));
    assertEquals("", LinkBuilder.link("/docs/index.html", "/docs/index.html"));

  }

  @Test
  void linkShouldBeCurrentWhenMovingFromPageToDirectory() {

    assertEquals(".", LinkBuilder.link("/index.html", "/"));
    assertEquals(".", LinkBuilder.link("/docs/index.html", "/docs"));
    assertEquals(".", LinkBuilder.link("/docs", "/"));
    assertEquals(".", LinkBuilder.link("/docs/more", "/docs"));

  }

  @Test
  void linkShouldBeRelativeDirToChildDir() {

    assertEquals("docs", LinkBuilder.link("/", "/docs"));
    assertEquals("more", LinkBuilder.link("/docs", "/docs/more"));

  }

  @Test
  void linkShouldBeUpForFiles() {

    assertEquals("../attribution.html", LinkBuilder.link("/docs/index.html", "/attribution.html"));
    assertEquals("../../attribution.html", LinkBuilder.link("/docs/more/index.html", "/attribution.html"));

  }

}