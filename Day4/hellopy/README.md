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
