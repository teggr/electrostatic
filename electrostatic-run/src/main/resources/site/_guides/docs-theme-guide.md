---
title: Docs Theme Guide
description: Build section-based documentation sites.
order: 3
---

The `docs` theme is optimized for product and project documentation.

## Initialize docs theme

```bash
electrostatic init --theme docs
```

For Maven:

```bash
mvn electrostatic:init -Delectrostatic.theme=docs
```

## Configure docs sections

In `site-config.xml`, set section keys and labels:

```xml
<docsSections>installation,guides,plugins</docsSections>
<docsSectionLabels>installation=Installation,guides=Guides,plugins=Plugins</docsSectionLabels>
```

Section key `installation` maps to folder `_installation/`.

## Author section pages

Each markdown file in a section folder becomes a docs page.

Markdown links and images are resolved against your configured `baseUrl` path during generation:

- Root-relative destinations like `/guides/index.html` are prefixed with the configured base path.
- Page-relative destinations like `plugin-overview.html` or `../guides/first-guide.html` are resolved from the current page path, then prefixed with the configured base path.
- External URLs (`https://...`, `//...`), anchors (`#...`), and URI schemes (`mailto:`, `tel:`, `data:`) are not changed.

Do not use `{{site.baseurl}}` placeholders in markdown content.

Use frontmatter for ordering:

```yaml
---
title: My Topic
description: What this page covers.
order: 10
---
```

## Customize the root landing page

Add `_index.md` at the site root to replace the default docs landing copy at `/index.html`.
The frontmatter `title` becomes the landing page heading, the markdown body is rendered above the generated section cards, and the Browse buttons stay in place.

```md
---
title: My Product Docs
description: Start here
---

Welcome to the docs for my product.
```

## Build workflow

```bash
electrostatic build
electrostatic serve
```

Landing page and top navigation are generated from configured docs sections.
