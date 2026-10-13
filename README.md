# electrostatic

A Java-based static site generator with a multi-module Maven structure.

## Quick start

Choose the wrapper that fits your project. Both delegate to the shared generation core.
Electrostatic requires Java 21 or newer; the repository Maven Wrapper pins Maven 3.9.16.
The plugin does not declare a separate minimum Maven version. `0.0.4` is the latest
published release; the current development version is `0.0.5-SNAPSHOT`.

| Workflow | Source directory by default | Output directory by default |
| --- | --- | --- |
| JBang / CLI | Current directory | `generated-site/` |
| Maven plugin | `src/main/resources/site/` | `target/generated-site/` |

### JBang and CLI

Install [JBang](https://www.jbang.dev/download/), then create and enter a site directory:

```bash
mkdir my-site
cd my-site
jbang io.github.teggr:electrostatic-cli:0.0.4 init
jbang io.github.teggr:electrostatic-cli:0.0.4 build
jbang io.github.teggr:electrostatic-cli:0.0.4 serve --port 8091
```

Open `http://localhost:8091`; press Ctrl+C to stop the preview server. The generated site is written to `generated-site/`.

### Maven plugin

Create an empty project directory with this `pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>example</groupId>
  <artifactId>my-site</artifactId>
  <version>1.0-SNAPSHOT</version>
  <build>
    <plugins>
      <plugin>
        <groupId>io.github.teggr</groupId>
        <artifactId>electrostatic-maven-plugin</artifactId>
        <version>0.0.4</version>
      </plugin>
    </plugins>
  </build>
</project>
```

From that directory, initialize the site, generate it, and preview it:

```bash
mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:init
mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:generate
mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:serve
```

The Maven plugin initializes content in `src/main/resources/site` and writes generated output to `target/generated-site`. Open the local URL printed by `serve`; press Ctrl+C to stop the preview server.

Start with a new empty site directory; the Maven init goal also requires that the site target directory does not already exist. `build`/`generate` and `serve` read `baseUrl` from `site-config.xml` unless explicitly overridden.

### Use the docs theme bundle

For a docs-focused site profile (landing page + section collections), pass `--theme docs` to JBang/CLI init, or `-Delectrostatic.theme=docs` to Maven init. The selected theme is saved to `site-config.xml` and used by later build and serve commands.
For every configuration key, wrapper default, theme/plugin precedence, local asset override,
and troubleshooting step, see the
[Configuration and Troubleshooting guide](electrostatic-run/src/main/resources/site/_guides/configuration-guide.md).

JBang works the same way:

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 init --theme docs
jbang io.github.teggr:electrostatic-cli:0.0.4 build
jbang io.github.teggr:electrostatic-cli:0.0.4 serve
```

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 build --base-url http://localhost:8080 --include-drafts
jbang io.github.teggr:electrostatic-cli:0.0.4 serve --base-url http://localhost:8080 --include-drafts
```

### JBang

Install JBang, then run the CLI artifact directly:

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 init
jbang io.github.teggr:electrostatic-cli:0.0.4 build
```

The command resolves the CLI from your local Maven cache (or configured repositories) and is the quickest way to try the generator in a new project.

### Maven plugin

Add the plugin to your project and call the `init`, `themes`, `serve` and `generate` goals:

```xml
<plugin>
	<groupId>io.github.teggr</groupId>
	<artifactId>electrostatic-maven-plugin</artifactId>
	<version>0.0.4</version>
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
mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:init
mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:themes
mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:serve
mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:generate
```

Or configure your Maven plugin groups once so `mvn electrostatic:*` works everywhere:

```xml
<!-- ~/.m2/settings.xml -->
<settings>
	<pluginGroups>
		<pluginGroup>io.github.teggr</pluginGroup>
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

### Watch and live reload

Enable watch mode to rebuild when source files change and automatically reload open preview pages:

```bash
electrostatic serve --watch --base-url http://localhost:8080
# JBang CLI artifact supports the same --watch option.
mvn electrostatic:serve -Delectrostatic.watch=true -Delectrostatic.baseUrl=http://localhost:8080
```

Watch mode is off by default. It recursively watches the input directory (`--input` or
`electrostatic.inputDirectory`, defaulting to the working directory for CLI/JBang and
`src/main/resources/site` for Maven), including newly created directories. Edits, additions,
deletions, and renames trigger a debounced rebuild with the same generation options.
Generated output and `.git` directories are ignored to avoid rebuild loops.

Open the local preview URL printed by `serve`. In watch mode, served HTML includes a
preview-only reload script that checks for successful builds once per second; generated
files remain unchanged. Pages and assets are served without caching so content, images,
and styling stay current. Failed builds are logged, and watching continues so a corrected
source file can trigger another build. Ctrl+C stops both serving and watching.

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

To override the docs-theme root landing page at `/index.html`, add `_index.md` at the site root.
Its frontmatter `title` becomes the landing page title, its markdown body is rendered above the generated section cards, and the Browse buttons remain theme-generated.

Example:

```md
---
title: CI Ready Maven
description: Project-specific docs landing page
---

Build and publish docs for your own project.
```

## Modules

- `electrostatic-core` - combined site generation engine and shared `SiteGenerator` facade used by runtime entry points.
- `electrostatic-cli` - Picocli-based command-line app with `init`, `build`, `serve`, and `themes` commands for setup, generation, preview, and theme discovery.
- `electrostatic-maven-plugin` - Maven plugin (`electrostatic-maven-plugin`) exposing `init`, `themes`, `serve` and `generate` goals for Maven projects.
- `electrostatic-smoke-tests` - JUnit smoke tests that verify Maven plugin and JBang flows (init, generate/build, and serve behavior) using temporary directories.

## Build and release

The current development version of Electrostatic is `0.0.5-SNAPSHOT`, as declared by the root POM. Consumer examples use the latest published release.

Run tests from the repository root:

```bash
mvn -B -ntp install
```

This runs unit tests and the end-to-end smoke tests, including real Maven goal invocations in a temporary project. Maven artifacts are installed only into the local Maven repository.

Maven Central release instructions are in [`docs/RELEASE.md`](docs/RELEASE.md).

## Project website

The documentation site at https://teggr.github.io/electrostatic/ is built with Electrostatic from `electrostatic-run/src/main/resources/site` and deployed to GitHub Pages by [`.github/workflows/build-site.yml`](.github/workflows/build-site.yml) on every push to `main`.

Build and preview it locally:

```bash
jbang --fresh io.github.teggr:electrostatic-cli:0.0.4 build --input ./electrostatic-run/src/main/resources/site --output ./generated-site
jbang --fresh io.github.teggr:electrostatic-cli:0.0.4 serve --input ./electrostatic-run/src/main/resources/site --output ./generated-site --base-url=http://localhost:8080
```

To publish your own Electrostatic site the same way, see the "Publish to GitHub Pages" guide (`electrostatic-run/src/main/resources/site/_guides/github-pages-guide.md`).
