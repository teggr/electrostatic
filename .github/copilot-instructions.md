
# Module Boundaries And Entrypoints

- Keep shared generation behavior in core. Add or change behavior there first, then adapt wrappers.
- CLI and Maven plugin should share underlying code paths through core services and models, not through direct dependencies on each other.
- Do not add CLI as a dependency of the Maven plugin or Maven plugin as a dependency of the CLI.
- When introducing a user-facing feature available in both wrappers, implement once in core and wire it in both wrappers using wrapper-specific UX only.

# JBang Contract

- Keep Electrostatic.java as a thin launcher that delegates to ElectrostaticCli.main(args).
- Keep jbang-catalog.json alias electrostatic pointing to script-ref Electrostatic.java.
- When CLI artifact coordinates or project version change, update the //DEPS line in Electrostatic.java to match the current CLI artifact/version.

# Quick Verification

- Build wrappers with shared core changes: .\mvnw -pl cli,maven-plugin -am clean verify
- Verify JBang entrypoint help output: jbang Electrostatic.java --help