package run.electrostatic.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThemesCommandTest {

    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreStdOut() {
        System.setOut(originalOut);
    }

    @Test
    void execute_shouldPrintAvailableThemes() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        int exitCode = new CommandLine(new ThemesCommand()).execute();

        assertEquals(0, exitCode);
        assertEquals("default" + System.lineSeparator() + "docs" + System.lineSeparator() + "v2" + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }
}