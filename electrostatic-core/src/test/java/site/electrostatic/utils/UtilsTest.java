package site.electrostatic.utils;

import org.junit.jupiter.api.Test;

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
}