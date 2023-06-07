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

## Lab - Creating a Custom Ubuntu Ansible Node Image

Generate a key-pair as rps user in the terminal
```
ssh-keygen
```
Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/ubuntu$ <b>ssh-keygen</b>
Generating public/private rsa key pair.
Enter file in which to save the key (/home/jegan/.ssh/id_rsa): 
Enter passphrase (empty for no passphrase): 
Enter same passphrase again: 
Your identification has been saved in /home/jegan/.ssh/id_rsa
Your public key has been saved in /home/jegan/.ssh/id_rsa.pub
The key fingerprint is:
SHA256:qtaD4ToVIun0Pa820XZI+BRKA1UBNzSZSNFI0LlKnmw jegan@tektutor
The key's randomart image is:
+---[RSA 3072]----+
|   oB*XB+        |
|     B.=o        |
| .  . = .        |
|o....+ o         |
|o.=.+.= S        |
| . Eo+ * .       |
|  .o +* .        |
|  . ++o.         |
|  .+o.o.         |
+----[SHA256]-----+
</pre>

```
cd ~/devops-june-2023
git pull

cd Day3/
cd CustomDockerImagesForAnsibleNodes/ubuntu
cp ~/.ssh/id_rsa.pub authorized_keys

docker build -t tektutor/ansible-ubuntu-node:latest .
docker images
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/ubuntu$ <b>cp /home/jegan/.ssh/id_rsa.pub authorized_keys</b>
jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/ubuntu$ <b>ls</b>
authorized_keys  Dockerfile

jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/ubuntu$ <b>docker build -t tektutor/ansible-ubuntu-node:latest .</b>
Sending build context to Docker daemon  4.096kB
Step 1/12 : FROM ubuntu:16.04
16.04: Pulling from library/ubuntu
58690f9b18fc: Pull complete 
b51569e7c507: Pull complete 
da8ef40b9eca: Pull complete 
fb15d46c38dc: Pull complete 
Digest: sha256:1f1a2d56de1d604801a9671f301190704c25d604a416f59e03c04f5c6ffee0d6
Status: Downloaded newer image for ubuntu:16.04
 ---> b6f507652425
Step 2/12 : MAINTAINER Jeganathan Swaminathan <jegan@tektutor.org>
 ---> Running in 9793a6ecc174
Removing intermediate container 9793a6ecc174
 ---> 2836df7e0f16
Step 3/12 : RUN apt-get update && apt-get install -y openssh-server python3
 ---> Running in 3aa0dcf1b6c7
Get:1 http://security.ubuntu.com/ubuntu xenial-security InRelease [99.8 kB]
Get:2 http://archive.ubuntu.com/ubuntu xenial InRelease [247 kB]
Get:3 http://security.ubuntu.com/ubuntu xenial-security/main amd64 Packages [2051 kB]
Get:4 http://archive.ubuntu.com/ubuntu xenial-updates InRelease [99.8 kB]
Get:5 http://archive.ubuntu.com/ubuntu xenial-backports InRelease [97.4 kB]
Get:6 http://archive.ubuntu.com/ubuntu xenial/main amd64 Packages [1558 kB]
Get:7 http://archive.ubuntu.com/ubuntu xenial/restricted amd64 Packages [14.1 kB]
Get:8 http://archive.ubuntu.com/ubuntu xenial/universe amd64 Packages [9827 kB]
Get:9 http://security.ubuntu.com/ubuntu xenial-security/restricted amd64 Packages [15.9 kB]
Get:10 http://security.ubuntu.com/ubuntu xenial-security/universe amd64 Packages [985 kB]
Get:11 http://security.ubuntu.com/ubuntu xenial-security/multiverse amd64 Packages [8820 B]
Get:12 http://archive.ubuntu.com/ubuntu xenial/multiverse amd64 Packages [176 kB]
Get:13 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 Packages [2560 kB]
Get:14 http://archive.ubuntu.com/ubuntu xenial-updates/restricted amd64 Packages [16.4 kB]
Get:15 http://archive.ubuntu.com/ubuntu xenial-updates/universe amd64 Packages [1545 kB]
Get:16 http://archive.ubuntu.com/ubuntu xenial-updates/multiverse amd64 Packages [26.2 kB]
Get:17 http://archive.ubuntu.com/ubuntu xenial-backports/main amd64 Packages [10.9 kB]
Get:18 http://archive.ubuntu.com/ubuntu xenial-backports/universe amd64 Packages [12.7 kB]
Fetched 19.4 MB in 3s (5622 kB/s)
Reading package lists...
Reading package lists...
Building dependency tree...
Reading state information...
The following additional packages will be installed:
  ca-certificates dh-python file krb5-locales libbsd0 libedit2 libexpat1
  libgssapi-krb5-2 libidn11 libk5crypto3 libkeyutils1 libkrb5-3
  libkrb5support0 libmagic1 libmpdec2 libpython3-stdlib libpython3.5-minimal
  libpython3.5-stdlib libsqlite3-0 libssl1.0.0 libwrap0 libx11-6 libx11-data
  libxau6 libxcb1 libxdmcp6 libxext6 libxmuu1 mime-support ncurses-term
  openssh-client openssh-sftp-server openssl python3-chardet python3-minimal
  python3-pkg-resources python3-requests python3-six python3-urllib3 python3.5
  python3.5-minimal ssh-import-id tcpd wget xauth
Suggested packages:
  libdpkg-perl krb5-doc krb5-user ssh-askpass libpam-ssh keychain monkeysphere
  rssh molly-guard ufw python3-doc python3-tk python3-venv python3-setuptools
  python3-ndg-httpsclient python3-openssl python3-pyasn1 python3.5-venv
  python3.5-doc binutils binfmt-support
The following NEW packages will be installed:
  ca-certificates dh-python file krb5-locales libbsd0 libedit2 libexpat1
  libgssapi-krb5-2 libidn11 libk5crypto3 libkeyutils1 libkrb5-3
  libkrb5support0 libmagic1 libmpdec2 libpython3-stdlib libpython3.5-minimal
  libpython3.5-stdlib libsqlite3-0 libssl1.0.0 libwrap0 libx11-6 libx11-data
  libxau6 libxcb1 libxdmcp6 libxext6 libxmuu1 mime-support ncurses-term
  openssh-client openssh-server openssh-sftp-server openssl python3
  python3-chardet python3-minimal python3-pkg-resources python3-requests
  python3-six python3-urllib3 python3.5 python3.5-minimal ssh-import-id tcpd
  wget xauth
0 upgraded, 47 newly installed, 0 to remove and 0 not upgraded.
Need to get 10.5 MB of archives.
After this operation, 55.0 MB of additional disk space will be used.
Get:1 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libssl1.0.0 amd64 1.0.2g-1ubuntu4.20 [1083 kB]
Get:2 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libpython3.5-minimal amd64 3.5.2-2ubuntu0~16.04.13 [524 kB]
Get:3 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libexpat1 amd64 2.1.0-7ubuntu0.16.04.5 [71.5 kB]
Get:4 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 python3.5-minimal amd64 3.5.2-2ubuntu0~16.04.13 [1597 kB]
Get:5 http://archive.ubuntu.com/ubuntu xenial/main amd64 python3-minimal amd64 3.5.1-3 [23.3 kB]
Get:6 http://archive.ubuntu.com/ubuntu xenial/main amd64 mime-support all 3.59ubuntu1 [31.0 kB]
Get:7 http://archive.ubuntu.com/ubuntu xenial/main amd64 libmpdec2 amd64 2.4.2-1 [82.6 kB]
Get:8 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libsqlite3-0 amd64 3.11.0-1ubuntu1.5 [398 kB]
Get:9 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libpython3.5-stdlib amd64 3.5.2-2ubuntu0~16.04.13 [2135 kB]
Get:10 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 python3.5 amd64 3.5.2-2ubuntu0~16.04.13 [165 kB]
Get:11 http://archive.ubuntu.com/ubuntu xenial/main amd64 libpython3-stdlib amd64 3.5.1-3 [6818 B]
Get:12 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 dh-python all 2.20151103ubuntu1.2 [73.9 kB]
Get:13 http://archive.ubuntu.com/ubuntu xenial/main amd64 python3 amd64 3.5.1-3 [8710 B]
Get:14 http://archive.ubuntu.com/ubuntu xenial/main amd64 libxau6 amd64 1:1.0.8-1 [8376 B]
Get:15 http://archive.ubuntu.com/ubuntu xenial/main amd64 libxdmcp6 amd64 1:1.1.2-1.1 [11.0 kB]
Get:16 http://archive.ubuntu.com/ubuntu xenial/main amd64 libxcb1 amd64 1.11.1-1ubuntu1 [40.0 kB]
Get:17 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libx11-data all 2:1.6.3-1ubuntu2.2 [114 kB]
Get:18 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libx11-6 amd64 2:1.6.3-1ubuntu2.2 [572 kB]
Get:19 http://archive.ubuntu.com/ubuntu xenial/main amd64 libxext6 amd64 2:1.3.3-1 [29.4 kB]
Get:20 http://archive.ubuntu.com/ubuntu xenial/main amd64 libwrap0 amd64 7.6.q-25 [46.2 kB]
Get:21 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libmagic1 amd64 1:5.25-2ubuntu1.4 [216 kB]
Get:22 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 file amd64 1:5.25-2ubuntu1.4 [21.2 kB]
Get:23 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libbsd0 amd64 0.8.2-1ubuntu0.1 [42.0 kB]
Get:24 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libidn11 amd64 1.32-3ubuntu1.2 [46.5 kB]
Get:25 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 openssl amd64 1.0.2g-1ubuntu4.20 [492 kB]
Get:26 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 ca-certificates all 20210119~16.04.1 [148 kB]
Get:27 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 krb5-locales all 1.13.2+dfsg-5ubuntu2.2 [13.7 kB]
Get:28 http://archive.ubuntu.com/ubuntu xenial/main amd64 libedit2 amd64 3.1-20150325-1ubuntu2 [76.5 kB]
Get:29 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libkrb5support0 amd64 1.13.2+dfsg-5ubuntu2.2 [31.2 kB]
Get:30 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libk5crypto3 amd64 1.13.2+dfsg-5ubuntu2.2 [81.2 kB]
Get:31 http://archive.ubuntu.com/ubuntu xenial/main amd64 libkeyutils1 amd64 1.5.9-8ubuntu1 [9904 B]
Get:32 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libkrb5-3 amd64 1.13.2+dfsg-5ubuntu2.2 [273 kB]
Get:33 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 libgssapi-krb5-2 amd64 1.13.2+dfsg-5ubuntu2.2 [120 kB]
Get:34 http://archive.ubuntu.com/ubuntu xenial/main amd64 libxmuu1 amd64 2:1.1.2-2 [9674 B]
Get:35 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 openssh-client amd64 1:7.2p2-4ubuntu2.10 [590 kB]
Get:36 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 wget amd64 1.17.1-1ubuntu1.5 [299 kB]
Get:37 http://archive.ubuntu.com/ubuntu xenial/main amd64 xauth amd64 1:1.0.9-1ubuntu2 [22.7 kB]
Get:38 http://archive.ubuntu.com/ubuntu xenial/main amd64 ncurses-term all 6.0+20160213-1ubuntu1 [249 kB]
Get:39 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 openssh-sftp-server amd64 1:7.2p2-4ubuntu2.10 [38.8 kB]
Get:40 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 openssh-server amd64 1:7.2p2-4ubuntu2.10 [335 kB]
Get:41 http://archive.ubuntu.com/ubuntu xenial/main amd64 python3-pkg-resources all 20.7.0-1 [79.0 kB]
Get:42 http://archive.ubuntu.com/ubuntu xenial/main amd64 python3-chardet all 2.3.0-2 [96.2 kB]
Get:43 http://archive.ubuntu.com/ubuntu xenial/main amd64 python3-six all 1.10.0-3 [11.0 kB]
Get:44 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 python3-urllib3 all 1.13.1-2ubuntu0.16.04.4 [58.6 kB]
Get:45 http://archive.ubuntu.com/ubuntu xenial-updates/main amd64 python3-requests all 2.9.1-3ubuntu0.1 [55.8 kB]
Get:46 http://archive.ubuntu.com/ubuntu xenial/main amd64 tcpd amd64 7.6.q-25 [23.0 kB]
Get:47 http://archive.ubuntu.com/ubuntu xenial/main amd64 ssh-import-id all 5.5-0ubuntu1 [10.2 kB]
debconf: delaying package configuration, since apt-utils is not installed
Fetched 10.5 MB in 6s (1520 kB/s)
Selecting previously unselected package libssl1.0.0:amd64.
(Reading database ... 4785 files and directories currently installed.)
Preparing to unpack .../libssl1.0.0_1.0.2g-1ubuntu4.20_amd64.deb ...
Unpacking libssl1.0.0:amd64 (1.0.2g-1ubuntu4.20) ...
Selecting previously unselected package libpython3.5-minimal:amd64.
Preparing to unpack .../libpython3.5-minimal_3.5.2-2ubuntu0~16.04.13_amd64.deb ...
Unpacking libpython3.5-minimal:amd64 (3.5.2-2ubuntu0~16.04.13) ...
Selecting previously unselected package libexpat1:amd64.
Preparing to unpack .../libexpat1_2.1.0-7ubuntu0.16.04.5_amd64.deb ...
Unpacking libexpat1:amd64 (2.1.0-7ubuntu0.16.04.5) ...
Selecting previously unselected package python3.5-minimal.
Preparing to unpack .../python3.5-minimal_3.5.2-2ubuntu0~16.04.13_amd64.deb ...
Unpacking python3.5-minimal (3.5.2-2ubuntu0~16.04.13) ...
Selecting previously unselected package python3-minimal.
Preparing to unpack .../python3-minimal_3.5.1-3_amd64.deb ...
Unpacking python3-minimal (3.5.1-3) ...
Selecting previously unselected package mime-support.
Preparing to unpack .../mime-support_3.59ubuntu1_all.deb ...
Unpacking mime-support (3.59ubuntu1) ...
Selecting previously unselected package libmpdec2:amd64.
Preparing to unpack .../libmpdec2_2.4.2-1_amd64.deb ...
Unpacking libmpdec2:amd64 (2.4.2-1) ...
Selecting previously unselected package libsqlite3-0:amd64.
Preparing to unpack .../libsqlite3-0_3.11.0-1ubuntu1.5_amd64.deb ...
Unpacking libsqlite3-0:amd64 (3.11.0-1ubuntu1.5) ...
Selecting previously unselected package libpython3.5-stdlib:amd64.
Preparing to unpack .../libpython3.5-stdlib_3.5.2-2ubuntu0~16.04.13_amd64.deb ...
Unpacking libpython3.5-stdlib:amd64 (3.5.2-2ubuntu0~16.04.13) ...
Selecting previously unselected package python3.5.
Preparing to unpack .../python3.5_3.5.2-2ubuntu0~16.04.13_amd64.deb ...
Unpacking python3.5 (3.5.2-2ubuntu0~16.04.13) ...
Selecting previously unselected package libpython3-stdlib:amd64.
Preparing to unpack .../libpython3-stdlib_3.5.1-3_amd64.deb ...
Unpacking libpython3-stdlib:amd64 (3.5.1-3) ...
Selecting previously unselected package dh-python.
Preparing to unpack .../dh-python_2.20151103ubuntu1.2_all.deb ...
Unpacking dh-python (2.20151103ubuntu1.2) ...
Processing triggers for libc-bin (2.23-0ubuntu11.3) ...
Setting up libssl1.0.0:amd64 (1.0.2g-1ubuntu4.20) ...
debconf: unable to initialize frontend: Dialog
debconf: (TERM is not set, so the dialog frontend is not usable.)
debconf: falling back to frontend: Readline
debconf: unable to initialize frontend: Readline
debconf: (Can't locate Term/ReadLine.pm in @INC (you may need to install the Term::ReadLine module) (@INC contains: /etc/perl /usr/local/lib/x86_64-linux-gnu/perl/5.22.1 /usr/local/share/perl/5.22.1 /usr/lib/x86_64-linux-gnu/perl5/5.22 /usr/share/perl5 /usr/lib/x86_64-linux-gnu/perl/5.22 /usr/share/perl/5.22 /usr/local/lib/site_perl /usr/lib/x86_64-linux-gnu/perl-base .) at /usr/share/perl5/Debconf/FrontEnd/Readline.pm line 7.)
debconf: falling back to frontend: Teletype
Setting up libpython3.5-minimal:amd64 (3.5.2-2ubuntu0~16.04.13) ...
Setting up libexpat1:amd64 (2.1.0-7ubuntu0.16.04.5) ...
Setting up python3.5-minimal (3.5.2-2ubuntu0~16.04.13) ...
Setting up python3-minimal (3.5.1-3) ...
Processing triggers for libc-bin (2.23-0ubuntu11.3) ...
Selecting previously unselected package python3.
(Reading database ... 5761 files and directories currently installed.)
Preparing to unpack .../python3_3.5.1-3_amd64.deb ...
Unpacking python3 (3.5.1-3) ...
Selecting previously unselected package libxau6:amd64.
Preparing to unpack .../libxau6_1%3a1.0.8-1_amd64.deb ...
Unpacking libxau6:amd64 (1:1.0.8-1) ...
Selecting previously unselected package libxdmcp6:amd64.
Preparing to unpack .../libxdmcp6_1%3a1.1.2-1.1_amd64.deb ...
Unpacking libxdmcp6:amd64 (1:1.1.2-1.1) ...
Selecting previously unselected package libxcb1:amd64.
Preparing to unpack .../libxcb1_1.11.1-1ubuntu1_amd64.deb ...
Unpacking libxcb1:amd64 (1.11.1-1ubuntu1) ...
Selecting previously unselected package libx11-data.
Preparing to unpack .../libx11-data_2%3a1.6.3-1ubuntu2.2_all.deb ...
Unpacking libx11-data (2:1.6.3-1ubuntu2.2) ...
Selecting previously unselected package libx11-6:amd64.
Preparing to unpack .../libx11-6_2%3a1.6.3-1ubuntu2.2_amd64.deb ...
Unpacking libx11-6:amd64 (2:1.6.3-1ubuntu2.2) ...
Selecting previously unselected package libxext6:amd64.
Preparing to unpack .../libxext6_2%3a1.3.3-1_amd64.deb ...
Unpacking libxext6:amd64 (2:1.3.3-1) ...
Selecting previously unselected package libwrap0:amd64.
Preparing to unpack .../libwrap0_7.6.q-25_amd64.deb ...
Unpacking libwrap0:amd64 (7.6.q-25) ...
Selecting previously unselected package libmagic1:amd64.
Preparing to unpack .../libmagic1_1%3a5.25-2ubuntu1.4_amd64.deb ...
Unpacking libmagic1:amd64 (1:5.25-2ubuntu1.4) ...
Selecting previously unselected package file.
Preparing to unpack .../file_1%3a5.25-2ubuntu1.4_amd64.deb ...
Unpacking file (1:5.25-2ubuntu1.4) ...
Selecting previously unselected package libbsd0:amd64.
Preparing to unpack .../libbsd0_0.8.2-1ubuntu0.1_amd64.deb ...
Unpacking libbsd0:amd64 (0.8.2-1ubuntu0.1) ...
Selecting previously unselected package libidn11:amd64.
Preparing to unpack .../libidn11_1.32-3ubuntu1.2_amd64.deb ...
Unpacking libidn11:amd64 (1.32-3ubuntu1.2) ...
Selecting previously unselected package openssl.
Preparing to unpack .../openssl_1.0.2g-1ubuntu4.20_amd64.deb ...
Unpacking openssl (1.0.2g-1ubuntu4.20) ...
Selecting previously unselected package ca-certificates.
Preparing to unpack .../ca-certificates_20210119~16.04.1_all.deb ...
Unpacking ca-certificates (20210119~16.04.1) ...
Selecting previously unselected package krb5-locales.
Preparing to unpack .../krb5-locales_1.13.2+dfsg-5ubuntu2.2_all.deb ...
Unpacking krb5-locales (1.13.2+dfsg-5ubuntu2.2) ...
Selecting previously unselected package libedit2:amd64.
Preparing to unpack .../libedit2_3.1-20150325-1ubuntu2_amd64.deb ...
Unpacking libedit2:amd64 (3.1-20150325-1ubuntu2) ...
Selecting previously unselected package libkrb5support0:amd64.
Preparing to unpack .../libkrb5support0_1.13.2+dfsg-5ubuntu2.2_amd64.deb ...
Unpacking libkrb5support0:amd64 (1.13.2+dfsg-5ubuntu2.2) ...
Selecting previously unselected package libk5crypto3:amd64.
Preparing to unpack .../libk5crypto3_1.13.2+dfsg-5ubuntu2.2_amd64.deb ...
Unpacking libk5crypto3:amd64 (1.13.2+dfsg-5ubuntu2.2) ...
Selecting previously unselected package libkeyutils1:amd64.
Preparing to unpack .../libkeyutils1_1.5.9-8ubuntu1_amd64.deb ...
Unpacking libkeyutils1:amd64 (1.5.9-8ubuntu1) ...
Selecting previously unselected package libkrb5-3:amd64.
Preparing to unpack .../libkrb5-3_1.13.2+dfsg-5ubuntu2.2_amd64.deb ...
Unpacking libkrb5-3:amd64 (1.13.2+dfsg-5ubuntu2.2) ...
Selecting previously unselected package libgssapi-krb5-2:amd64.
Preparing to unpack .../libgssapi-krb5-2_1.13.2+dfsg-5ubuntu2.2_amd64.deb ...
Unpacking libgssapi-krb5-2:amd64 (1.13.2+dfsg-5ubuntu2.2) ...
Selecting previously unselected package libxmuu1:amd64.
Preparing to unpack .../libxmuu1_2%3a1.1.2-2_amd64.deb ...
Unpacking libxmuu1:amd64 (2:1.1.2-2) ...
Selecting previously unselected package openssh-client.
Preparing to unpack .../openssh-client_1%3a7.2p2-4ubuntu2.10_amd64.deb ...
Unpacking openssh-client (1:7.2p2-4ubuntu2.10) ...
Selecting previously unselected package wget.
Preparing to unpack .../wget_1.17.1-1ubuntu1.5_amd64.deb ...
Unpacking wget (1.17.1-1ubuntu1.5) ...
Selecting previously unselected package xauth.
Preparing to unpack .../xauth_1%3a1.0.9-1ubuntu2_amd64.deb ...
Unpacking xauth (1:1.0.9-1ubuntu2) ...
Selecting previously unselected package ncurses-term.
Preparing to unpack .../ncurses-term_6.0+20160213-1ubuntu1_all.deb ...
Unpacking ncurses-term (6.0+20160213-1ubuntu1) ...
Selecting previously unselected package openssh-sftp-server.
Preparing to unpack .../openssh-sftp-server_1%3a7.2p2-4ubuntu2.10_amd64.deb ...
Unpacking openssh-sftp-server (1:7.2p2-4ubuntu2.10) ...
Selecting previously unselected package openssh-server.
Preparing to unpack .../openssh-server_1%3a7.2p2-4ubuntu2.10_amd64.deb ...
Unpacking openssh-server (1:7.2p2-4ubuntu2.10) ...
Selecting previously unselected package python3-pkg-resources.
Preparing to unpack .../python3-pkg-resources_20.7.0-1_all.deb ...
Unpacking python3-pkg-resources (20.7.0-1) ...
Selecting previously unselected package python3-chardet.
Preparing to unpack .../python3-chardet_2.3.0-2_all.deb ...
Unpacking python3-chardet (2.3.0-2) ...
Selecting previously unselected package python3-six.
Preparing to unpack .../python3-six_1.10.0-3_all.deb ...
Unpacking python3-six (1.10.0-3) ...
Selecting previously unselected package python3-urllib3.
Preparing to unpack .../python3-urllib3_1.13.1-2ubuntu0.16.04.4_all.deb ...
Unpacking python3-urllib3 (1.13.1-2ubuntu0.16.04.4) ...
Selecting previously unselected package python3-requests.
Preparing to unpack .../python3-requests_2.9.1-3ubuntu0.1_all.deb ...
Unpacking python3-requests (2.9.1-3ubuntu0.1) ...
Selecting previously unselected package tcpd.
Preparing to unpack .../tcpd_7.6.q-25_amd64.deb ...
Unpacking tcpd (7.6.q-25) ...
Selecting previously unselected package ssh-import-id.
Preparing to unpack .../ssh-import-id_5.5-0ubuntu1_all.deb ...
Unpacking ssh-import-id (5.5-0ubuntu1) ...
Processing triggers for libc-bin (2.23-0ubuntu11.3) ...
Processing triggers for systemd (229-4ubuntu21.31) ...
Setting up mime-support (3.59ubuntu1) ...
Setting up libmpdec2:amd64 (2.4.2-1) ...
Setting up libsqlite3-0:amd64 (3.11.0-1ubuntu1.5) ...
Setting up libpython3.5-stdlib:amd64 (3.5.2-2ubuntu0~16.04.13) ...
Setting up python3.5 (3.5.2-2ubuntu0~16.04.13) ...
Setting up libpython3-stdlib:amd64 (3.5.1-3) ...
Setting up libxau6:amd64 (1:1.0.8-1) ...
Setting up libxdmcp6:amd64 (1:1.1.2-1.1) ...
Setting up libxcb1:amd64 (1.11.1-1ubuntu1) ...
Setting up libx11-data (2:1.6.3-1ubuntu2.2) ...
Setting up libx11-6:amd64 (2:1.6.3-1ubuntu2.2) ...
Setting up libxext6:amd64 (2:1.3.3-1) ...
Setting up libwrap0:amd64 (7.6.q-25) ...
Setting up libmagic1:amd64 (1:5.25-2ubuntu1.4) ...
Setting up file (1:5.25-2ubuntu1.4) ...
Setting up libbsd0:amd64 (0.8.2-1ubuntu0.1) ...
Setting up libidn11:amd64 (1.32-3ubuntu1.2) ...
Setting up openssl (1.0.2g-1ubuntu4.20) ...
Setting up ca-certificates (20210119~16.04.1) ...
debconf: unable to initialize frontend: Dialog
debconf: (TERM is not set, so the dialog frontend is not usable.)
debconf: falling back to frontend: Readline
debconf: unable to initialize frontend: Readline
debconf: (Can't locate Term/ReadLine.pm in @INC (you may need to install the Term::ReadLine module) (@INC contains: /etc/perl /usr/local/lib/x86_64-linux-gnu/perl/5.22.1 /usr/local/share/perl/5.22.1 /usr/lib/x86_64-linux-gnu/perl5/5.22 /usr/share/perl5 /usr/lib/x86_64-linux-gnu/perl/5.22 /usr/share/perl/5.22 /usr/local/lib/site_perl /usr/lib/x86_64-linux-gnu/perl-base .) at /usr/share/perl5/Debconf/FrontEnd/Readline.pm line 7.)
debconf: falling back to frontend: Teletype
Setting up krb5-locales (1.13.2+dfsg-5ubuntu2.2) ...
Setting up libedit2:amd64 (3.1-20150325-1ubuntu2) ...
Setting up libkrb5support0:amd64 (1.13.2+dfsg-5ubuntu2.2) ...
Setting up libk5crypto3:amd64 (1.13.2+dfsg-5ubuntu2.2) ...
Setting up libkeyutils1:amd64 (1.5.9-8ubuntu1) ...
Setting up libkrb5-3:amd64 (1.13.2+dfsg-5ubuntu2.2) ...
Setting up libgssapi-krb5-2:amd64 (1.13.2+dfsg-5ubuntu2.2) ...
Setting up libxmuu1:amd64 (2:1.1.2-2) ...
Setting up openssh-client (1:7.2p2-4ubuntu2.10) ...
Setting up wget (1.17.1-1ubuntu1.5) ...
Setting up xauth (1:1.0.9-1ubuntu2) ...
Setting up ncurses-term (6.0+20160213-1ubuntu1) ...
Setting up openssh-sftp-server (1:7.2p2-4ubuntu2.10) ...
Setting up openssh-server (1:7.2p2-4ubuntu2.10) ...
debconf: unable to initialize frontend: Dialog
debconf: (TERM is not set, so the dialog frontend is not usable.)
debconf: falling back to frontend: Readline
debconf: unable to initialize frontend: Readline
debconf: (Can't locate Term/ReadLine.pm in @INC (you may need to install the Term::ReadLine module) (@INC contains: /etc/perl /usr/local/lib/x86_64-linux-gnu/perl/5.22.1 /usr/local/share/perl/5.22.1 /usr/lib/x86_64-linux-gnu/perl5/5.22 /usr/share/perl5 /usr/lib/x86_64-linux-gnu/perl/5.22 /usr/share/perl/5.22 /usr/local/lib/site_perl /usr/lib/x86_64-linux-gnu/perl-base .) at /usr/share/perl5/Debconf/FrontEnd/Readline.pm line 7.)
debconf: falling back to frontend: Teletype
Creating SSH2 RSA key; this may take some time ...
2048 SHA256:xJ2xUyVwTo/15ZT1pf3ZE44oAE34GgGr7nY1h0zGrl4 root@3aa0dcf1b6c7 (RSA)
Creating SSH2 DSA key; this may take some time ...
1024 SHA256:muuhol7NSs9wg1ltQMk1hWMRZkNLhng4umqiC+Z8hb0 root@3aa0dcf1b6c7 (DSA)
Creating SSH2 ECDSA key; this may take some time ...
256 SHA256:JDYqrG5nGCGqlsGnX6Np/KqJCoHStPr3Wf9keqpOtGM root@3aa0dcf1b6c7 (ECDSA)
Creating SSH2 ED25519 key; this may take some time ...
256 SHA256:DXwcb1Kzp0fb4+rRQCeADsLLAVu7eaB5AHmUE42eXZo root@3aa0dcf1b6c7 (ED25519)
invoke-rc.d: could not determine current runlevel
invoke-rc.d: policy-rc.d denied execution of start.
Setting up tcpd (7.6.q-25) ...
Setting up dh-python (2.20151103ubuntu1.2) ...
Setting up python3 (3.5.1-3) ...
running python rtupdate hooks for python3.5...
running python post-rtupdate hooks for python3.5...
Setting up python3-pkg-resources (20.7.0-1) ...
Setting up python3-chardet (2.3.0-2) ...
Setting up python3-six (1.10.0-3) ...
Setting up python3-urllib3 (1.13.1-2ubuntu0.16.04.4) ...
Setting up python3-requests (2.9.1-3ubuntu0.1) ...
Setting up ssh-import-id (5.5-0ubuntu1) ...
Processing triggers for libc-bin (2.23-0ubuntu11.3) ...
Processing triggers for ca-certificates (20210119~16.04.1) ...
Updating certificates in /etc/ssl/certs...
129 added, 0 removed; done.
Running hooks in /etc/ca-certificates/update.d...
done.
Processing triggers for systemd (229-4ubuntu21.31) ...
Removing intermediate container 3aa0dcf1b6c7
 ---> 71a63338dcf9
Step 4/12 : RUN mkdir -p /var/run/sshd
 ---> Running in e3292e62bff2
Removing intermediate container e3292e62bff2
 ---> 91cef0f675c0
Step 5/12 : RUN echo 'root:root' | chpasswd
 ---> Running in 80c276160d0e
Removing intermediate container 80c276160d0e
 ---> 74fa023c9ab7
Step 6/12 : RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
 ---> Running in 40d29023abd6
Removing intermediate container 40d29023abd6
 ---> ce6dc90594a1
Step 7/12 : RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd
 ---> Running in 2016e44b8316
Removing intermediate container 2016e44b8316
 ---> 687673093ee2
Step 8/12 : RUN mkdir -p /root/.ssh
 ---> Running in 3037935c124b
Removing intermediate container 3037935c124b
 ---> 3931ccba6595
Step 9/12 : COPY authorized_keys /root/.ssh/authorized_keys
 ---> b5dd5cc68e75
Step 10/12 : EXPOSE 22
 ---> Running in 7632a88610d4
Removing intermediate container 7632a88610d4
 ---> 0a7064f5298f
Step 11/12 : EXPOSE 80
 ---> Running in 54d64525f512
Removing intermediate container 54d64525f512
 ---> c65f78383445
Step 12/12 : CMD ["/usr/sbin/sshd", "-D"]
 ---> Running in e3a1c303f248
Removing intermediate container e3a1c303f248
 ---> 1463db4fb11c
Successfully built 1463db4fb11c
Successfully tagged tektutor/ansible-ubuntu-node:latest

jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/ubuntu$ <b>docker images</b>
REPOSITORY                                   TAG            IMAGE ID       CREATED          SIZE
<b>tektutor/ansible-ubuntu-node                 latest         1463db4fb11c   16 seconds ago   220MB</b>
mysql                                        latest         c71276df4a87   2 days ago       565MB
tektutor/java                                1.0            3dec350d1b8d   5 days ago       416MB
tektutor/hello                               1.0            f0652e271e67   5 days ago       416MB
localhost:5000/tektutor-ubuntu               22.04          8af846fe34ca   8 days ago       729MB
bitnami/nginx                                latest         7a094f97a968   8 days ago       92.2MB
nginx                                        latest         f9c14fe76d50   13 days ago      143MB
wordpress                                    latest         5174bdcbb532   2 weeks ago      616MB
registry                                     2              65f3b3441f04   3 weeks ago      24MB
ubuntu                                       22.04          3b418d7b466a   6 weeks ago      77.8MB
registry.access.redhat.com/ubi8/openjdk-11   latest         d1ce871371c2   6 weeks ago      394MB
mysql                                        5.7            dd6675b5cfea   7 weeks ago      569MB
ubuntu                                       16.04          b6f507652425   21 months ago    135MB
maven                                        3.6.3-jdk-11   e23b595c92ad   2 years ago      658MB
docker.bintray.io/jfrog/artifactory-oss      6.23.13        6106bdbbf79d   2 years ago      743MB
k8s.gcr.io/pause                             3.1            da86e6ba6ca1   5 years ago      742kB
</pre>

## Lab - Creating a Custom CentOS ansible node image
```
cd ~/devops-june-2023
git pull

