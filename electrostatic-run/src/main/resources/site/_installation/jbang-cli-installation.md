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

## 2. Initialize a site

From the repository root (or any directory where you keep content), run:

```bash
jbang Electrostatic.java init
```

To initialize docs-focused structure:

```bash
jbang Electrostatic.java init --theme docs
```

## 3. Build and preview

Generate static output:

```bash
jbang Electrostatic.java build
```

Start the preview server:

```bash
jbang Electrostatic.java serve --port 8091
```

Open `http://localhost:8091`.

## 4. Theme selection

Theme is persisted in `site-config.xml` during `init`. You can override for one run:

```bash
jbang Electrostatic.java build --theme v2
jbang Electrostatic.java serve --theme v2
```

## Troubleshooting

- If dependencies changed recently, run a Maven install for the CLI module first so JBang resolves the latest local artifact.
- If `init` fails, ensure the target directory is empty.
