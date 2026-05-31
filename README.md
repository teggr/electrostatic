# electrostatic

A Java-based static site generator with a multi-module Maven structure.

## Quick start

This repo supports two out-of-the-box ways to run Electrostatic:

### JBang

Install JBang, then run the root script directly:

```bash
jbang Electrostatic.java
```

The script boots the CLI from the local Maven cache and is the quickest way to try the generator in a new project.

### Maven plugin

Add the plugin to your project and call the `generate` goal:

```xml
<plugin>
	<groupId>run.electrostatic</groupId>
	<artifactId>electrostatic-maven-plugin</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</plugin>
```

```bash
mvn run.electrostatic:electrostatic-maven-plugin:0.0.1-SNAPSHOT:generate
```

By default, the plugin reads site content from `src/main/resources/site` and writes output to `target/site`.

## Modules

- `electrostatic-core` - combined site generation engine and shared `SiteGenerator` facade used by runtime entry points.
- `electrostatic-cli` - Picocli-based command-line app with `build` and `serve` commands for local generation and preview.
- `electrostatic-maven-plugin` - Maven plugin (`electrostatic-maven-plugin`) exposing the `generate` goal for Maven projects.

## Build and release

Run tests from the repository root:

```bash
mvn -B -ntp test
```

Maven Central release instructions are in [`docs/RELEASE.md`](docs/RELEASE.md).
