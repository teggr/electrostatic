---
title: Index Plugin
description: Builds the paginated homepage index for post content.
order: 10
---

## What it is for

`IndexPlugin` creates the homepage index from post content.

## Inputs

- Post items loaded into the content model.

## Output behavior

- Generates `/index.html` for first page.
- Generates additional pages such as `/page/2/index.html`.
- Applies pagination defaults from the plugin.

## Why it matters

Without this plugin, new posts would not be automatically listed on the homepage.