cd Day3/CustomDockerImagesForAnsibleNodes/centos
cp ~/.ssh/id_rsa.pub authorized_keys

docker build -t tektutor/ansible-centos-node:latest .
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/centos$ <b>docker build -t tektutor/ansible-centos-node:latest .</b>
Sending build context to Docker daemon  4.096kB
Step 1/13 : FROM centos:centos7
centos7: Pulling from library/centos
2d473b07cdd5: Pull complete 
Digest: sha256:be65f488b7764ad3638f236b7b515b3678369a5124c47b8d32916d6487418ea4
Status: Downloaded newer image for centos:centos7
 ---> eeb6ee3f44bd
Step 2/13 : MAINTAINER Jeganathan Swaminathan <jegan@tektutor.org>
 ---> Running in 9fd08e6da238
Removing intermediate container 9fd08e6da238
 ---> 3f5baca12e71
Step 3/13 : RUN yum install -y which openssh-clients openssh-server python3
 ---> Running in a0601917ae88
Loaded plugins: fastestmirror, ovl
Determining fastest mirrors
 * base: mirrors.nxtgen.com
 * extras: mirrors.nxtgen.com
 * updates: mirrors.nxtgen.com
http://mirrors.nxtgen.com/centos-mirror/7.9.2009/os/x86_64/repodata/repomd.xml: [Errno 12] Timeout on http://mirrors.nxtgen.com/centos-mirror/7.9.2009/os/x86_64/repodata/repomd.xml: (28, 'Connection timed out after 30001 milliseconds')
Trying other mirror.
Resolving Dependencies
--> Running transaction check
---> Package openssh-clients.x86_64 0:7.4p1-22.el7_9 will be installed
--> Processing Dependency: openssh = 7.4p1-22.el7_9 for package: openssh-clients-7.4p1-22.el7_9.x86_64
--> Processing Dependency: fipscheck-lib(x86-64) >= 1.3.0 for package: openssh-clients-7.4p1-22.el7_9.x86_64
--> Processing Dependency: libfipscheck.so.1()(64bit) for package: openssh-clients-7.4p1-22.el7_9.x86_64
--> Processing Dependency: libedit.so.0()(64bit) for package: openssh-clients-7.4p1-22.el7_9.x86_64
---> Package openssh-server.x86_64 0:7.4p1-22.el7_9 will be installed
--> Processing Dependency: libwrap.so.0()(64bit) for package: openssh-server-7.4p1-22.el7_9.x86_64
---> Package python3.x86_64 0:3.6.8-18.el7 will be installed
--> Processing Dependency: python3-libs(x86-64) = 3.6.8-18.el7 for package: python3-3.6.8-18.el7.x86_64
--> Processing Dependency: python3-setuptools for package: python3-3.6.8-18.el7.x86_64
--> Processing Dependency: python3-pip for package: python3-3.6.8-18.el7.x86_64
--> Processing Dependency: libpython3.6m.so.1.0()(64bit) for package: python3-3.6.8-18.el7.x86_64
---> Package which.x86_64 0:2.20-7.el7 will be installed
--> Running transaction check
---> Package fipscheck-lib.x86_64 0:1.4.1-6.el7 will be installed
--> Processing Dependency: /usr/bin/fipscheck for package: fipscheck-lib-1.4.1-6.el7.x86_64
---> Package libedit.x86_64 0:3.0-12.20121213cvs.el7 will be installed
---> Package openssh.x86_64 0:7.4p1-22.el7_9 will be installed
---> Package python3-libs.x86_64 0:3.6.8-18.el7 will be installed
--> Processing Dependency: libtirpc.so.1()(64bit) for package: python3-libs-3.6.8-18.el7.x86_64
---> Package python3-pip.noarch 0:9.0.3-8.el7 will be installed
---> Package python3-setuptools.noarch 0:39.2.0-10.el7 will be installed
---> Package tcp_wrappers-libs.x86_64 0:7.6-77.el7 will be installed
--> Running transaction check
---> Package fipscheck.x86_64 0:1.4.1-6.el7 will be installed
---> Package libtirpc.x86_64 0:0.2.4-0.16.el7 will be installed
--> Finished Dependency Resolution

