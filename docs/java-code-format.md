# Getting Started
This document is covering all guidelines for code formatting using code style and editorconfig

### Guides
###### The following guides illustrate how to use code style feature:
* Import **eclipse-java-code-style-{version}.xml** in java->code-style->formatter and then save this as a new profile.
* To apply formatting to all files in a project:
  * activate java facet for project facet: project-properties->project facets->check java
  * right-clicking on top of the PACKAGE-EXPLORER you will find the SOURCE - FORMAT option, this will format all files in your project

[HOW-TO-FORMAT-ALL-JAVA-FILES-IN-AN-ECLIPSE-PROJECT-AT-ONE-TIME](https://stackoverflow.com/questions/5133781/how-to-format-all-java-files-in-an-eclipse-project-at-one-time)

![HOW-TO-FORMAT-ALL-FILES-JAVA-FACET](https://i.stack.imgur.com/eAJoc.png)

![HOW-TO-FORMAT-ALL-FILES](https://i.stack.imgur.com/YQcwT.png)

###### The following guides illustrate how to use editorconfig feature:
* Install editorconfig-eclipse plugin from eclipse marketplace.
* **.editorconfig** file is present in project root folder. We have also specified editorconfig-maven-plugin in pom.xml which checks for file formatting according to mentioned rules and if files are not formatted accordingly then maven build will fail. 
* You can fix all file formatting using command **mvn editorconfig:format** or you can create a new goal in eclipse maven configuration for your project.