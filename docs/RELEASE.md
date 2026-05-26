# Release artefacts to Maven Central

```shell
# clean build outputs
mvn clean

# tag release source
git tag <version>

# set release version across all modules
mvn -Pcentral versions:set -DremoveSnapshot -DprocessAllModules

# validate POM metadata for central requirements
mvn -Pcentral org.kordamp.maven:pomchecker-maven-plugin:1.14.0:check-maven-central

# deploy to Maven Central
mvn -Pcentral deploy -DignorePublishedComponents=true

# set next development version
mvn -Pcentral versions:set -DnextSnapshot -DprocessAllModules

# persist version changes
mvn -Pcentral versions:commit -DprocessAllModules

# commit and push repository changes

# create GitHub release
https://github.com/teggr/electrostatic/releases
```

## Plugins

* https://www.mojohaus.org/versions/versions-maven-plugin/index.html
* https://maven.apache.org/plugins/maven-gpg-plugin/index.html
* https://kordamp.org/pomchecker/pomchecker-maven-plugin/index.html
* https://central.sonatype.org/publish/publish-portal-maven/

## Maintenance

* https://central.sonatype.com/publishing
