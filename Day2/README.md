# Day 2

## What is dual/multi booting?
- Using Boot Loader Utility, we should be able to support multiple OS in the Laptop/Desktop
- with Boot Loader Utility, only one OS can be active at a time
- Example:-
  - LILO
  - GRUB 

## What is Hypervisor?
- is Virtualization Technology
- helps us run multiple OS in the same Server/Workstation/Dekstop/Laptop etc.,
- the benefit is many OS can be active at the same time in the same machine
- for this to work, the Server/Workstation/Desktop/Laptop should have a Processor that supports Virtualization
- Intel Processor
  - Virtualization Feature is called VT-X
- AMD Processor
  - Virtualization Feature is called AMD-V
- there are two types of Hypervisors
  1. Type 1 ( Used in Servers/Workstations - Bare-metal without OS)
  2. Type 2 ( Used in Wokstations/Desktops/Laptors - with a Host OS )
- Examples of Hypervisor Softwares
- VMWare
  - Fusion ( Mac OS-X - Laptops/Desktops )
  - Workstation ( Windows, Linux  - Laptops/Desktop)
  - vSphere/vCenter ( Bare-metal Type 1 Hypervisor - Servers )
- Microsoft
  - Hyper-V ( Type 2  - Laptops/Desktops )
- Parallels ( Mac OS-X  - Laptops/Desktops )
- KVM ( Kernel Virtual Manager - Linux )
- each OS that is installed on top on the Hypervisor is called Guest OS or Virtual Machines
- Each Virtual machine(VM) gets its dedicated Hardware resources ( CPU Cores, RAM, Disk, Network Card[Virtual], Graphics Card[Virtual] )
- As every VM requires dedicated Hardware resources, they are called heavy-weight Virtualization Technology
- The number of VMs a Server/Destop/Workstation/Laptop can support is limited by number of CPU Cores the machine has, RAM, Disk, etc.,

## Hypervisor High Level Architecture

## Hypevisor vs Container

## Docker Overview

## Docker Alternatives

## What is a Docker Image?

## What is a Docker Container?

## Docker Registries

## Docker High Level Architecture


# Docker Commands

## Finding the docker version
```
docker --version
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day2$ <b>docker --version</b>
Docker version 20.10.21, build 20.10.21-0ubuntu1~22.04.3
</pre>

## Finding details about your docker installation
```
docker info
```

Expected output
<pre>
egan@tektutor:~/devops-june-2023/Day2$ docker info
Client:
 Context:    default
 Debug Mode: false

Server:
 Containers: 1
  Running: 0
  Paused: 0
  Stopped: 1
 Images: 14
 Server Version: 20.10.21
 Storage Driver: overlay2
  Backing Filesystem: extfs
  Supports d_type: true
  Native Overlay Diff: true
  userxattr: false
 Logging Driver: json-file
 Cgroup Driver: systemd
 Cgroup Version: 2
 Plugins:
  Volume: local
  Network: bridge host ipvlan macvlan null overlay
  Log: awslogs fluentd gcplogs gelf journald json-file local logentries splunk syslog
 Swarm: inactive
 Runtimes: io.containerd.runc.v2 io.containerd.runtime.v1.linux runc
 Default Runtime: runc
 Init Binary: docker-init
 containerd version: 
 runc version: 
 init version: 
 Security Options:
  apparmor
  seccomp
   Profile: default
  cgroupns
 Kernel Version: 5.19.0-43-generic
 Operating System: Ubuntu 22.04.2 LTS
 OSType: linux
 Architecture: x86_64
 CPUs: 48
 Total Memory: 125.5GiB
 Name: tektutor
 ID: WZA5:L6SA:D632:2LST:SLSS:TB3Y:AH7U:WLES:OL7T:G2TT:7TES:QLY2
 Docker Root Dir: /var/lib/docker
 Debug Mode: false
 Registry: https://index.docker.io/v1/
 Labels:
 Experimental: false
 Insecure Registries:
  127.0.0.0/8
 Live Restore Enabled: false
</pre>

## Listing the docker images in the local docker registry
```
docker images
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day2$ <b>docker images</b>
REPOSITORY                                   TAG            IMAGE ID       CREATED       SIZE
tektutor/java                                1.0            3dec350d1b8d   4 days ago    416MB
tektutor/hello                               1.0            f0652e271e67   4 days ago    416MB
localhost:5000/tektutor-ubuntu               22.04          8af846fe34ca   7 days ago    729MB
bitnami/nginx                                latest         7a094f97a968   7 days ago    92.2MB
registry                                     2              65f3b3441f04   3 weeks ago   24MB
ubuntu                                       22.04          3b418d7b466a   5 weeks ago   77.8MB
registry.access.redhat.com/ubi8/openjdk-11   latest         d1ce871371c2   6 weeks ago   394MB
maven                                        3.6.3-jdk-11   e23b595c92ad   2 years ago   658MB
docker.bintray.io/jfrog/artifactory-oss      6.23.13        6106bdbbf79d   2 years ago   743MB
k8s.gcr.io/pause                             3.1            da86e6ba6ca1   5 years ago   742kB
</pre>

