
# Module Boundaries And Entrypoints

- Keep shared generation behavior in core. Add or change behavior there first, then adapt wrappers.
- CLI and Maven plugin should share underlying code paths through core services and models, not through direct dependencies on each other.
- Do not add CLI as a dependency of the Maven plugin or Maven plugin as a dependency of the CLI.
- When introducing a user-facing feature available in both wrappers, implement once in core and wire it in both wrappers using wrapper-specific UX only.
- Keep wrapper capabilities in sync by default: when adding or changing a user-facing CLI/JBang command, add or update the corresponding Maven plugin goal in the same change (and vice versa) unless the request explicitly scopes to one wrapper.

# Wrapper Feature Parity

- Treat CLI/JBang and Maven plugin as parity surfaces for user-facing commands.
- For new command features, update both wrappers together, and document both invocation forms together.
- If parity is intentionally deferred, call it out explicitly in PR notes/docs and include follow-up work.

# Markdown Processing Principle

- Treat base-url-aware markdown destination resolution as a core library rule.
- For markdown links/images, resolve destinations through shared core markdown utilities so generated output remains correct when Site.baseUrl includes a subpath.
- Do not rely on `{{site.baseurl}}` placeholders in markdown content. Markdown processors should resolve destinations from runtime site configuration.
- New markdown-backed plugins must reuse shared markdown parser and URL transformation utilities from core rather than introducing plugin-specific markdown URL handling.

# Architecture Documentation Maintenance

- Keep docs/ARCHITECTURE.md current whenever architecture changes in a meaningful way.
- Treat the following as meaningful architecture changes that require an ARCHITECTURE.md update in the same change: core pipeline stage/order changes, plugin lifecycle or interface contract changes, new/removed plugin categories, theme resolution behavior changes, content model/rendering flow changes, wrapper-to-core delegation changes, and preview server behavior changes.
- When a change is intentionally implementation-only and does not alter architecture behavior, add a brief note in PR notes explaining why no ARCHITECTURE.md update was needed.

# Documentation Site Maintenance

- Keep the documentation site source under electrostatic-run/src/main/resources/site current when user-visible behavior changes.
- When architecture, plugin behavior, theme behavior, configuration semantics, or wrapper command/goal behavior changes, update the relevant docs-site pages in the same change (typically under _guides and _plugins), not only docs/ARCHITECTURE.md.
- If no docs-site page changes are required, add a brief PR note explaining why existing docs-site content is still accurate.

# Documentation Update Gate

- Before marking work complete, verify: docs/ARCHITECTURE.md is updated when architecture meaningfully changed.
- Before marking work complete, verify: electrostatic-run/src/main/resources/site docs pages are updated when user-visible behavior changed.
- If either update is intentionally not required, include a short PR note that explains why.

# JBang Contract

- Keep Electrostatic.java as a thin launcher that delegates to ElectrostaticCli.main(args).
- Keep jbang-catalog.json alias electrostatic pointing to script-ref Electrostatic.java.
- When CLI artifact coordinates or project version change, update the //DEPS line in Electrostatic.java to match the current CLI artifact/version.

# Quick Verification

- Build wrappers with shared core changes: .\mvnw -pl electrostatic-cli,electrostatic-maven-plugin -am clean verify
- Verify JBang entrypoint help output: jbang Electrostatic.java --help