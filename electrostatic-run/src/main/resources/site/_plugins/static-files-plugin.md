---
title: Static Files Plugin
description: Copies static assets from source folders to generated output.
order: 8
---

## What it is for

`StaticFilesPlugin` includes non-markdown assets in the generated site.

## Inputs

- Default source folder: `_static/`
- Typical files: images, CSS, JavaScript, PDFs, icons

## Output behavior

- Copies files to generated output paths.
- Preserves relative paths for static references.

## Example

If you place `logo.svg` in `_static/images/logo.svg`, it becomes available in output under `images/logo.svg`.
