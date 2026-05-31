---
title: Post Plugin
description: Loads markdown posts from the posts collection.
order: 3
---

## What it is for

`PostPlugin` loads blog posts from `_posts/` and parses markdown with YAML frontmatter.

## Inputs

- Folder: `_posts/`
- File type: `.md`
- Typical fields: `title`, `description`, `date`, `tags`

## Output behavior

- Creates `Post` content items used by index, posts, tag, category, and feed pages.
- Supports heading anchor generation in rendered HTML.

## Example post frontmatter

```yaml
---
title: Release 1.0
description: What changed in this release.
date: 2026-05-31
tags: [release, changelog]
---
```
