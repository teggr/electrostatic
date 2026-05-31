---
title: Default Theme Guide
description: Build a blog-style site with the default theme.
order: 2
---

The `default` theme is a general-purpose blog and content theme.

## Best for

- Blogs and changelogs.
- Mixed content sites with posts, podcasts, books, and feeds.

## Initialize with default theme

```bash
electrostatic init
```

Explicitly choose default:

```bash
electrostatic init --theme default
```

## Content structure

Default theme commonly uses:

- `_posts/` for markdown posts.
- `_drafts/` for unpublished posts.
- `_books/` for book metadata.
- `_podcasts/` for podcast episodes.
- `_feeds/` for feed subscriptions.
- `_static/` for static assets.

## Build and serve

```bash
electrostatic build
electrostatic serve
```

## Customization tips

- Use frontmatter fields like `title`, `description`, `date`, and `tags` in posts.
- Place CSS, images, and downloadable files in `_static/`.
- Use category and tag metadata to drive archive pages.
