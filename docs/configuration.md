# Configurations

### Important Maven Goals
Create maven goals from Eclipse. Below are some important goals you should know and you should configure them in your eclipse.

* Default maven clean command: `mvn clean`
* Default maven install command: `mvn install`
* Maven aggregate clean and package commands: `mvn clean package`
* Format all source files as per editorconfig: `mvn editorconfig:format`
* Generate javadoc: `mvn clean install -Ddelombok.skip=false -Djavadoc.skip=false`
* Install with Animal-Sniffer: `mvn clean install -Danimal-sniffer.skip=false`
* Install with Duplicate-Finder: `mvn clean install -Dduplicate-finder.skip=false`
* Install with Jacoco: `mvn clean install -Djacoco.skip=false`
* Install with Jacoco(exclude class/package): `mvn clean install -Djacoco.skip=false -Djacoco.exclude=<package/class path>`

### Swagger

For Configuration - Refer `com.backend.boilerplate.config.Swagger2Config` 

For API Documentation - Refer `com.backend.boilerplate.web.controller.AutoCompleteApiController`

Below are the required dependencies

```sh
<dependency>
  <groupId>io.springfox</groupId>
  <artifactId>springfox-swagger2</artifactId>
  <version>2.9.2</version>
</dependency>
<dependency>
  <groupId>io.springfox</groupId>
  <artifactId>springfox-swagger-ui</artifactId>
  <version>2.9.2</version>
</dependency>
```

### Log4j2
For Configuration - Refer `log4j2.yml`. 

Below are the required dependencies

```sh
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter</artifactId>
	<exclusions>
		<exclusion>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-logging</artifactId>
		</exclusion>
	</exclusions>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
<!-- for log4j2.yml, need jackson-databind and jackson-dataformat-yaml -->
<!-- spring-boot-starter-web -> spring-boot-starter-json -> jackson-databind -->
<!-- included by spring boot
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId> 
	<artifactId>jackson-databind</artifactId>
</dependency> -->
<dependency>
	<groupId>com.fasterxml.jackson.dataformat</groupId>
	<artifactId>jackson-dataformat-yaml</artifactId>
</dependency>
```

### LoggerUtilities
Below are the required dependencies

```sh
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-collections4</artifactId>
  <version>4.3</version>
</dependency>
<dependency>
  <groupId>org.owasp.encoder</groupId>
  <artifactId>encoder</artifactId>
  <version>1.2.2</version>
</dependency>
```

### Lombok
Below is the required dependency

```sh
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <optional>true</optional>
</dependency>
```
To install lombok eclipse plug-in, Refer [Lombok Plugin Setup Doc](./lombok-plugin.md)

### Junit5
Below are the required dependencies

```sh
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
  <exclusions>
    <exclusion>
      <groupId>org.junit.vintage</groupId>
      <artifactId>junit-vintage-engine</artifactId>
    </exclusion>
    <exclusion>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </exclusion>
  </exclusions>
</dependency>
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter-api</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter-engine</artifactId>
  <scope>test</scope>
</dependency>
```

### Spring Cloud Config
For Configuration - Refer `bootstrap.yml`

Below are the required dependencies

```sh
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

### Circuit Breaker
Below are the required dependencies

```sh
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

### Spring boot maven plugin
Add below plugin configuration in pom.xml

```sh
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <configuration>
    <executable>true</executable>
  </configuration>
  <executions>
    <execution>
      <id>generate build info</id>
      <goals>
        <goal>build-info</goal>
        <goal>repackage</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### Editor Config maven plugin
Add below plugin configuration in pom.xml

```sh
<plugin>
  <groupId>org.ec4j.maven</groupId>
  <artifactId>editorconfig-maven-plugin</artifactId>
  <version>0.0.10</version>
  <executions>
    <execution>
      <id>check</id>
      <phase>verify</phase>
      <goals>
        <goal>check</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <excludes>
      <exclude>eclipse-java-code-style*.xml</exclude>
      <exclude>docs/</exclude>
    </excludes>
  </configuration>
</plugin>
```
Add eclipse plug-in, Refer [EditorConfig](./java-code-format.md)

### JavaDocs maven plugin
Add below plugin configuration in pom.xml. This plugin depends on lombok-maven-plugin which will delombok and generate source files and then javadoc plugin will refer these source files for documentation.

This plugin will only be invoked by passing `-Djavadoc.skip=false` as a maven command argument. Default value of this configuration is true means it will not generate javadocs unless it is asked for.

```sh
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-javadoc-plugin</artifactId>
  <configuration>
    <charset>UTF-8</charset>
    <docencoding>UTF-8</docencoding>
    <destDir>docs/javadocs</destDir>
    <sourcepath>${project.basedir}/target/generated-sources/delombok</sourcepath>
    <skip>${maven.javadoc.skip}</skip>
  </configuration>
  <executions>
    <execution>
      <id>attach-javadocs</id>
      <goals>
        <goal>jar</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### Lombok maven plugin
