package com.robintegg.web.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void urlFromKey() {

        String urlFromKey = Utils.urlFromKey("2018-01-07-first-look-at-java-support-in-visual-studio-code");

        assertEquals("/2018/01/07/first-look-at-java-support-in-visual-studio-code.html", urlFromKey);

    }
}