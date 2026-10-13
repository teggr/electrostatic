---
title: Configuration and Troubleshooting
description: Configure a site, choose a theme, override assets, and resolve common problems.
order: 2
---

Electrostatic reads site metadata and theme settings from `site-config.xml` in the input
directory. The selected theme supplies its bundled plugins and layouts; files in `_static/`
can override bundled assets.

## Complete configuration example

```xml
<site>
  <title>My Site</title>
  <description>A short description of this site.</description>
  <baseUrl>https://example.com/</baseUrl>
  <theme>default</theme>
  <email>hello@example.com</email>
  <twitterUsername>example</twitterUsername>
  <githubUsername>example</githubUsername>
  <linkedinUsername>example</linkedinUsername>
  <blueskyUsername>example.bsky.social</blueskyUsername>
  <mastodonUsername>@example</mastodonUsername>
  <feedUrl>https://example.com/feed.xml</feedUrl>
  <author>
    <name>Site Author</name>
    <email>author@example.com</email>
  </author>
  <docsSections>installation,guides,plugins</docsSections>
  <docsSectionLabels>installation=Installation,guides=Guides,plugins=Plugins</docsSectionLabels>
</site>
```

XML does not require fields at parse time, but built-in themes expect `author.name` for
metadata. Setting `title` and `baseUrl` is also recommended for useful metadata and correct
absolute links. Configuration values are not automatically filled in when an XML field is
omitted; `init` writes a starter configuration with a title,
`http://localhost:8080` base URL, theme, and author name.

| Field | Purpose and behavior |
| --- | --- |
| `title` | Site title used in generated page metadata and theme headers. |
| `description` | Default site description used in page metadata and theme footers. |
| `baseUrl` | Public root URL used when resolving site and feed URLs. Set the deployment path, including a repository subpath for project pages; a trailing slash is recommended. |
| `theme` | Built-in theme bundle: `default`, `docs`, or `v2`. If omitted, the theme falls back to `default`. |
| `email` | Contact email shown by themes that include contact details. |
| `twitterUsername`, `githubUsername`, `linkedinUsername`, `blueskyUsername`, `mastodonUsername` | Optional social profile names used by the theme's social links. |
| `feedUrl` | Optional feed URL used by themes that display a feed link. |
| `author.name`, `author.email` | Author name and email; built-in themes use `author.name` in page metadata and feed author details. |
| `docsSections` | Comma-separated docs section keys, used by the `docs` theme. Defaults to `installation,guides,plugins` when not set. |
| `docsSectionLabels` | Comma-separated `key=Label` pairs for docs navigation. Defaults to labels for the default sections. |

The `docs` theme maps each section key to a folder with a leading underscore (for example,
`guides` maps to `_guides/`). See the [docs theme guide](docs-theme-guide.html) for section
and landing page details.

## Choose an invocation

Both wrappers use the same core generator. JBang launches the CLI artifact directly; the
Maven plugin exposes equivalent Maven goals.

| Task | JBang / CLI | Maven plugin |
| --- | --- | --- |
| Initialize a docs site | `jbang io.github.teggr:electrostatic-cli:0.0.4 init --theme docs` | `mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:init -Delectrostatic.theme=docs` |
| Build | `jbang io.github.teggr:electrostatic-cli:0.0.4 build` | `mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:generate` |
| Preview | `jbang io.github.teggr:electrostatic-cli:0.0.4 serve` | `mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:serve` |
| List built-in themes | `jbang io.github.teggr:electrostatic-cli:0.0.4 themes` | `mvn io.github.teggr:electrostatic-maven-plugin:0.0.4:themes` |

The examples use the latest published release, `0.0.4`. The CLI artifact is
`io.github.teggr:electrostatic-cli`; the Maven plugin is
`io.github.teggr:electrostatic-maven-plugin`. The project requires Java 21 or newer.
The repository Maven Wrapper pins Maven 3.9.16. The project does not declare a separate
minimum Maven version.