Dependencies Resolved

================================================================================
 Package                Arch       Version                    Repository   Size
================================================================================
Installing:
 openssh-clients        x86_64     7.4p1-22.el7_9             updates     655 k
 openssh-server         x86_64     7.4p1-22.el7_9             updates     459 k
 python3                x86_64     3.6.8-18.el7               updates      70 k
 which                  x86_64     2.20-7.el7                 base         41 k
Installing for dependencies:
 fipscheck              x86_64     1.4.1-6.el7                base         21 k
 fipscheck-lib          x86_64     1.4.1-6.el7                base         11 k
 libedit                x86_64     3.0-12.20121213cvs.el7     base         92 k
 libtirpc               x86_64     0.2.4-0.16.el7             base         89 k
 openssh                x86_64     7.4p1-22.el7_9             updates     510 k
 python3-libs           x86_64     3.6.8-18.el7               updates     6.9 M
 python3-pip            noarch     9.0.3-8.el7                base        1.6 M
 python3-setuptools     noarch     39.2.0-10.el7              base        629 k
 tcp_wrappers-libs      x86_64     7.6-77.el7                 base         66 k

Transaction Summary
================================================================================
Install  4 Packages (+9 Dependent packages)

Total download size: 11 M
Installed size: 53 M
Downloading packages:
warning: /var/cache/yum/x86_64/7/base/packages/fipscheck-lib-1.4.1-6.el7.x86_64.rpm: Header V3 RSA/SHA256 Signature, key ID f4a80eb5: NOKEY
Public key for fipscheck-lib-1.4.1-6.el7.x86_64.rpm is not installed
Public key for openssh-7.4p1-22.el7_9.x86_64.rpm is not installed
--------------------------------------------------------------------------------
Total                                              1.0 MB/s |  11 MB  00:11     
Retrieving key from file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
Importing GPG key 0xF4A80EB5:
 Userid     : "CentOS-7 Key (CentOS 7 Official Signing Key) <security@centos.org>"
 Fingerprint: 6341 ab27 53d7 8a78 a7c2 7bb1 24c6 a8a7 f4a8 0eb5
 Package    : centos-release-7-9.2009.0.el7.centos.x86_64 (@CentOS)
 From       : /etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  Installing : fipscheck-1.4.1-6.el7.x86_64                                1/13 
  Installing : fipscheck-lib-1.4.1-6.el7.x86_64                            2/13 
  Installing : openssh-7.4p1-22.el7_9.x86_64                               3/13 
  Installing : tcp_wrappers-libs-7.6-77.el7.x86_64                         4/13 
  Installing : libedit-3.0-12.20121213cvs.el7.x86_64                       5/13 
  Installing : libtirpc-0.2.4-0.16.el7.x86_64                              6/13 
  Installing : python3-setuptools-39.2.0-10.el7.noarch                     7/13 
  Installing : python3-pip-9.0.3-8.el7.noarch                              8/13 
  Installing : python3-3.6.8-18.el7.x86_64                                 9/13 
  Installing : python3-libs-3.6.8-18.el7.x86_64                           10/13 
  Installing : openssh-clients-7.4p1-22.el7_9.x86_64                      11/13 
  Installing : openssh-server-7.4p1-22.el7_9.x86_64                       12/13 
  Installing : which-2.20-7.el7.x86_64                                    13/13 
