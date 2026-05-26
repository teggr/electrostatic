---
applyTo: "**/src/test/**/*.java"
---

# Test Instructions

- Prefer cohesive unit tests that exercise collaborating classes in the same core flow.
- Start from engine/core behavior and then verify plugin wiring around that behavior.
- Keep tests deterministic: no network calls, no clock dependence without control, no shared mutable global state leakage between tests.
- For static plugin registries (`Plugins.*`), clear and restore state in `@BeforeEach`/`@AfterEach`.
- Assert observable outcomes (generated files, rendered output, visited pages/files, registered plugin counts) rather than internal implementation details.
- Use small in-test fakes/stubs when needed to verify interactions across core + plugin boundaries.
