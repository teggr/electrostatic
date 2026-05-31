package run.electrostatic.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "electrostatic",
    mixinStandardHelpOptions = true,
    version = "0.0.1-SNAPSHOT",
    description = "Electrostatic static site generator",
    subcommands = {
        InitCommand.class,
        BuildCommand.class,
        ServeCommand.class
    }
)
public class ElectrostaticCli implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ElectrostaticCli()).execute(args);
        System.exit(exitCode);
    }

}