install-info: No such file or directory for /usr/share/info/which.info.gz
  Verifying  : fipscheck-lib-1.4.1-6.el7.x86_64                            1/13 
  Verifying  : libtirpc-0.2.4-0.16.el7.x86_64                              2/13 
  Verifying  : python3-setuptools-39.2.0-10.el7.noarch                     3/13 
  Verifying  : python3-libs-3.6.8-18.el7.x86_64                            4/13 
  Verifying  : openssh-server-7.4p1-22.el7_9.x86_64                        5/13 
  Verifying  : fipscheck-1.4.1-6.el7.x86_64                                6/13 
  Verifying  : which-2.20-7.el7.x86_64                                     7/13 
  Verifying  : libedit-3.0-12.20121213cvs.el7.x86_64                       8/13 
  Verifying  : openssh-clients-7.4p1-22.el7_9.x86_64                       9/13 
  Verifying  : tcp_wrappers-libs-7.6-77.el7.x86_64                        10/13 
  Verifying  : python3-3.6.8-18.el7.x86_64                                11/13 
  Verifying  : python3-pip-9.0.3-8.el7.noarch                             12/13 
  Verifying  : openssh-7.4p1-22.el7_9.x86_64                              13/13 

Installed:
  openssh-clients.x86_64 0:7.4p1-22.el7_9                                       
  openssh-server.x86_64 0:7.4p1-22.el7_9                                        
  python3.x86_64 0:3.6.8-18.el7                                                 
  which.x86_64 0:2.20-7.el7                                                     

