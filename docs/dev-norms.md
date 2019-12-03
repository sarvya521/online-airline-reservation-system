# Dev Guidelines

### Top-level directory layout

    ├── src
    │   ├── main
    │   │   ├── java
    │   │   ├── resources
    │   ├── test
    │   │   ├── java
    │   │   ├── resources
    ├── docs
    ├── pom.xml
    └── README.md

Once project is created from Spring Tools, you can see basic maven project is created for you with chosen spring dependencies. 
Under src/main/resources If your spring project is dealing with Spring Cloud, then use bootstrap.yml file for cloud related configurations. Other properties can be powered from application.yml file.
We will prefer .yml file in place of .properties file.
Refer [Package Structure](https://docs.google.com/document/d/1pc0mhAToil6K3kmknUdrpfayNYGkRwvrTzx1lElYP08/edit#) for more information.

### Git Branching Strategy

Refer [Branching-Strategy](https://docs.google.com/document/d/1zMBsAFxcE93J0lr98zwoV5Cy89-2u5xigpR_eG1Ugns/edit#heading=h.5x0d5h95i329)

###### Branch Naming Convention

Refer [Naming convention for feature branch guidelines](https://docs.google.com/document/d/1zMBsAFxcE93J0lr98zwoV5Cy89-2u5xigpR_eG1Ugns/edit#heading=h.awhel6pw88o3)

###### Commit Message Format

Refer [Naming convention for commit message guidelines](https://docs.google.com/document/d/1zMBsAFxcE93J0lr98zwoV5Cy89-2u5xigpR_eG1Ugns/edit#heading=h.aggiqwqdmqn5)

###### Pull Request

Refer [Incorporating a finished feature on develop (PR)](https://docs.google.com/document/d/1zMBsAFxcE93J0lr98zwoV5Cy89-2u5xigpR_eG1Ugns/edit#heading=h.aggiqwqdmqn5)

### Exception Handling

Refer - ControllerAdvice in `com.backend.boilerplate.exception` and `com.backend.boilerplate.web.exception`
	
### Unit Test Cases

We will use `junit 5` framework for normal test cases.
API/Controllers will be tested with `Mockito framework`. These API/Controllers should also cover their IntegrationTest with TestRestTemplate
Database testing can be done with in-memory database like h2 and test schema.
Test schema should be part of `src/test/resources`.
All util, service, dao and controller classes should have full test coverage.

### Logging

Refer - `com.backend.boilerplate.util.LoggerUtilities`

### HTTP Request Tracing
###### Micro Service
We use opentracing in micro services

###### Monolith
We use MDC or ThreadContext in Monolith.

Refer - [MDC in log4j2](https://www.baeldung.com/mdc-in-log4j-2-logback), [Log4j MDC in Spring Boot](https://medium.com/sipios/how-to-use-log4j-and-mdc-in-java-spring-boot-application-26b1a77f5c3e)
			
### Java Doc

Generate java documentation for every class and its members, Follow proper javadoc guidelines.
For Reference - [Spring Boot boilerplate](https://bitbucket.org/rochedis/ow-fhir-db-poc-scratch) project
	
### Common Code

For LoggerUtilites, Refer `com.backend.boilerplate.util.LoggerUtilities`

Related to exception handling, Refer `com.backend.boilerplate.exception`, `com.backend.boilerplate.web.exception`

Swagger Configuration, Refer `com.backend.boilerplate.config.Swagger2Config`

Cloud Configuration, Refer `bootstrap.yml`

Log4j Configuration, Refer `log4j2.yml`

For SynchronizedInMemoryCache, Refer `com.backend.boilerplate.util.SynchronizedInMemoryCache`
