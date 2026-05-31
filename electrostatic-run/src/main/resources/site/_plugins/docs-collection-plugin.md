---
title: Docs Collection Plugin
description: Loads section-based documentation entries for the docs theme.
order: 2
---

## What it is for

`DocsCollectionPlugin` reads markdown content from configured docs sections such as `_installation`, `_guides`, and `_plugins`.

## Inputs

- `site-config.xml` values:
  - `docsSections`
  - `docsSectionLabels`
- Markdown files in each configured section folder.

## Output behavior

- Creates individual docs pages from markdown files.
- Creates an index page per section.
- Sorts pages by frontmatter `order`, then title.

## Frontmatter example

```yaml
---
title: Build Commands
description: Generate and preview the site.
order: 20
---
```
