---
title: V2 Theme Guide
description: Use the v2 visual variant with alternate layouts.
order: 4
---

The `v2` theme provides an alternate visual style and layout set.

## When to use v2

- You want the built-in content model with a different look.
- You prefer v2-specific home, default, and post layouts.

## Use v2 during init

```bash
electrostatic init --theme v2
```

Or with Maven:

```bash
mvn electrostatic:init -Delectrostatic.theme=v2
```

## Override on demand

Theme is persisted in `site-config.xml`, but you can override per command:

```bash
electrostatic build --theme v2
electrostatic serve --theme v2
```

## Content compatibility

You can keep the same content directories used by the default theme:

- `_posts/`
- `_drafts/`
- `_books/`
- `_podcasts/`
- `_feeds/`
- `_static/`
