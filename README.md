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

### JBang

Install JBang, then run the root script directly:

```bash
jbang Electrostatic.java init
jbang Electrostatic.java build
```

The script boots the CLI from the local Maven cache and is the quickest way to try the generator in a new project.

### Maven plugin

Add the plugin to your project and call the `init`, `serve` and `generate` goals:

```xml
<plugin>
	<groupId>run.electrostatic</groupId>
	<artifactId>electrostatic-maven-plugin</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</plugin>
```

```bash
mvn electrostatic:init
mvn electrostatic:serve
mvn electrostatic:generate
```

If prefix resolution is not configured in your Maven environment, use fully-qualified coordinates:

```bash
mvn run.electrostatic:electrostatic-maven-plugin:0.0.1-SNAPSHOT:init
mvn run.electrostatic:electrostatic-maven-plugin:0.0.1-SNAPSHOT:serve
mvn run.electrostatic:electrostatic-maven-plugin:0.0.1-SNAPSHOT:generate
```

Or configure your Maven plugin groups once so `mvn electrostatic:*` works everywhere:

```xml
<!-- ~/.m2/settings.xml -->
<settings>
	<pluginGroups>
		<pluginGroup>run.electrostatic</pluginGroup>
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

## Modules

- `electrostatic-core` - combined site generation engine and shared `SiteGenerator` facade used by runtime entry points.
- `electrostatic-cli` - Picocli-based command-line app with `init`, `build`, and `serve` commands for setup, generation, and preview.
- `electrostatic-maven-plugin` - Maven plugin (`electrostatic-maven-plugin`) exposing `init`, `serve` and `generate` goals for Maven projects.
- `electrostatic-smoke-tests` - JUnit smoke tests that verify Maven plugin and JBang flows (init, generate/build, and serve behavior) using temporary directories.

## Build and release

Run tests from the repository root:

```bash
mvn -B -ntp test
```

Maven Central release instructions are in [`docs/RELEASE.md`](docs/RELEASE.md).