Dependency Installed:
  fipscheck.x86_64 0:1.4.1-6.el7                                                
  fipscheck-lib.x86_64 0:1.4.1-6.el7                                            
  libedit.x86_64 0:3.0-12.20121213cvs.el7                                       
  libtirpc.x86_64 0:0.2.4-0.16.el7                                              
  openssh.x86_64 0:7.4p1-22.el7_9                                               
  python3-libs.x86_64 0:3.6.8-18.el7                                            
  python3-pip.noarch 0:9.0.3-8.el7                                              
  python3-setuptools.noarch 0:39.2.0-10.el7                                     
  tcp_wrappers-libs.x86_64 0:7.6-77.el7                                         

Complete!
Removing intermediate container a0601917ae88
 ---> c595ca923583
Step 4/13 : RUN ssh-keygen -f /etc/ssh/ssh_host_rsa_key
 ---> Running in 68a53f9a531f
Enter passphrase (empty for no passphrase): Enter same passphrase again: Generating public/private rsa key pair.
Your identification has been saved in /etc/ssh/ssh_host_rsa_key.
Your public key has been saved in /etc/ssh/ssh_host_rsa_key.pub.
The key fingerprint is:
SHA256:7BqFMLFlcBDPT1s12D+6s8FiLpo1+IYE0MqVv+XNcYQ root@68a53f9a531f
The key's randomart image is:
+---[RSA 2048]----+
|   .==+    +o    |
|  . +O    E.o.   |
| . ++.o . .. .   |
|  o .o.=.o. . o  |
|     ..+So o . . |
|      o+. o..    |
|     .o.+ o o.   |
|      .*o+ .o.   |
|      +o... .o   |
+----[SHA256]-----+
Removing intermediate container 68a53f9a531f
 ---> a34cdf78f144
