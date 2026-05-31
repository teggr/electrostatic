---
title: Create Your Own Theme
description: Build a custom theme plugin for Electrostatic.
order: 5
---

When built-in themes are not enough, create a custom `ThemePlugin`.

## Theme plugin responsibilities

A theme can contribute:

- Content loading behavior.
- Layout registration.
- Plugin registration.
- Initialization scaffolding.

In practice, custom themes typically implement `ThemePlugin` and optionally `ContentTypePlugin`, `ContentRenderPlugin`, and `InitializationPlugin`.

## Minimum viable custom theme

1. Implement a theme class with `registerPlugins()`.
2. Register at least one layout in `loadLayout(...)`.
3. Add landing/index page content in `loadContent(...)`.
4. Optionally create starter folders in `initialize(...)`.

## Suggested structure

```text
my-theme-module/
  src/main/java/.../MyThemePlugin.java
  src/main/resources/theme/my-theme/css/theme.css
```

## Wiring strategy

- Keep shared generation behavior in core.
- Register content and aggregator plugins needed by your content model.
- Add classpath static files for theme assets.

## Verify your theme

- Initialize with your theme.
- Generate and serve.
- Validate that layouts resolve and assets are copied.

If your theme is intended for both CLI and Maven users, wire it once in core so wrappers share behavior.
