///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavenLocal
//DEPS dev.rebelcraft:cli:0.0.1-SNAPSHOT

import dev.rebelcraft.electrostatic.cli.ElectrostaticCli;

class Electrostatic {
    public static void main(String... args) {
        ElectrostaticCli.main(args);
    }
}
