---
title: Install and Run with Maven
description: Maven plugin workflow for project and CI usage.
order: 3
---

Use the Maven plugin when your site is part of a Maven module or build pipeline.

## 1. Add the plugin

Add Electrostatic to your `pom.xml`:

```xml
<plugin>
  <groupId>run.electrostatic</groupId>
  <artifactId>electrostatic-maven-plugin</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</plugin>
```

If plugin prefix resolution is not configured, use fully qualified goals.

## 2. Initialize site content

Initialize default content in the module:

```bash
mvn electrostatic:init
```

Initialize docs theme content:

```bash
mvn electrostatic:init -Delectrostatic.theme=docs
```

## 3. Generate and serve

Generate static output:

```bash
mvn electrostatic:generate
```

By default, Maven generation uses the `baseUrl` from `site-config.xml`. If you want the localhost shortcut, set it explicitly and include drafts when needed:

```bash
mvn electrostatic:generate -Delectrostatic.baseUrl=http://localhost:8080 -Delectrostatic.includeDrafts=true
```

Preview locally:

```bash
mvn electrostatic:serve
```

```bash
mvn electrostatic:serve -Delectrostatic.baseUrl=http://localhost:8080 -Delectrostatic.includeDrafts=true
```

List available built-in theme bundles:

```bash
mvn electrostatic:themes
```

By default, source content is read from `src/main/resources/site` and output is written to `target/generated-site`.

## 4. Optional configuration

Set a custom site root:

```bash
mvn electrostatic:init -Delectrostatic.rootDirectory=./my-site
```

Override theme for a single invocation:

```bash
mvn electrostatic:generate -Delectrostatic.theme=v2
```

PowerShell note for dotted properties:

```powershell
mvn electrostatic:generate '-Delectrostatic.baseUrl=http://localhost:8080' '-Delectrostatic.includeDrafts=true'
```

```powershell
mvn electrostatic:init '-Delectrostatic.theme=docs'
```
