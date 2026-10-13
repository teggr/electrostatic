---
title: Install with JBang and CLI
description: Fast setup path for local development.
order: 2
---

Use JBang when you want to try Electrostatic quickly without creating a Maven project first.

## 1. Install JBang

Follow the official installer instructions for your OS:

- [https://www.jbang.dev/download/](https://www.jbang.dev/download/)

Then verify:

```bash
jbang version
```

## 2. Initialize a new site

Create an empty directory and run the commands from inside it:

```bash
mkdir my-site
cd my-site
jbang io.github.teggr:electrostatic-cli:0.0.4 init
```

To initialize docs-focused structure:

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 init --theme docs
```

## 3. Build and preview

Generate static output:

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 build
```

By default, JBang/CLI builds use the `baseUrl` from `site-config.xml`. If you want the localhost shortcut, set it explicitly and include drafts when needed:

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 build --base-url http://localhost:8080 --include-drafts
```

Start the preview server:

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 serve --port 8091
```

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 serve --port 8091 --base-url http://localhost:8080 --include-drafts
```

Open `http://localhost:8091`.

The generated files are written to `generated-site/`. Press Ctrl+C to stop the preview server.

## 4. Theme selection

Theme is persisted in `site-config.xml` during `init`. You can override for one run:

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 build --theme v2
jbang io.github.teggr:electrostatic-cli:0.0.4 serve --theme v2
```

List all available built-in theme bundles:

```bash
jbang io.github.teggr:electrostatic-cli:0.0.4 themes
```

If you installed the CLI directly, the equivalent command is:

```bash
electrostatic themes
```

## Troubleshooting

- If dependencies changed recently, run a Maven install for the CLI module first so JBang resolves the latest local artifact.
- If `init` fails, ensure the target directory is empty.
- For local static servers, ensure directory index files are enabled. Tag and category detail pages are generated as `.../index.html` paths.
