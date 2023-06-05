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

## Cloning TekTutor Training Repository
```
cd ~
git clone https://github.com/tektutor/devops-june-2023.git
cd devops-june-2023
```

## Lab - Validate the hello pom.xml
```
cd ~/devops-june-2023
git pull
cd Day1/hello

mvn validate
```

Expected output
<pre>
[jegan@tektutor.org hello]$ <b>mvn validate</b>
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------< org.tektutor:tektutor-hello-app >-------------------
[INFO] Building tektutor-hello-app 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.032 s
[INFO] Finished at: 2023-06-05T13:00:31+05:30
[INFO] ------------------------------------------------------------------------
</pre>

## Lab - Compiling the Hello maven project
```
cd ~/devops-june-2023
git pull

cd Day1/hello
mvn compile
```

Expected output
<pre>
[jegan@tektutor.org hello]$ <b>mvn compile</b>
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------< org.tektutor:tektutor-hello-app >-------------------
[INFO] Building tektutor-hello-app 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ tektutor-hello-app ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /home/jegan/devops-june-2023/Day1/hello/src/main/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ tektutor-hello-app ---
[INFO] Changes detected - recompiling the module!
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[INFO] Compiling 1 source file to /home/jegan/devops-june-2023/Day1/hello/target/classes
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
[ERROR] Source option 5 is no longer supported. Use 6 or later.
[ERROR] Target option 1.5 is no longer supported. Use 1.6 or later.
[INFO] 2 errors 
[INFO] -------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.318 s
[INFO] Finished at: 2023-06-05T13:01:21+05:30
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:compile (default-compile) on project tektutor-hello-app: Compilation failure: Compilation failure: 
[ERROR] Source option 5 is no longer supported. Use 6 or later.
[ERROR] Target option 1.5 is no longer supported. Use 1.6 or later.
[ERROR] -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
</pre>

The root cause of the above problem is that, my maven version i.e 3.6.3 makes use of maven-compiler-plugin version 3.1 which was release in year 2013.  Hence, the maven-compiler-plugin seems to restrict the java compiler feature set to JDK/JRE version 1.5.

So we could solve the issue in two different ways.
1. We could instruct the maven-compiler-plugin to use JDK/JRE( source and target compiler switches ) to 1.8.
2. Alternatively, we could upgrade the maven-compiler-plugin to latest available in the maven central repository which will resolve the problem in your current project.

After the fix, maven compile output looks as shown below
<pre>
[jegan@tektutor.org hello]$ <b>mvn compile</b>
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------< org.tektutor:tektutor-hello-app >-------------------
[INFO] Building tektutor-hello-app 1.0.0
Downloading from central: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-utils/3.4.1/plexus-utils-3.4.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/maven/shared/maven-shared-utils/3.3.4/maven-shared-utils-3.3.4.jar (153 kB at 517 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-compiler-manager/2.11.1/plexus-compiler-manager-2.11.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-compiler-api/2.11.1/plexus-compiler-api-2.11.1.jar (27 kB at 83 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-compiler-javac/2.11.1/plexus-compiler-javac-2.11.1.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/thoughtworks/qdox/qdox/2.0.1/qdox-2.0.1.jar (334 kB at 810 kB/s)
Downloaded from central: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-compiler-javac/2.11.1/plexus-compiler-javac-2.11.1.jar (23 kB at 55 kB/s)
Downloaded from central: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-utils/3.4.1/plexus-utils-3.4.1.jar (264 kB at 583 kB/s)
Downloaded from central: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-compiler-manager/2.11.1/plexus-compiler-manager-2.11.1.jar (4.7 kB at 9.4 kB/s)
Downloaded from central: https://repo.maven.apache.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar (215 kB at 269 kB/s)
[INFO] Changes detected - recompiling the module!
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[INFO] Compiling 1 source file to /home/jegan/devops-june-2023/Day1/hello/target/classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.664 s
[INFO] Finished at: 2023-06-05T13:19:55+05:30
[INFO] ------------------------------------------------------------------------

</pre>
