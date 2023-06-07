# Day 3

## What is Configuration Management Tool?
- Example - Ansible, Puppet, Chef, Salt/SaltStack 
- They would be used mostly by Administrators, however also beneficial to Developers and QA Engineers to automate software installations on an existing OS/VM/ec2-instance/Azure VM
- it supports automating software installations on a Docker, on-prem servers, private cloud, public cloud and even hybrid cloud
- software installation automations can be done on Windows/Unix/Linux/Mac
- They also have some minimal provisioning capabilities

## What is a Provisioner Tool?
- Example - Terraform, Cloudformation, etc.,
- They would be used mostly by Administrators, however dev/qa team also would be using these tools
- It helps in creating a new Container, Virtual Machine, installing OS on On-Prem machines, private/hybrid/public cloud environments
- They also have some minimal configuration management capabilities

## What is the difference between Provisioner Tool and Configuration Management Tool?
Provisioners are used to create a new VM with a particular Operating System, with specific Network configuration, with specific storage configurations etc.,

On a already provisioned machine, if you wish to install additional softwares then Configuration Management Tools like Ansible, Puppet, Chef or Salt can be used.

## When Ansible can also provision, what is the need to use a provisioner tool like Terraform/Cloudformation?
Ansible strength is Configuration Management but Provisioning. Hence, we should ideally use a Provisioner tool to create a VM as opposed to Ansible or similar configuration management tools.

## When Terraform can also perform configuration management, what is the motivation to use a configuration management tool like Ansible?
Terraform's strenth is Provisioning, weakness is Configuration Management.  Terraform supports scripting languages to perform simple software installations, for complex software installations we shouldn't use Terraform. Ideally we should Configuration management tools, as they can easily handle very complex software installation and configurations all in a declarative way.
