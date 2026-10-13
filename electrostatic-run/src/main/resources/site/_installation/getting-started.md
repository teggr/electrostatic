---
title: Installation
description: Choose the setup path that fits your workflow.
order: 1
---

Electrostatic supports two primary setup paths. Both use the same generation core:

| Workflow | Start here | Source / output defaults |
| --- | --- | --- |
| JBang + CLI | [Install with JBang](jbang-cli-installation.html) | Current directory / `generated-site/` |
| Maven plugin | [Install with Maven](maven-cli-installation.html) | `src/main/resources/site/` / `target/generated-site/` |

## Choose your path

- [Install with JBang and use the CLI](jbang-cli-installation.html)
- [Install and run with Maven](maven-cli-installation.html)

## Minimum requirements

- Java 21 or newer.
- Maven 3.9.16 is the version pinned by the project Maven Wrapper; the plugin does not declare a separate minimum Maven version.
- Internet access for dependency download on first run.

The latest published release is `0.0.4`. The CLI coordinate is
`io.github.teggr:electrostatic-cli`; the Maven plugin coordinate is
`io.github.teggr:electrostatic-maven-plugin`.

If you already use Maven in your project, start with the Maven page. If you want the fastest
trial without adding a plugin to your POM, start with JBang. See
[Configuration and troubleshooting](../guides/configuration-guide.html) for all settings,
theme selection, asset overrides, and common build problems.
