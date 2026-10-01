---
title: Publish to GitHub Pages
description: Build an Electrostatic site with GitHub Actions and deploy it to GitHub Pages.
order: 6
---

This guide sets up an Electrostatic site that you can build and preview locally, then deploy to GitHub Pages automatically with GitHub Actions.

## 1. Set up the site

Configure your site in `site-config.xml`, including its title, theme, content sections, and public URL. For a repository site, the base URL is typically:

```text
https://<owner>.github.io/<repository>/
```

For example:

```xml
<site>
  <title>My Project</title>
  <baseUrl>https://my-org.github.io/my-project/</baseUrl>
  <theme>docs</theme>
  <docsSections>installation,guides</docsSections>
  <docsSectionLabels>installation=Installation,guides=Guides</docsSectionLabels>
</site>
```

Install **Java 21** and **JBang**, then build and preview locally:

```bash
jbang --fresh site.electrostatic:electrostatic-cli:0.0.3 build
jbang --fresh site.electrostatic:electrostatic-cli:0.0.3 serve --base-url=http://localhost:8080
```

The build output is written to `generated-site/`. Add `generated-site/` to your `.gitignore`; GitHub Actions builds it for you.

## 2. Add the GitHub Actions workflow

Create `.github/workflows/build-site.yml`:

```yaml
name: Build and Deploy Site

on:
  push:
    branches:
      - main
  workflow_dispatch:

permissions:
  contents: read
  pages: write
  id-token: write

concurrency:
  group: pages
  cancel-in-progress: false

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Check out repository
        uses: actions/checkout@v4

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "21"

      - name: Set up JBang
        uses: jbangdev/setup-jbang@v0.1.1

      - name: Build site
        run: jbang --fresh site.electrostatic:electrostatic-cli:0.0.3 build

      - name: Upload Pages artifact
        uses: actions/upload-pages-artifact@v3
        with:
          path: ./generated-site

  deploy:
    runs-on: ubuntu-latest
    needs: build
    environment:
      name: github-pages
      url: ${{ steps.deployment.outputs.page_url }}
    steps:
      - name: Deploy to GitHub Pages
        id: deployment
        uses: actions/deploy-pages@v4
```

Change `main` if your project uses a different deployment branch. The workflow builds on pushes to that branch, and `workflow_dispatch` lets you run it manually from the **Actions** tab.

### Site content in a subdirectory

If your site source is not at the repository root, pass `--input` and `--output` to the build step:

```yaml
      - name: Build site
        run: >-
          jbang --fresh site.electrostatic:electrostatic-cli:0.0.3 build
          --input ./docs-site
          --output ./generated-site
```

This is how the Electrostatic project publishes this documentation site from `electrostatic-run/src/main/resources/site`.

## 3. Enable GitHub Pages

In your repository, open **Settings → Pages** and set **Build and deployment → Source** to **GitHub Actions**.

Then push to the configured branch, or run **Build and Deploy Site** manually from **Actions**. The deploy job publishes the uploaded `generated-site/` artifact to your Pages URL.

## Troubleshooting

- **Broken links or missing styles:** check that `baseUrl` in `site-config.xml` matches your Pages URL, including the repository path and trailing slash.
- **Build fails with `NoSuchFileException ... _static`:** make sure a `_static/` directory exists in your site source. Git does not track empty directories, so add at least one file to it (for example, an empty `.nojekyll`).
- **Deploy job fails with a permissions error:** confirm the Pages source is set to **GitHub Actions** and the workflow has the `pages: write` and `id-token: write` permissions.
- **Old content after a push:** open the latest workflow run in **Actions** and confirm both the build and deploy jobs succeeded.
