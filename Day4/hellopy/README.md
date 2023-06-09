# Day 4

## Jenkins Overview
- CI/CD Build Server
- comes in 2 flavors
  1. Jenkins ( opensource )
  2. Cloudbees ( Enterprise Vesion of Jenkins )
- this was developed Kosuke Kavaguchi, he was former Sun Microsystems employee, he developed this tool in the name Hudson
- developed in Java but Jenkins can be used in projects developed in any programming language stack

## Jenkins Alternatives
- Hudson
- Bamboo
- TeamCity
- Microsoft TFS

## Configure Jenkins Docker Plugin
<pre>
https://medium.com/tektutor/ci-cd-with-maven-github-docker-jenkins-aca28c252fec
</pre>

## Install Python builder
```
sudo yum install -y python3-pip
sudo pip3 install pybuilder
sudo pip3 install coverage
```

## Running the python build 
```
cd ~/devops-june-2023
git pull

cd Day4/hellopy
pyb
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day4/hellopy$ <b>pyb</b>
PyBuilder version 0.13.9
Build started at 2023-06-09 10:34:19
------------------------------------------------------------
[INFO]  Building hellopy version 1.0.dev0
[INFO]  Executing build in /home/jegan/devops-june-2023/Day4/hellopy
[INFO]  Going to execute task publish
[INFO]  Processing plugin packages 'coverage~=6.0' to be installed with {'upgrade': True}
[INFO]  Processing plugin packages 'flake8~=4.0' to be installed with {'upgrade': True}
[INFO]  Processing plugin packages 'pypandoc~=1.4' to be installed with {'upgrade': True}
[INFO]  Processing plugin packages 'setuptools>=38.6.0' to be installed with {}
[INFO]  Processing plugin packages 'twine>=1.15.0' to be installed with {'upgrade': True}
[INFO]  Processing plugin packages 'unittest-xml-reporting~=3.0.4' to be installed with {'upgrade': True}
[INFO]  Processing plugin packages 'wheel>=0.34.0' to be installed with {}
[INFO]  Creating target 'build' VEnv in '/home/jegan/devops-june-2023/Day4/hellopy/target/venv/build/cpython-3.10.6.final.0'
[INFO]  Creating target 'test' VEnv in '/home/jegan/devops-june-2023/Day4/hellopy/target/venv/test/cpython-3.10.6.final.0'
[INFO]  Requested coverage for tasks: pybuilder.plugins.python.unittest_plugin:run_unit_tests
[INFO]  Running unit tests
[INFO]  Executing unit tests from Python modules in /home/jegan/devops-june-2023/Day4/hellopy/src/unittest/python
Hello constructor
[INFO]  Executed 1 unit tests
[INFO]  All unit tests passed.
[INFO]  Building distribution in /home/jegan/devops-june-2023/Day4/hellopy/target/dist/hellopy-1.0.dev0
[INFO]  Copying scripts to /home/jegan/devops-june-2023/Day4/hellopy/target/dist/hellopy-1.0.dev0/scripts
[INFO]  Writing setup.py as /home/jegan/devops-june-2023/Day4/hellopy/target/dist/hellopy-1.0.dev0/setup.py
[INFO]  Collecting coverage information for 'pybuilder.plugins.python.unittest_plugin:run_unit_tests'
[WARN]  ut_coverage_branch_threshold_warn is 0 and branch coverage will not be checked
[WARN]  ut_coverage_branch_partial_threshold_warn is 0 and partial branch coverage will not be checked
[INFO]  Running unit tests
[INFO]  Executing unit tests from Python modules in /home/jegan/devops-june-2023/Day4/hellopy/src/unittest/python
Hello constructor
[INFO]  Executed 1 unit tests
[INFO]  All unit tests passed.
[INFO]  Overall pybuilder.plugins.python.unittest_plugin.run_unit_tests coverage is 75%
[INFO]  Overall pybuilder.plugins.python.unittest_plugin.run_unit_tests branch coverage is 50%
[INFO]  Overall pybuilder.plugins.python.unittest_plugin.run_unit_tests partial branch coverage is 50%
[INFO]  Overall hellopy coverage is 75%
[INFO]  Overall hellopy branch coverage is 50%
[INFO]  Overall hellopy partial branch coverage is 50%
[INFO]  Building binary distribution in /home/jegan/devops-june-2023/Day4/hellopy/target/dist/hellopy-1.0.dev0
[INFO]  Running Twine check for generated artifacts
------------------------------------------------------------
BUILD SUCCESSFUL
------------------------------------------------------------
Build Summary
             Project: hellopy
             Version: 1.0.dev0
      Base directory: /home/jegan/devops-june-2023/Day4/hellopy
        Environments: 
               Tasks: prepare [3891 ms] compile_sources [0 ms] run_unit_tests [267 ms] package [4 ms] run_integration_tests [0 ms] verify [0 ms] coverage [382 ms] publish [1272 ms]
Build finished at 2023-06-09 10:34:26
Build took 6 seconds (6765 ms)
</pre>
