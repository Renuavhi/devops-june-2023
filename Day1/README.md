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
