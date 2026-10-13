# Release artifacts to Maven Central

Releases are started manually from GitHub Actions. The workflow publishes the current snapshot version, creates a matching GitHub Release, then advances the selected branch to the next patch snapshot.

The current development version of Electrostatic is `0.0.5-SNAPSHOT`, as declared by the root POM. Consumer examples use the latest published release.

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
4. The Central plugin automatically publishes the deployment after validation (`autoPublish=true`) and waits until publication completes (`waitUntil=published`). Only then does the workflow create a GitHub Release tagged with that version and GitHub-generated release notes.
5. The workflow advances the POM versions to the next patch snapshot, updates README consumer examples and this guide, then commits and pushes those changes to the selected branch.

The workflow does not create a GitHub Release or push a version change if Maven Central deployment fails. If a release already exists for the same source commit, a rerun reuses it so the version and documentation commit can be retried.

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
