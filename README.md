# electrostatic

A Java-based static site generator with a multi-module Maven structure.

## Quick start

This repo supports two out-of-the-box ways to run Electrostatic:

### Initialize a site

Create a new site scaffold in the current directory:

```bash
electrostatic init
```

Create a new site scaffold in a specific directory:

```bash
electrostatic init ./my-site
```

The CLI/JBang `init` command requires an empty target directory and creates a starter structure including:

- `site-config.xml`
- `_posts/2026-01-01-hello-world.md`
- `_books/`
- `_podcasts/`
- `_feeds/`
- `_static/`
- `_drafts/`

For the JBang/CLI flow, `build` and `serve` default to reading site content from the current working directory and writing output to `./generated-site`.

### Use the docs theme bundle

Electrostatic now supports selecting a theme bundle.
The selected theme is persisted to `site-config.xml` during `init`, so `build` and `serve` automatically use it.

For a docs-focused site profile (landing page + section collections), use:

```bash
electrostatic init --theme docs
electrostatic build
electrostatic serve
```

`build` and `serve` now default to the `baseUrl` in `site-config.xml`. If you want the localhost shortcut, pass it explicitly. You can also opt into drafts for a single run:

```bash
electrostatic build --base-url http://localhost:8080 --include-drafts
electrostatic serve --base-url http://localhost:8080 --include-drafts
```

List available theme bundles:

```bash
electrostatic themes
jbang site.electrostatic:electrostatic-cli:0.0.1-SNAPSHOT themes
```

You can still pass `--theme` to `build` or `serve` to override the persisted value for a single run.

JBang works the same way:

```bash
jbang site.electrostatic:electrostatic-cli:0.0.1-SNAPSHOT init --theme docs
jbang site.electrostatic:electrostatic-cli:0.0.1-SNAPSHOT build
jbang site.electrostatic:electrostatic-cli:0.0.1-SNAPSHOT serve
```

```bash
jbang site.electrostatic:electrostatic-cli:0.0.1-SNAPSHOT build --base-url http://localhost:8080 --include-drafts
jbang site.electrostatic:electrostatic-cli:0.0.1-SNAPSHOT serve --base-url http://localhost:8080 --include-drafts
```

### JBang

Install JBang, then run the CLI artifact directly:

```bash
jbang site.electrostatic:electrostatic-cli:0.0.1-SNAPSHOT init
jbang site.electrostatic:electrostatic-cli:0.0.1-SNAPSHOT build
```

The command resolves the CLI from your local Maven cache (or configured repositories) and is the quickest way to try the generator in a new project.

### Maven plugin

Add the plugin to your project and call the `init`, `themes`, `serve` and `generate` goals:

```xml
<plugin>
	<groupId>site.electrostatic</groupId>
	<artifactId>electrostatic-maven-plugin</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</plugin>
```

```bash
mvn electrostatic:init
mvn electrostatic:themes
mvn electrostatic:serve
mvn electrostatic:generate
```

If prefix resolution is not configured in your Maven environment, use fully-qualified coordinates:

```bash
mvn site.electrostatic:electrostatic-maven-plugin:0.0.1-SNAPSHOT:init
mvn site.electrostatic:electrostatic-maven-plugin:0.0.1-SNAPSHOT:themes
mvn site.electrostatic:electrostatic-maven-plugin:0.0.1-SNAPSHOT:serve
mvn site.electrostatic:electrostatic-maven-plugin:0.0.1-SNAPSHOT:generate
```

Or configure your Maven plugin groups once so `mvn electrostatic:*` works everywhere:

```xml
<!-- ~/.m2/settings.xml -->
<settings>
	<pluginGroups>
		<pluginGroup>site.electrostatic</pluginGroup>
	</pluginGroups>
</settings>
```

To initialize a different root directory:

```bash
mvn electrostatic:init -Delectrostatic.rootDirectory=./my-site
```

By default, the Maven plugin initializes site scaffolding in `src/main/resources/site`.
This works for existing Maven modules (for example, when `pom.xml`, `src/`, or `target/` already exist) and fails only when the target site directory already exists.

To initialize a different site directory:

```bash
mvn electrostatic:init -Delectrostatic.rootDirectory=./src/main/resources/my-site
```

By default, the Maven plugin reads site content from `src/main/resources/site` and writes output to `target/generated-site`.

`generate` and `serve` default to the `baseUrl` in `site-config.xml`.

To select the docs bundle in Maven plugin goals:

`--theme` is a CLI/JBang option, not a Maven goal option. For Maven, use `-D` properties.

```bash
mvn electrostatic:init -Delectrostatic.theme=docs
mvn electrostatic:generate
mvn electrostatic:serve
```

In PowerShell, quote dotted `-D` property keys so they are passed correctly:

```powershell
mvn electrostatic:init '-Delectrostatic.theme=docs'
```

You can still pass `-Delectrostatic.theme=...` to `generate` or `serve` to override the persisted theme for that invocation.

You can also explicitly choose the localhost shortcut and include drafts for a single Maven invocation:

```bash
mvn electrostatic:generate -Delectrostatic.baseUrl=http://localhost:8080 -Delectrostatic.includeDrafts=true
mvn electrostatic:serve -Delectrostatic.baseUrl=http://localhost:8080 -Delectrostatic.includeDrafts=true
```

## Docs collections configuration

The docs bundle uses a generic section-collection model configured in `site-config.xml`.
This makes section sets project-specific without code changes.

Default docs profile:

```xml
<docsSections>installation,guides,plugins</docsSections>
<docsSectionLabels>installation=Installation,guides=Guides,plugins=Plugins</docsSectionLabels>
```

Example deploy4j-like profile (swap `plugins` for `commands`):

```xml
<docsSections>installation,guides,commands</docsSections>
<docsSectionLabels>installation=Installation,guides=Guides,commands=Commands</docsSectionLabels>
```

Each section key maps to a markdown folder with an underscore prefix:

- `installation` -> `_installation/`
- `guides` -> `_guides/`
- `plugins` -> `_plugins/`
- `commands` -> `_commands/`

Markdown files support frontmatter fields like `title`, `description`, `order`, and optional `slug`.

## Modules

- `electrostatic-core` - combined site generation engine and shared `SiteGenerator` facade used by runtime entry points.
- `electrostatic-cli` - Picocli-based command-line app with `init`, `build`, `serve`, and `themes` commands for setup, generation, preview, and theme discovery.
- `electrostatic-maven-plugin` - Maven plugin (`electrostatic-maven-plugin`) exposing `init`, `themes`, `serve` and `generate` goals for Maven projects.
- `electrostatic-smoke-tests` - JUnit smoke tests that verify Maven plugin and JBang flows (init, generate/build, and serve behavior) using temporary directories.

## Build and release

Run tests from the repository root:

```bash
mvn -B -ntp test
```

Maven Central release instructions are in [`docs/RELEASE.md`](docs/RELEASE.md).
