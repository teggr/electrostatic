# Generate Plugin

This is the module for building a static website

# Usage

```java
import com.robintegg.web.engine.WebSiteBuilder;
import com.robintegg.web.theme.DefaultThemePlugin;

// set configuration + theme
WebSiteBuilder webSiteBuilder = new WebSiteBuilder(
        DefaultThemePlugin.create()
);

webSiteBuilder.build();
```

# Architecture

## Engine

This package contains the core engine for building static websites. It provides the necessary abstractions and functionalities to create, manage, and render web pages.

### WebSiteBuilder

The `WebSiteBuilder` class is the main entry point for building a static website. It allows you to configure the website, add themes, and initiate the build process for a static web site.

Process:

* Register plugins from theme
* Loads site configuration
* Loads content model from source using `contentTypePlugins` + `aggregatorPlugins`
* Loads context for rendering
* Loads layouts
* Creates output directory
* Renders content in output directory