Step 5/13 : RUN ssh-keygen -t dsa -f /etc/ssh/ssh_host_dsa_key
 ---> Running in 12efcfb2c2fa
Enter passphrase (empty for no passphrase): Enter same passphrase again: Generating public/private dsa key pair.
Your identification has been saved in /etc/ssh/ssh_host_dsa_key.
Your public key has been saved in /etc/ssh/ssh_host_dsa_key.pub.
The key fingerprint is:
SHA256:Knw7SzIpHAnfjbphcy/sFLzBhre3CPkHegXtqmaK4KU root@12efcfb2c2fa
The key's randomart image is:
+---[DSA 1024]----+
|                 |
|                 |
|.    .           |
| o *.o.          |
|  = Xo. S        |
| . B.*o.         |
|. X+@+=          |
|+.*X=Xoo         |
|oEo++o=o         |
+----[SHA256]-----+
Removing intermediate container 12efcfb2c2fa
 ---> b2fc6eeeb262
Step 6/13 : RUN sed -i '/pam_loginuid.so/c session    optional     pam_loginuid.so'  /etc/pam.d/sshd
 ---> Running in 3b871b63d2c5
Removing intermediate container 3b871b63d2c5
 ---> c8d25e9cedfc
Step 7/13 : RUN echo 'root:root' | chpasswd
 ---> Running in b67203f26d43
