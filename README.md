# Spring Boot Guidelines
This spring boot project has covered standard coding practices.
## Things covered in this project
* Project Documentation. Refer [docs folder](docs)

## Prerequisites
* Java 11
* Eclipse 2019-03
* Eclipse Plugins
    * Sonarlint
    * STS 4
    * editorconfig
    * lombok

### Tech/frameworks used
* Spring-boot cloud: for cloud config 
* Swagger: for REST API end-points documentation and self-service browser interface
* Log4J2: for logging with roll out configuration
* Junit5: for Unit Testing
* Lombok: It is a java library that automatically plugs into your editor and build tools, spicing up your java
* flyway: for database migration and versioning

## Setting up Dev
1. Clone the repository with SSH - git clone https://github.com/sarvya521/springboot-boilerplate.git
2. Import eclipse code formatter - Refer java-code-format.md
3. Install Lombok, Refer lombok-plugin.md

### Build
Create a maven goal in eclipse using below steps.
	
1. Right click the project, select "Run as → Maven build..." (notice the "..." at the end)
2. Enter the Goals: clean install
3. Select Apply, then Run
4. The build output will be displayed in the Console.

### Run the application
Start the application from the Boot Dashboard (Window -> Show View -> Boot Dashboard)