JBang and CLI commands run from the site root by default, reading the current directory and
writing to `generated-site/`. The Maven plugin expects a Maven project: it reads
`src/main/resources/site/` and writes `target/generated-site/` by default. Maven's `init`
goal creates the site directory only if it does not already exist.

## Configuration and override precedence

For `build`, `generate`, and `serve`, an explicit theme option takes precedence over
`<theme>` in `site-config.xml`; if neither is supplied, Electrostatic uses `default`.
`init` selects a theme and saves it in the generated configuration. Available built-in
themes can be listed with the `themes` command/goal. Theme bundles register their own
built-in plugins; plugins are not discovered by scanning content folders. To write a
custom theme or register custom plugins, see [Create your own theme](create-your-own-theme.html).

An explicit base URL option takes precedence over `<baseUrl>` in `site-config.xml`.
For JBang/CLI, use `--base-url`; for Maven, use `-Delectrostatic.baseUrl=...`. If no
override is given, the configured value is used. For a local preview, explicitly pass
`http://localhost:8080`. For a project website hosted below a path, include that path in
the value, for example `https://example.github.io/my-site/`. See the
[GitHub Pages guide](github-pages-guide.html).

| Setting | JBang / CLI default | Maven default |
| --- | --- | --- |
| Input directory | Current directory | `src/main/resources/site` |
| Generated output | `generated-site/` | `target/generated-site/` |
| Include draft posts | Off; add `--include-drafts` | Off; add `-Delectrostatic.includeDrafts=true` |
| Preview port | `8080`; change with `--port` | `8080`; change with `-Delectrostatic.port=...` |
| Watch and reload | Off; add `serve --watch` | Off; add `-Delectrostatic.watch=true` |

Use `--input` and `--output` to change CLI paths, or
`-Delectrostatic.inputDirectory` and `-Delectrostatic.outputDirectory` for Maven. The
Maven `init` goal uses `-Delectrostatic.rootDirectory` to change its target directory.

## Theme and local asset overrides

The built-in themes are `default`, `docs`, and `v2`. `default` and `v2` provide general
content-site layouts; `docs` provides a section-based documentation landing page and
collections. Each theme registers its own built-in plugin set. The
[plugin overview](../plugins/plugin-overview.html) links to descriptions of those plugins.

Put local images, stylesheets, scripts, and downloads under `_static/`. The folder name is
not part of the published URL: `_static/images/logo.svg` is emitted as `images/logo.svg`.
When a local file has the same output path as a bundled theme or plugin asset, the local
file wins and a warning identifies both sources. Local CSS files are linked automatically
after the theme stylesheet links. For example, `_static/css/main.css` replaces the
`/css/main.css` theme asset used by `default` and `docs`; `_static/css/theme.css` replaces
the corresponding `v2` stylesheet.

## Troubleshooting

### Links or styles break after deployment

Set `baseUrl` to the complete public site root, including a repository path for project
pages. For example, use `https://example.github.io/my-site/`, not only
`https://example.github.io/`. Use the same value in `site-config.xml` for builds and
deployments, or pass a one-command override. A mismatch commonly produces links or CSS
URLs that point to the wrong path.

### An image, stylesheet, or download is missing

- Confirm the file is under `_static/` and use the output path in links (omit `_static/`).
- Check filename capitalization; published site hosts may use case-sensitive paths.
- Confirm the generated output contains the expected file at that output path.
- If replacing a theme asset, match its output path exactly; check the build log for a
  static-asset conflict warning.
- Check `baseUrl` when the file exists but its browser URL points to the wrong location.

### Initialization or build fails

- Use Java 21 or newer; check `java -version`.
- Run Maven goals from a directory containing the intended project `pom.xml`. The Maven
  `init` goal fails if its target site directory already exists; choose a new target with
  `-Delectrostatic.rootDirectory=...` if needed.
- Ensure `site-config.xml` is well-formed XML and that its theme is `default`, `docs`, or
  `v2`. An unknown theme is an error.
- Use matching published artifact versions in CLI and Maven commands. If a preview port
  is already in use, select another with `--port` or `-Delectrostatic.port`.
- Read the first reported build error; Maven's `-e` option can show the underlying cause.
