---
title: Feed Subscription Plugin
description: Loads feed subscription metadata from the feeds collection.
order: 7
---

## What it is for

`FeedSubscriptionPlugin` reads feed/subscription entries from `_feeds/`.

## Inputs

- Folder: `_feeds/`
- File type: `.md`
- Frontmatter-driven metadata.

## Output behavior

- Adds feed subscription entries to the content model.
- Allows themes to render external feed links or subscriptions.

## Notes

If `_feeds/` does not exist, the plugin can skip quietly depending on theme setup.