## Downloading docker image from Docker Hub Remote Registry to Local Docker Registry
```
docker pull hello-world:latest
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day2$ <b>docker pull hello-world:latest</b>
latest: Pulling from library/hello-world
719385e32844: Pull complete 
Digest: sha256:fc6cf906cbfa013e80938cdf0bb199fbdbb86d6e3e013783e5a766f50f5dbce0
Status: Downloaded newer image for hello-world:latest
docker.io/library/hello-world:latest

jegan@tektutor:~/devops-june-2023/Day2$ <b>docker images</b>
REPOSITORY                                   TAG            IMAGE ID       CREATED       SIZE
tektutor/java                                1.0            3dec350d1b8d   4 days ago    416MB
tektutor/hello                               1.0            f0652e271e67   4 days ago    416MB
localhost:5000/tektutor-ubuntu               22.04          8af846fe34ca   7 days ago    729MB
bitnami/nginx                                latest         7a094f97a968   7 days ago    92.2MB
registry                                     2              65f3b3441f04   3 weeks ago   24MB
hello-world                                  latest         9c7a54a9a43c   4 weeks ago   13.3kB
ubuntu                                       22.04          3b418d7b466a   5 weeks ago   77.8MB
registry.access.redhat.com/ubi8/openjdk-11   latest         d1ce871371c2   6 weeks ago   394MB
maven                                        3.6.3-jdk-11   e23b595c92ad   2 years ago   658MB
docker.bintray.io/jfrog/artifactory-oss      6.23.13        6106bdbbf79d   2 years ago   743MB
k8s.gcr.io/pause                             3.1            da86e6ba6ca1   5 years ago   742kB
</pre>

## Lab - Deleting a docker image from Docker local registry
```
docker rmi hello-world:latest
```
Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day2$ <b>docker rmi hello-world:latest</b>
Untagged: hello-world:latest
Untagged: hello-world@sha256:fc6cf906cbfa013e80938cdf0bb199fbdbb86d6e3e013783e5a766f50f5dbce0
Deleted: sha256:9c7a54a9a43cca047013b82af109fe963fde787f63f9e016fdc3384500c2823d
Deleted: sha256:01bb4fce3eb1b56b05adf99504dafd31907a5aadac736e36b27595c8b92f07f1

jegan@tektutor:~/devops-june-2023/Day2$ <b>docker images</b>
REPOSITORY                                   TAG            IMAGE ID       CREATED       SIZE
tektutor/java                                1.0            3dec350d1b8d   4 days ago    416MB
tektutor/hello                               1.0            f0652e271e67   4 days ago    416MB
localhost:5000/tektutor-ubuntu               22.04          8af846fe34ca   7 days ago    729MB
bitnami/nginx                                latest         7a094f97a968   7 days ago    92.2MB
registry                                     2              65f3b3441f04   3 weeks ago   24MB
ubuntu                                       22.04          3b418d7b466a   5 weeks ago   77.8MB
registry.access.redhat.com/ubi8/openjdk-11   latest         d1ce871371c2   6 weeks ago   394MB
maven                                        3.6.3-jdk-11   e23b595c92ad   2 years ago   658MB
docker.bintray.io/jfrog/artifactory-oss      6.23.13        6106bdbbf79d   2 years ago   743MB
k8s.gcr.io/pause                             3.1            da86e6ba6ca1   5 years ago   742kB
</pre>

## Lab - Create a container and run it in the background
```
docker run -dit --name ubuntu1 --hostname ubuntu1 ubuntu:22.04 /bin/bash
```

Once the start the container, you may list running containers and see if the ubuntu1 container is running
```
docker ps
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day2$ <b>docker run -dit --name ubuntu1 --hostname ubuntu1 ubuntu:22.04 /bin/bash</b>
bc3c746dfb14e25f41da5f7716e2147bbd72ce4fc8074fe55d2cfa65fbb354cf

