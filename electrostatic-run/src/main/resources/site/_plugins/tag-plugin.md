---
title: Tag Plugin
description: Builds tag landing pages and groups content by tag.
order: 12
---

## What it is for

`TagPlugin` groups tagged content and generates tag-specific pages.

## Inputs

- Content implementing tag metadata (for example post frontmatter `tags`).

## Output behavior

- Generates tag pages at paths like `/tags/{tag}/index.html`.
- Makes tagged content discoverable by topic.

## Example

Add tags in post frontmatter:

```yaml
---
title: Feature Deep Dive
tags: [architecture, plugins]
---
```
