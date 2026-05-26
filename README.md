# electrostatic

A Java-based static site generator with a multi-module Maven structure.

## Modules

- `core` - shared core module scaffold for reusable generation components.
- `generate-plugin` - imported generate plugin module from `teggr/robintegg` for static site generation features.

## Build and release

Run tests from the repository root:

```bash
mvn -B -ntp test
```

Maven Central release instructions are in [`docs/RELEASE.md`](docs/RELEASE.md).