Removing intermediate container b67203f26d43
 ---> 68c1e8572f70
Step 8/13 : RUN usermod -aG wheel root
 ---> Running in d53de2f390d6
Removing intermediate container d53de2f390d6
 ---> 94d27858c6ff
Step 9/13 : RUN mkdir -p /root/.ssh
 ---> Running in 5ed259bd8eeb
Removing intermediate container 5ed259bd8eeb
 ---> 61601dca99ca
Step 10/13 : COPY authorized_keys /root/.ssh/authorized_keys
 ---> 5326572e7c8f
Step 11/13 : EXPOSE 22
 ---> Running in 21a9ba8bb1a3
Removing intermediate container 21a9ba8bb1a3
 ---> 14ddf2f60d7f
Step 12/13 : EXPOSE 80
 ---> Running in 7675d94b9e2d
Removing intermediate container 7675d94b9e2d
 ---> 497105840c0e
Step 13/13 : ENTRYPOINT ["/usr/sbin/sshd", "-D"]
 ---> Running in 9de48b4f87b1
Removing intermediate container 9de48b4f87b1
 ---> 4b5334077cdf
Successfully built 4b5334077cdf
Successfully tagged tektutor/ansible-centos-node:latest

jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/centos$ docker images
REPOSITORY                                   TAG            IMAGE ID       CREATED          SIZE
<b>tektutor/ansible-centos-node                 latest         4b5334077cdf   6 seconds ago    468MB
tektutor/ansible-ubuntu-node                 latest         1463db4fb11c   31 minutes ago   220MB</b>
mysql                                        latest         c71276df4a87   2 days ago       565MB
tektutor/java                                1.0            3dec350d1b8d   5 days ago       416MB
tektutor/hello                               1.0            f0652e271e67   5 days ago       416MB
localhost:5000/tektutor-ubuntu               22.04          8af846fe34ca   8 days ago       729MB
bitnami/nginx                                latest         7a094f97a968   8 days ago       92.2MB
nginx                                        latest         f9c14fe76d50   13 days ago      143MB
wordpress                                    latest         5174bdcbb532   2 weeks ago      616MB
registry                                     2              65f3b3441f04   3 weeks ago      24MB
ubuntu                                       22.04          3b418d7b466a   6 weeks ago      77.8MB
registry.access.redhat.com/ubi8/openjdk-11   latest         d1ce871371c2   6 weeks ago      394MB
mysql                                        5.7            dd6675b5cfea   7 weeks ago      569MB
centos                                       centos7        eeb6ee3f44bd   20 months ago    204MB
ubuntu                                       16.04          b6f507652425   21 months ago    135MB
maven                                        3.6.3-jdk-11   e23b595c92ad   2 years ago      658MB
docker.bintray.io/jfrog/artifactory-oss      6.23.13        6106bdbbf79d   2 years ago      743MB
k8s.gcr.io/pause                             3.1            da86e6ba6ca1   5 years ago      742kB
</pre>

## Lab - Testing our custom image and see if we are able to login to the containers using key-pair login authentication
```
docker run -d --name ubuntu1 --hostname ubuntu1 -p 2001:22 -p 8001:80 tektutor/ansible-ubuntu-node:latest
docker run -d --name ubuntu2 --hostname ubuntu2 -p 2002:22 -p 8002:80 tektutor/ansible-ubuntu-node:latest
docker run -d --name centos1 --hostname centos1 -p 2003:22 -p 8003:80 tektutor/ansible-centos-node:latest
docker run -d --name centos2 --hostname centos2 -p 2004:22 -p 8004:80 tektutor/ansible-centos-node:latest
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023$ docker run -d --name ubuntu1 --hostname ubuntu1 -p 2001:22 -p 8001:80 tektutor/ansible-ubuntu-node:latest 
2d04a938eea24341b667370efd13c3bfecf9be7d80a1e6f69353cd1eeb319d9f
jegan@tektutor:~/devops-june-2023$ docker run -d --name ubuntu2 --hostname ubuntu2 -p 2002:22 -p 8002:80 tektutor/ansible-ubuntu-node:latest 
1c8536e30e14ce66aa3a484387b92f2a637012916eaba439a05e3baae6827c38
jegan@tektutor:~/devops-june-2023$ docker run -d --name centos1 --hostname centos1 -p 2003:22 -p 8003:80 tektutor/ansible-centos-node:latest 
b22aeb7c307a3fd1a4cdcf4f1e765775adfc03d5565b8f2055f914212d8080b9
jegan@tektutor:~/devops-june-2023$ docker run -d --name centos2 --hostname centos2 -p 2004:22 -p 8004:80 tektutor/ansible-centos-node:latest 
c24244f1e6e2591274731e8ae4a0ba19e15d8c52201d8bd9d59409ba73e663a4

jegan@tektutor:~/devops-june-2023$ docker ps
CONTAINER ID   IMAGE                                 COMMAND               CREATED          STATUS          PORTS                                                                          NAMES
c24244f1e6e2   tektutor/ansible-centos-node:latest   "/usr/sbin/sshd -D"   3 seconds ago    Up 2 seconds    0.0.0.0:2004->22/tcp, :::2004->22/tcp, 0.0.0.0:8004->80/tcp, :::8004->80/tcp   centos2
b22aeb7c307a   tektutor/ansible-centos-node:latest   "/usr/sbin/sshd -D"   13 seconds ago   Up 12 seconds   0.0.0.0:2003->22/tcp, :::2003->22/tcp, 0.0.0.0:8003->80/tcp, :::8003->80/tcp   centos1
1c8536e30e14   tektutor/ansible-ubuntu-node:latest   "/usr/sbin/sshd -D"   43 seconds ago   Up 43 seconds   0.0.0.0:2002->22/tcp, :::2002->22/tcp, 0.0.0.0:8002->80/tcp, :::8002->80/tcp   ubuntu2
2d04a938eea2   tektutor/ansible-ubuntu-node:latest   "/usr/sbin/sshd -D"   55 seconds ago   Up 55 seconds   0.0.0.0:2001->22/tcp, :::2001->22/tcp, 0.0.0.0:8001->80/tcp, :::8001->80/tcp   ubuntu1
</pre>

You should be able to ssh into the containers without the need to type in the password
<pre>
jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/ubuntu$ <b>ssh -p 2001 root@localhost</b>
Welcome to Ubuntu 16.04.7 LTS (GNU/Linux 5.19.0-43-generic x86_64)

 * Documentation:  https://help.ubuntu.com
 * Management:     https://landscape.canonical.com
 * Support:        https://ubuntu.com/advantage
Last login: Wed Jun  7 07:39:59 2023 from 172.17.0.1
root@ubuntu1:~# exit
logout
Connection to localhost closed.

jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/ubuntu$ <b>ssh -p 2002 root@localhost</b>
Welcome to Ubuntu 16.04.7 LTS (GNU/Linux 5.19.0-43-generic x86_64)

 * Documentation:  https://help.ubuntu.com
 * Management:     https://landscape.canonical.com
 * Support:        https://ubuntu.com/advantage
Last login: Wed Jun  7 07:33:34 2023 from 172.17.0.1
root@ubuntu2:~# exit
logout
Connection to localhost closed.

jegan@tektutor:~/devops-june-2023/Day3/CustomDockerImagesForAnsibleNodes/ubuntu$ <b>ssh -p 2003 root@localhost</b>
Last login: Wed Jun  7 08:48:58 2023 from gateway
[root@centos1 ~]# exit
logout
Connection to localhost closed.

root@tektutor:~# <b>ssh -p 2004 root@localhost</b>
The authenticity of host '[localhost]:2004 ([127.0.0.1]:2004)' can't be established.
RSA key fingerprint is SHA256:7BqFMLFlcBDPT1s12D+6s8FiLpo1+IYE0MqVv+XNcYQ.
This key is not known by any other names
Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
Warning: Permanently added '[localhost]:2004' (RSA) to the list of known hosts.
root@localhost's password: 

root@tektutor:~# exit
logout
</pre>

## Lab - Executing Ansbile adhoc command - Ansible ping
```
cd ~/devops-june-2023
git pull

