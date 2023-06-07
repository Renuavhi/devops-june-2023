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

## Ansible High Level Architecture

## Ansible Overview
- comes in 3 flavors
  1. Ansile Core ( Open-source, supports only CLI )
  2. AWX - Opensource product that supports Web Interface, built on top Ansible Core
  3. Red Hat Ansible Tower ( Enterprise Edition built on top of opensource AWX, requires license, support backed by Red Hat - an IBM company )
- is an opensource configuration management tool
- developed in Python by Ansible Inc organization incorported by Michael Deehan
- Michael Deehan was former employee of Red Hat
- As Ansible gained traction, Red Hat acquired Ansible Inc, hence Ansible core and Ansible Tower are both Red Products
- Ansible is agent-less, meaning we don't have to install any Ansible proprietary tools on the Ansible Node where software installation automation must be done
- the machine where Ansible is installed is called Ansible Controller Machine(ACM), this can only be a Linux machine
- On Unix/Linux/Mac based Ansible nodes, we must ensure Python and SSH Servers is installed
- On Windows Ansible nodes, we must ensure Powershell and WinRm is installed

# Ansible Commands

## Finding ansible version
```
ansible --version
```

Expected output
<pre>
jegan@tektutor:~/Desktop$ ansible --version
ansible [core 2.15.0]
  config file = None
  configured module search path = ['/home/jegan/.ansible/plugins/modules', '/usr/share/ansible/plugins/modules']
  ansible python module location = /home/jegan/.local/lib/python3.10/site-packages/ansible
  ansible collection location = /home/jegan/.ansible/collections:/usr/share/ansible/collections
  executable location = /home/jegan/.local/bin/ansible
  python version = 3.10.6 (main, Mar 10 2023, 10:55:28) [GCC 11.3.0] (/usr/bin/python3)
  jinja version = 3.1.2
  libyaml = True
</pre>

