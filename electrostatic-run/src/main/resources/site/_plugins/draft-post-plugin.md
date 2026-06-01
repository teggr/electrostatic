---
title: Draft Post Plugin
description: Loads unpublished posts for local preview workflows.
order: 4
---

## What it is for

`DraftPostPlugin` loads markdown files in `_drafts/` for draft content.

## Inputs

- Folder: `_drafts/`
- File type: `.md`
- Uses the same markdown/frontmatter format as standard posts.

## Output behavior

- Drafts are available when draft mode is enabled.
- Useful for local editing and preview before publish.

## Usage note

Enable drafts during generation when needed:

```bash
mvn electrostatic:generate -Delectrostatic.includeDrafts=true
jbang run.electrostatic:electrostatic-cli:0.0.1-SNAPSHOT build --include-drafts
```

If you want the localhost shortcut instead of the site-config base URL, pass `--base-url http://localhost:8080` or `-Delectrostatic.baseUrl=http://localhost:8080` alongside the draft flag.
