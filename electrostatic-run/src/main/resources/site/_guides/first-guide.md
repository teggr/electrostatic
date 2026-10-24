---
title: Guides
description: Practical walkthroughs for themes and customization.
order: 1
---

Use these guides to configure a site, choose a theme, understand how content is organized, and build a custom theme when the built-ins are not enough.

## Local preview with watch mode

Rebuild and automatically reload your browser while editing:

```bash
electrostatic serve --watch --base-url http://localhost:8080
```

The CLI's `--watch` option also works when launching through JBang. For Maven projects, use:

```bash
mvn electrostatic:serve -Delectrostatic.watch=true -Delectrostatic.baseUrl=http://localhost:8080
```

Open the local URL printed by the command. Watch mode monitors the input directory recursively
(the working directory for CLI/JBang, or `src/main/resources/site` for Maven by default).
Use `--input` or `-Delectrostatic.inputDirectory=...` to choose another source directory.
Changes to content, configuration, images, and styling trigger a debounced full rebuild.
New directories, deleted files, and renamed files are detected too.

Generated output, `.git` directories, and paths matched by `.gitignore` files under the
input directory are ignored. A rebuild log lists the source paths that triggered its event
batch; an event-overflow warning indicates that some paths may not be available. Open preview
pages check for successful builds every second and reload automatically, with caching disabled
for fresh assets. The browser reload script is added only to preview responses, not to generated
files.
If a build fails, the error is logged and watching continues; fix the source to trigger another build.
Watch mode is opt-in, and Ctrl+C stops the server and watcher together.

## Theme guides

- [Configuration and troubleshooting](configuration-guide.html)
- [Default theme guide](default-theme-guide.html)
- [Docs theme guide](docs-theme-guide.html)
- [V2 theme guide](v2-theme-guide.html)

## Customization

- [Create your own theme](create-your-own-theme.html)

## Publishing

- [Publish to GitHub Pages](github-pages-guide.html)