jegan@tektutor:~/devops-june-2023/Day2$ <b>docker ps</b>
CONTAINER ID   IMAGE          COMMAND       CREATED         STATUS         PORTS     NAMES
bc3c746dfb14   ubuntu:22.04   "/bin/bash"   4 seconds ago   Up 3 seconds             ubuntu1
</pre>

## Lab - Getting inside a container that runs in background
```
docker ps
docker exec -it ubuntu1 /bin/bash
hostname
hostname -i
exit
```

Expected output
<pre>
egan@tektutor:~/devops-june-2023/Day2$ <b>docker ps</b>
CONTAINER ID   IMAGE          COMMAND       CREATED          STATUS          PORTS     NAMES
bc3c746dfb14   ubuntu:22.04   "/bin/bash"   28 minutes ago   Up 28 minutes             ubuntu1

jegan@tektutor:~/devops-june-2023/Day2$ <b>docker exec -it ubuntu1 /bin/bash</b>
root@ubuntu1:/# ls
bin   dev  home  lib32  libx32  mnt  proc  run   srv  tmp  var
boot  etc  lib   lib64  media   opt  root  sbin  sys  usr
root@ubuntu1:/# hostname
ubuntu1
root@ubuntu1:/# hostname -i
172.17.0.2
root@ubuntu1:/# exit
exit

jegan@tektutor:~/devops-june-2023/Day2$ <b>docker ps</b>
CONTAINER ID   IMAGE          COMMAND       CREATED          STATUS          PORTS     NAMES
bc3c746dfb14   ubuntu:22.04   "/bin/bash"   31 minutes ago   Up 31 minutes             ubuntu1
</pre>


## Lab - Creating a mysql container and run it in background
```
docker run -d --name mysql --hostname mysql -e MYSQL_ROOT_PASSWORD mysql:latest
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day2$ <b>docker run -d --name mysql --hostname mysql -e MYSQL_ROOT_PASSWORD mysql:latest</b>
Unable to find image 'mysql:latest' locally
latest: Pulling from library/mysql
3e0c3751e648: Pull complete 
7914193c6f0e: Pull complete 
fe4b3f820487: Pull complete 
63683b304e3d: Pull complete 
6ad9069836bd: Pull complete 
de90cd4c0e5d: Pull complete 
892e565e2cf0: Pull complete 
73057d123da0: Pull complete 
af1a3c0ec34e: Pull complete 
62fe8dc4ffe9: Pull complete 
8807488ae889: Pull complete 
Digest: sha256:4bae98614cd6ad1aecbdd32ff1b37b93fb0ee22b069469e7bc9679bacef1abd2
Status: Downloaded newer image for mysql:latest
d1a50ac156ffa43db82a2dcb79a63cfd309bcb4bf571d20093e1bc13be3ac4b5

jegan@tektutor:~/devops-june-2023/Day2$ <b>docker ps</b>
CONTAINER ID   IMAGE          COMMAND                  CREATED         STATUS         PORTS                 NAMES
d1a50ac156ff   mysql:latest   "docker-entrypoint.s…"   4 seconds ago   Up 3 seconds   3306/tcp, 33060/tcp   mysql
</pre>
