---
title: Book Plugin
description: Loads book entries from XML metadata files.
order: 5
---

## What it is for

`BookPlugin` reads book metadata from `_books/` and produces book content pages/components.

## Inputs

- Folder: `_books/`
- File type: `.xml`
- Parsed with JAXB.

## Output behavior

- Adds structured book entries to the content model.
- Enables book-related rendering in supported themes.

## When to use

Use this plugin when your site includes recommended reading or a library page.