Add below plugin configuration in pom.xml. It will delombok and generate source files in `/target/delombok` folder and then javadoc plugin will refer these source files for documentation.

This plugin will only be invoked by passing `-Ddelombok.skip=false` as a maven command argument. Default value of this configuration is true means it will not delombok unless it is asked for.

```sh
<plugin>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok-maven-plugin</artifactId>
  <version>1.18.6.0</version>
  <executions>
    <execution>
      <phase>generate-sources</phase>
      <goals>
        <goal>delombok</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <addOutputDirectory>false</addOutputDirectory>
    <sourceDirectory>${project.basedir}/src/main/java</sourceDirectory>
  </configuration>
</plugin>
```

### Animal Sniffer Maven Plugin
The Animal Sniffer Plugin is used to build signatures of APIs and to check your classes against previously generated signatures. This plugin is called animal sniffer because the principal signatures that are used are those of the Java Runtime, and since Sun traditionally names the different versions of its Java Runtimes after different animals, the plugin that detects what Java Runtime your code requires was called "Animal Sniffer".

See more on this plugin [here](https://www.mojohaus.org/animal-sniffer/animal-sniffer-maven-plugin/)

This plugin will only be invoked by passing `-Danimal-sniffer.skip=false` as a maven command argument. Default value of this configuration is true means it will not be executed unless it is asked for.

Add below plugin configuration in pom.xml.

```sh
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>animal-sniffer-maven-plugin</artifactId>
  <configuration>
    <skip>true</skip>
  </configuration>
</plugin>
```

### Duplicate-Finder Maven plugin
A maven plugin to find and flag duplicate classes and resources on the java classpath.

This ensures stability and reproducibility of a maven build and will flag possible problems or conflicts with dependencies in a project.

This plugin will only be invoked by passing `-Dduplicate-finder.skip=false` as a maven command argument. Default value of this configuration is true means it will not be executed unless it is asked for.

Add below plugin configuration in pom.xml.

```sh
<plugin>
  <groupId>org.basepom.maven</groupId>
  <artifactId>duplicate-finder-maven-plugin</artifactId>
  <configuration>
    <skip>${duplicate-finder.skip}</skip>
  </configuration>
</plugin>
```

### JaCoCo Maven plug-in

The JaCoCo Maven plug-in provides the JaCoCo runtime agent to your tests and allows basic report creation. [Refer](https://www.jacoco.org/jacoco/trunk/doc/maven.html) for more details

* Install with Jacoco: `mvn clean install -Djacoco.skip=false`
* Install with Jacoco(exclude class/package): `mvn clean install -Djacoco.skip=false -Djacoco.exclude=<package/class path>`

Example:
* Exclude Specific Package: `mvn clean install -Djacoco.skip=false -Djacoco.exclude=com/ac/util/`
* Exclude Specific Class: `mvn clean install -Djacoco.skip=false -Djacoco.exclude=com/ac/util/LoggerUtilities`

Add below plugin configuration in pom.xml. With below configuration, build will fail if it violate below rules
* All methods in a class should provide the test coverage
* Minimum of 60% test code coverage for each method 

```sh
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.4</version>
  <configuration>
    <skip>${jacoco.skip}</skip>
    <includes>
      <include>com/ac/dao/*</include>
      <include>com/ac/service/*</include>
      <include>com/ac/util/*</include>
      <include>com/ac/web/controller/*</include>
    </includes>
    <excludes>
      <exclude>${jacoco.exclude}*</exclude>
    </excludes>
  </configuration>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals>
        <goal>report</goal>
      </goals>
      <configuration>
        <outputDirectory>docs/jacoco</outputDirectory>
      </configuration>
    </execution>
    <execution>
      <id>check</id>
      <goals>
        <goal>check</goal>
      </goals>
      <configuration>
        <rules>
          <rule>
            <element>CLASS</element>
            <limits>
              <limit>
                <counter>METHOD</counter>
                <value>MISSEDCOUNT</value>
                <maximum>0</maximum>
              </limit>
            </limits>
          </rule>
          <rule>
            <element>METHOD</element>
            <limits>
              <limit>
                <counter>LINE</counter>
                <value>COVEREDRATIO</value>
                <minimum>.6</minimum>
              </limit>
            </limits>
          </rule>
        </rules>
      </configuration>
    </execution>
  </executions>
</plugin>
```
