package com.robintegg.web.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSiteBuilderTest {

    @TempDir
    Path tempDir;

    @Test
    void cleanOutputDirectory_shouldRemoveExistingFilesAndFolders() throws Exception {
        Path outputDirectory = tempDir.resolve("target/site");
        Path stalePage = outputDirectory.resolve("page/6/index.html");
        Path cssFile = outputDirectory.resolve("css/theme.css");

        Files.createDirectories(stalePage.getParent());
        Files.createDirectories(cssFile.getParent());
        Files.writeString(stalePage, "stale");
        Files.writeString(cssFile, "body {}");

        assertTrue(Files.exists(stalePage));
        assertTrue(Files.exists(cssFile));

        WebSiteBuilder.cleanOutputDirectory(outputDirectory);

        assertFalse(Files.exists(outputDirectory));
    }
}