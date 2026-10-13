---
title: Plugins
description: Understand what each built-in plugin does.
order: 1
---

Electrostatic uses plugins to load content, aggregate site data, and generate outputs like feeds and paginated archives.

## How plugins are selected

The selected built-in theme registers its bundled plugins; Electrostatic does not scan site
folders to discover plugins. List available theme bundles with `electrostatic themes` or
`mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:themes`: `default`, `docs`, and
`v2`. The `docs` theme registers docs section collections, while the general-purpose
themes include plugins for posts and other content types. See
[Configuration and troubleshooting](../guides/configuration-guide.html) for theme-selection
precedence and local asset overrides.

## Content plugins

- [Docs Collection Plugin](docs-collection-plugin.html)
- [Post Plugin](post-plugin.html)
- [Draft Post Plugin](draft-post-plugin.html)
- [Book Plugin](book-plugin.html)
- [Podcast Plugin](podcast-plugin.html)
- [Feed Subscription Plugin](feed-subscription-plugin.html)
- [Static Files Plugin](static-files-plugin.html)
- [GitHub Activity Plugin](github-activity-plugin.html)

## Aggregation plugins

- [Index Plugin](index-plugin.html)
- [Posts Plugin](posts-plugin.html)
- [Tag Plugin](tag-plugin.html)
- [Categories Plugin](categories-plugin.html)
- [Feed Plugin](feed-plugin.html)
