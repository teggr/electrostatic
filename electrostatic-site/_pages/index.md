---
layout: landing
title: electrostatic
subtitle: Generate web sites using Java.
authors: Robin Tegg
tags:
    - docs
    - getting started
date: 2023-08-22
navigation:
  - Get started ; #getting-started 
  - Read the docs ; /docs
features:
  - Java ; Use the languages and libraries you like. ; filetype-java
  - Maven Plugin ; First class integration with Maven projects for building sites. ; nut
  - j2html ; Use j2html, the fast and fluent Java HTML5 builder. ; shield
  - Plugins ; Build and extend themes with a pluggable architecture. ; puzzle
---

### Installation

#### Add the maven plugin

```xml
<plugin>
    <groupId>website.electrostatic</groupId>
    <artifactId>electrostatic-maven-plugin</artifactId>
    <version>1.0.0</version>
</plugin>
```

Find the latest version on [maven central](https://)

#### Add some content (optional)

The default theme supports posts. Add a new post with a empty file in the `_posts` directory. Something like `/_posts/2023-08-18-new-blog.md` with the contents:

```markdown
---
layout: post
title: "New Blog"
date: "2023-08-18"
image: https://placehold.co/600x400/EEE/31343C
tags:
  - electrostatic
  - example
---

# Hello world

Welcome to my place

```

#### Build the site

```shell
mvn clean electrostatic:build
```

Now open the index.html and voila!

<div class="text-left">
<img alt="starter site" src="images/starter-site.png" class="rounded" width="300px" height="300px"></img>
</div>

### Next steps

Find more in the [documentation](/docs)

* Configuration Options
* Theming
* Content types