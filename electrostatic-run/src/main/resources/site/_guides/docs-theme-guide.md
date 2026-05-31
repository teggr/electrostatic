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

Use frontmatter for ordering:

```yaml
---
title: My Topic
description: What this page covers.
order: 10
---
```

## Build workflow

```bash
electrostatic build
electrostatic serve
```

Landing page and top navigation are generated from configured docs sections.
