# Release artifacts to Maven Central

Releases are started manually from GitHub Actions. The workflow publishes the current snapshot version, creates a matching GitHub Release, then advances the selected branch to the next patch snapshot.

The current development version of Electrostatic is `0.0.4-SNAPSHOT`, as declared by the root POM. Consumer examples use the latest published release.

## Configure repository secrets

Add these Actions secrets under **Settings > Secrets and variables > Actions**:

| Secret | Value |
| --- | --- |
| `CENTRAL_USERNAME` | Username from a Sonatype Central user token |
| `CENTRAL_PASSWORD` | Password from the same Sonatype Central user token |
| `GPG_KEY_ID` | ID or fingerprint of the signing key |
| `GPG_PRIVATE_KEY` | ASCII-armored private signing key |
| `GPG_PASSPHRASE` | Passphrase for the signing key |

Before publishing under `io.github.teggr`, verify that this namespace is registered to the account used by the Central publishing credentials.

The workflow needs `contents: write` permission to create the GitHub Release and push the next-snapshot POM and documentation changes. The selected branch must allow pushes from `github-actions[bot]`; adjust branch protection or dispatch from a branch where the workflow is allowed to push.

## Publish a release

1. Ensure the selected branch has a root POM version ending in `-SNAPSHOT`.
2. Open **Actions > Publish release > Run workflow**, select the branch, and run it.
3. The workflow removes `-SNAPSHOT` from the current version, checks POM metadata, and publishes the signed artifacts using the `central` profile. The `electrostatic-smoke-tests` and `electrostatic-run` modules are excluded from Maven Central.
4. After a successful Maven Central deployment, the workflow creates a GitHub Release tagged with that version and GitHub-generated release notes.
5. The workflow advances the POM versions to the next patch snapshot, updates README consumer examples and this guide, then commits and pushes those changes to the selected branch.

The workflow does not create a GitHub Release or push a version change if Maven Central deployment fails. If a release already exists for the same source commit, a rerun skips credentials, signing, publication, and release creation, so only the version and documentation commit is retried.

## Recover from an existing release version

Release tags and published versions are immutable. A release at a different commit, or a tag without a matching GitHub Release, stops the workflow before signing or publication. Do not move, delete, or recreate the tag, and do not publish newer sources under that version.

The existing `0.0.3` release predates the `io.github.teggr` namespace and the new workflows. The development POMs were still at `0.0.3-SNAPSHOT`, so dispatching from current `main` attempted to reuse that historical version. Development now starts at `0.0.4-SNAPSHOT`; the next release is `0.0.4`, leaving `0.0.3` unchanged.

For a future collision, advance every reactor POM to an unused patch snapshot with the Versions plugin, update the development version in README and this guide, commit the changes, and dispatch from that commit. If Central deployment succeeds but GitHub Release creation fails, inspect the deployment and its exact source commit before recovering; do not blindly rerun publication over existing components.

## Workflow maintenance notes

These fixes change release tag/version recovery only; no generation pipeline, plugin contract, wrapper delegation, or preview behavior changes, so `docs/ARCHITECTURE.md` remains applicable. No documentation-site changes are needed because consumer commands, goals, configuration, and Pages behavior are unchanged. Maven CI, Pages, and Maven Central publishing configuration are intentionally outside this change.

## Build requirements

JDK 21 is required to build, test, and publish this repository.

## Plugins

* [Sonatype Central publishing](https://central.sonatype.com/publishing)
* [Sonatype Central deployments](https://central.sonatype.com/publishing/deployments)
* [Maven GPG Plugin](https://maven.apache.org/plugins/maven-gpg-plugin/)
* [Maven Versions Plugin](https://www.mojohaus.org/versions/versions-maven-plugin/)
* [POM Checker](https://kordamp.org/pomchecker/pomchecker-maven-plugin/)

## Maintenance

* https://central.sonatype.com/publishing