cd Day3/ansible
ansible -i inventory all -m ping
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day3/ansible$ <b>cat inventory</b>
[all]
ubuntu1 ansible_user=root ansible_host=localhost ansible_port=2001 ansible_private_key_file=~/.ssh/id_rsa
ubuntu2 ansible_user=root ansible_host=localhost ansible_port=2002 ansible_private_key_file=~/.ssh/id_rsa
centos1 ansible_user=root ansible_host=localhost ansible_port=2003 ansible_private_key_file=~/.ssh/id_rsa
centos2 ansible_user=root ansible_host=localhost ansible_port=2004 ansible_private_key_file=~/.ssh/id_rsa

jegan@tektutor:~/devops-june-2023/Day3/ansible$ <b>ansible -i inventory all -m ping</b>
ubuntu2 | SUCCESS => {
    "ansible_facts": {
        "discovered_interpreter_python": "/usr/bin/python3"
    },
    "changed": false,
    "ping": "pong"
}
ubuntu1 | SUCCESS => {
    "ansible_facts": {
        "discovered_interpreter_python": "/usr/bin/python3"
    },
    "changed": false,
    "ping": "pong"
}
centos2 | SUCCESS => {
    "ansible_facts": {
        "discovered_interpreter_python": "/usr/bin/python"
    },
    "changed": false,
    "ping": "pong"
}
centos1 | SUCCESS => {
    "ansible_facts": {
        "discovered_interpreter_python": "/usr/bin/python"
    },
    "changed": false,
    "ping": "pong"
}
</pre>

## Lab - Enabling the different levels of verbosity while running ansible ad-hoc commands
```
ansible -i inventory all -m ping -vvvv
```

## Lab - Using ansible setup module to collect facts about your ansible nodes
```
cd ~/devops-june-2023
git pull

cd Day3/ansible
ansible -i inventory ubuntu1 -m setup | grep ansible_os_family
ansible -i inventory ubuntu1 | grep ansible_distribution
ansible -i inventory centos1 -m setup | grep ansible_distribution
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day3/ansible$ ansible -i inventory ubuntu1 -m setup | grep ansible_os_family
        "ansible_os_family": "Debian",
jegan@tektutor:~/devops-june-2023/Day3/ansible$ ansible -i inventory ubuntu1 -m setup | grep ansible_distribution
        "ansible_distribution": "Ubuntu",
        "ansible_distribution_file_parsed": true,
        "ansible_distribution_file_path": "/etc/os-release",
        "ansible_distribution_file_variety": "Debian",
        "ansible_distribution_major_version": "16",
        "ansible_distribution_release": "xenial",
        "ansible_distribution_version": "16.04",
jegan@tektutor:~/devops-june-2023/Day3/ansible$ ansible -i inventory centos1 -m setup | grep ansible_distribution
        "ansible_distribution": "CentOS",
        "ansible_distribution_file_parsed": true,
        "ansible_distribution_file_path": "/etc/redhat-release",
        "ansible_distribution_file_variety": "RedHat",
        "ansible_distribution_major_version": "7",
        "ansible_distribution_release": "Core",
        "ansible_distribution_version": "7.9",
</pre>

## Lab - Using Ansible shell module to execute shell commands on the ansible nodes from your ACM
```
ansible -i inventory all -m shell -a "hostname"
ansible -i inventory all -m shell -a "hostname -i"
ansible -i inventory all -m shell -a "uptime"
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day3/ansible$ ansible -i inventory all -m shell -a "hostname -i"
ubuntu1 | CHANGED | rc=0 >>
172.17.0.2
ubuntu2 | CHANGED | rc=0 >>
172.17.0.3
centos1 | CHANGED | rc=0 >>
172.17.0.4
centos2 | CHANGED | rc=0 >>
172.17.0.5
</pre>

## Lab - Running your first Ansible Playbook
```
cd ~/devops-june-2023
git pull

cd Day3/ansible
ansible-playbook -i inventory ping-playbook.yml 
```

<pre>
jegan@tektutor:~/devops-june-2023/Day3/ansible$ <b>ansible-playbook -i inventory ping-playbook.yml</b>

PLAY [Ping Playbook] **********************************************************************************************************

TASK [Gathering Facts] ********************************************************************************************************
ok: [ubuntu2]
ok: [ubuntu1]
ok: [centos1]
ok: [centos2]

TASK [Ping ansible node container] ********************************************************************************************
ok: [ubuntu2]
ok: [ubuntu1]
ok: [centos1]
ok: [centos2]

PLAY RECAP ********************************************************************************************************************
centos1                    : ok=2    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
centos2                    : ok=2    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
ubuntu1                    : ok=2    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
ubuntu2                    : ok=2    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0 
</pre>

## Lab - Installing nginx using Ansible playbook
```
cd ~/devops-june-2023
git pull

cd Day3/ansible
ansible-playbook -i inventory install-nginx-playbook
```

## Lab - Developing Custom Ansible module and invoking it from an Ansible playbook
```
cd ~/devops-june-2023
git pull

cd Day3/ansible/custom-module
ansible-playbook -i inventory playbook.yml
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day3/ansible/custom-module$ ansible-playbook -i inventory playbook.yml 

PLAY [This playbook demonstrates invoking Ansible Custom Module] **************************************************************

TASK [Gathering Facts] ********************************************************************************************************
ok: [ubuntu2]
ok: [ubuntu1]
ok: [centos2]
ok: [centos1]

TASK [Invoke hello custom module] *********************************************************************************************
ok: [ubuntu1]
ok: [ubuntu2]
ok: [centos1]
ok: [centos2]

TASK [debug] ******************************************************************************************************************
ok: [ubuntu1] => {
    "output": {
        "changed": false,
        "failed": false,
        "output": "Hello Custom Module message - Hello Module !"
    }
}
ok: [ubuntu2] => {
    "output": {
        "changed": false,
        "failed": false,
        "output": "Hello Custom Module message - Hello Module !"
    }
}
ok: [centos1] => {
    "output": {
        "changed": false,
        "failed": false,
        "output": "Hello Custom Module message - Hello Module !"
    }
}
ok: [centos2] => {
    "output": {
        "changed": false,
        "failed": false,
        "output": "Hello Custom Module message - Hello Module !"
    }
}

PLAY RECAP ********************************************************************************************************************
centos1                    : ok=3    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
centos2                    : ok=3    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
ubuntu1                    : ok=3    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
ubuntu2                    : ok=3    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
</pre>


## Lab - Using extra variable to an Ansible playbook
```
cd ~/devops-june-2023
git pull

cd Day3/ansible
ansible-playbook -i inventory -e greeting_msg="Welcome" install-nginx-playbook.yml
```

