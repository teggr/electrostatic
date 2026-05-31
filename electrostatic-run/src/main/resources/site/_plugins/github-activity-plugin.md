---
title: GitHub Activity Plugin
description: Fetches recent repository activity for a configured GitHub user.
order: 9
---

## What it is for

`GithubActivityPlugin` fetches active repositories from GitHub for display in supported themes.

## Inputs

- `site-config.xml` field: `githubUsername`

Example:

```xml
<githubUsername>your-username</githubUsername>
```

## Output behavior

- Loads a limited list of active repositories.
- Exposes data for theme rendering.
- Fails gracefully when GitHub is unavailable.

## When to use

Use this plugin for personal/project homepages that show current open-source activity.
