---
---

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>your.domain</groupId>
    <artifactId>new-electrostatic-site</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <properties>
        <!-- build properties -->
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <!-- versions -->
        <electrostatic.version>0.0.1-SNAPSHOT</electrostatic.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>website.electrostatic</groupId>
            <artifactId>electrostatic-core</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>website.electrostatic</groupId>
                <artifactId>electrostatic-maven-plugin</artifactId>
                <version>${project.version}</version>
            </plugin>
        </plugins>
    </build>

</project>
```