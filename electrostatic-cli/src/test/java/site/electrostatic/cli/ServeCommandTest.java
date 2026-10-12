package site.electrostatic.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServeCommandTest {

    @Test
    void watchShouldBeOptInAndAppearInServeHelp() {
        CommandLine command = new CommandLine(new ServeCommand());
        assertFalse((boolean) command.getCommandSpec().findOption("--watch").getValue());
        command.parseArgs("--watch");
        assertTrue((boolean) command.getCommandSpec().findOption("--watch").getValue());

        StringWriter help = new StringWriter();
        command.setOut(new PrintWriter(help));
        assertEquals(0, command.execute("--help"));
        assertTrue(help.toString().contains("--watch"));
        assertTrue(help.toString().contains("automatically reload"));
    }
}
