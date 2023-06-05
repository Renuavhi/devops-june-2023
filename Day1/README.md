# Day 1

## What is DevOps?

- Test Driven Devopment 
  - Java
    - Junit, Mockito, TestNg, PowerMockito(Not Recommended)
  - C#
    - NUnit, Moq
  - C/C++
    - CppUnit, CUnit
    - Google Test & Google Mock
  - Python
    - Pytest
- Behavior Driven Development
   - Java
     - Cucumber
   - C++
     - Cucumber
   - C#
     - Specflow
   - JavaScript
     - Jasmine, Karma
   - Python
- Tools used by Operations Team ( Administrators to automate )
  - Configuration Management Tools ( Ansible, Puppet, Chef, Salt/Saltstack, etc.,)
  - Infrastructure as a Code Tools (Terraform, Cloudformation, etc.,)

## What is Continuous Integration (CI)?

## What is Continuous Deployment (CD)?

## What is Continuous Delivery (CD)?

# Maven Overview
- a build tool used in Java based projects
- this is an alternate tool for Apache Ant
- an opensource tool from Apache Foundation
- is programming agnostic/independent tool
- however used mostly by Java based project
- Supports Dependency Management
- Maven has conventions for everything
  - has conventions on how to name your project
  - has conventions on project directory structure
  - has conventions for versioning your artifacts (components)
- Convention Over Configuration
  - Based on 80-20 Principle
  - In Maven, the most commonly used features can be utilized with less/no extra configuration
  - at the same time, the advanced scenarios are also supposed with some extra configuration
- Maven co-ordinates
  - ArtifactId ( name of the jar/war/ear/zip )
  - GroupId ( similar to package/namespace - usually your organization reverse domain name )
  - Version ( 1.2.3 )
    - 1 represents the major version
    - 2 represents the minor version
    - 3 represents the incremental version

# Maven Commands

## Finding the maven version
```
mvn --version
```

Expected output
<pre>
[jegan@tektutor.org devops-june-2023]$ <b>mvn --version</b>
Apache Maven 3.6.3 (Red Hat 3.6.3-10.3)
Maven home: /usr/share/maven
Java version: 11.0.18, vendor: Red Hat, Inc., runtime: /usr/lib/jvm/java-11-openjdk-11.0.18.0.10-2.el8_7.x86_64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "4.18.0-425.10.1.el8_7.x86_64", arch: "amd64", family: "unix"
</pre>
