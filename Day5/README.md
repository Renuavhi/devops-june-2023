# Day 5

## Pre-requites to run the CI/CD pipeline using Jenkinsfile
```
sudo yum install -y python3-pip
sudo pip3 install docker-py
```

Edit your /etc/sudoers file and add the below entry for rps user to give root permission
```
sudo vim /etc/sudoers
```

Make sure you give root permission to the rps user as shown below
<pre>
# This file MUST be edited with the 'visudo' command as root.
#
# Please consider adding local content in /etc/sudoers.d/ instead of
# directly modifying this file.
#
# See the man page for details on how to write a sudoers file.
#
Defaults	env_reset
Defaults	mail_badpass
Defaults	secure_path="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/snap/bin"
Defaults	use_pty

# This preserves proxy settings from user environments of root
# equivalent users (group sudo)
#Defaults:%sudo env_keep += "http_proxy https_proxy ftp_proxy all_proxy no_proxy"

# This allows running arbitrary commands, but so does ALL, and it means
# different sudoers have their choice of editor respected.
#Defaults:%sudo env_keep += "EDITOR"

# Completely harmless preservation of a user preference.
#Defaults:%sudo env_keep += "GREP_COLOR"

# While you shouldn't normally run git as root, you need to with etckeeper
#Defaults:%sudo env_keep += "GIT_AUTHOR_* GIT_COMMITTER_*"

# Per-user preferences; root won't have sensible values for them.
#Defaults:%sudo env_keep += "EMAIL DEBEMAIL DEBFULLNAME"

# "sudo scp" or "sudo rsync" should be able to use your SSH agent.
#Defaults:%sudo env_keep += "SSH_AGENT_PID SSH_AUTH_SOCK"

# Ditto for GPG agent
#Defaults:%sudo env_keep += "GPG_AGENT_INFO"

# Host alias specification

# User alias specification

# Cmnd alias specification

# User privilege specification
root	ALL=(ALL:ALL) ALL
<b>rps   ALL=(ALL) NOPASSWD:ALL</b>

# Members of the admin group may gain root privileges
%admin ALL=(ALL) ALL

# Allow members of group sudo to execute any command
%sudo	ALL=(ALL:ALL) ALL

# See sudoers(5) for more information on "@include" directives:

@includedir /etc/sudoers.d
</pre>

## Jenkins Docker Agent Template configuration
Navigate to Jenkins Dashboard --> Manage Jenkins --> Nodes and Cloud --> Clouds --> Docker Agent Templates
You need to ensure the label is docker-slave-jenkins also the Pull Strategy should be Never.

Once the above changes are done, please save.

## Lab - Setting up Continuous Integration for mysql db changes using Datical liquibase

Let's create a mysql db container
```
docker run -d --name mysql --hostname mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root@123 mysql:latest
```
Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day5/datical/db-ci$ docker run -d --name mysql --hostname mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root@123 mysql:latest
2d1d256a3e00104d7410d8f13ff09c8261307fdda6bf3961d18dc64a9e3565b6
</pre>


You may now check if the mysql db container is running
```
docker ps
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day5/datical/db-ci$ docker ps
CONTAINER ID   IMAGE          COMMAND                  CREATED         STATUS         PORTS                                                  NAMES
2d1d256a3e00   mysql:latest   "docker-entrypoint.s…"   2 minutes ago   Up 2 minutes   0.0.0.0:3306->3306/tcp, :::3306->3306/tcp, 33060/tcp   mysql
</pre>


Let us get inside the mysql db container, when prompts for password type 'root@123' without quotes
```
docker exec -it mysql bash
mysql -u root -p
CREATE DATABASE tektutor;
SHOW DATABASE;
```
Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day5/datical/db-ci$ docker exec -it mysql bash
bash-4.4# mysql -u root -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 8
Server version: 8.0.33 MySQL Community Server - GPL

Copyright (c) 2000, 2023, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> CREATE DATABASE tektutor;
Query OK, 1 row affected (0.01 sec)

mysql> SHOW DATABASES;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
| tektutor           |
+--------------------+
5 rows in set (0.00 sec)

mysql> exit
Bye
bash-4.4# exit
exit
</pre>

Let's check the datical liquibase now

```
cd ~/devops-june-2023
git pull

cd Day5/datical/db-ci
cat liquibase.properties
```

The liquibase.properties file has the mysql connection details and it looks as shown below
<pre>
jegan@tektutor:~/devops-june-2023/Day5/datical/db-ci$ <b>cat liquibase.properties</b>
changeLogFile: dbchangelog.xml
url: jdbc:mysql://localhost:3306/tektutor
username: root
password: root@123
</pre>

Any schema changes that we wish to perform, we need to do only via the dbchangelog.xml file. For instance, to create a trainig table with 3 colums, we may create a dbchangelog.xml as shown below
```
<?xml version="1.0" encoding="UTF-8"?>  
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
    xmlns:pro="http://www.liquibase.org/xml/ns/pro"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd
        http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd
        http://www.liquibase.org/xml/ns/pro http://www.liquibase.org/xml/ns/pro/liquibase-pro-latest.xsd">
        
     <changeSet  id="1"  author="Jeganathan Swaminathan">  
         <createTable  tableName="training">  
             <column  name="id"  type="int">  
                 <constraints  primaryKey="true"  nullable="false"/>  
             </column>  
             <column  name="name"  type="varchar(200)">  
                 <constraints  nullable="false"/>  
             </column>  
             <column  name="duration"  type="varchar(200)"/>  
         </createTable>  
    </changeSet>  
</databaseChangeLog>
```

In order to apply the table schema changes, you may run the below command
```
cd ~/devops-june-2023/Day5/datical/db-ci

mvn liquibase:update
```
Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day5/datical/db-ci$ mvn liquibase:update
[INFO] Scanning for projects...
[INFO] 
[INFO] -------------------< org.tektutor:tektutor-java-app >-------------------
[INFO] Building tektutor-java-app 1.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- liquibase-maven-plugin:4.22.0:update (default-cli) @ tektutor-java-app ---
[INFO] ------------------------------------------------------------------------
[INFO] there are no resolved artifacts for the Maven project.
[INFO] there are no resolved artifacts for the Maven project.
[INFO] Parsing Liquibase Properties File
[INFO]   File: liquibase.properties
[INFO] ------------------------------------------------------------------------
[INFO] ####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ## 
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ## 
##                                                ##
####################################################
Starting Liquibase at 15:53:32 (version 4.22.0 #9559 built at 2023-05-10 20:45+0000)
[INFO] Parsing Liquibase Properties File liquibase.properties for changeLog parameters
[INFO] Executing on Database: jdbc:mysql://localhost:3306/tektutor
[INFO] Successfully acquired change log lock
[INFO] Creating database history table with name: DATABASECHANGELOG
[INFO] Reading from DATABASECHANGELOG
[INFO] Using deploymentId: 6306214275
[INFO] Reading from DATABASECHANGELOG
Running Changeset: dbchangelog.xml::1::Jeganathan Swaminathan
[INFO] Table training created
[INFO] ChangeSet dbchangelog.xml::1::Jeganathan Swaminathan ran successfully in 25ms
[INFO] UPDATE SUMMARY
[INFO] Run:                          1
[INFO] Previously run:               0
[INFO] Filtered out:                 0
[INFO] -------------------------------
[INFO] Total change sets:            1


UPDATE SUMMARY
Run:                          1
Previously run:               0
Filtered out:                 0
-------------------------------
Total change sets:            1

[INFO] Update summary generated
[INFO] Update command completed successfully.
Liquibase: Update has been successful.
[INFO] Successfully released change log lock
[INFO] Successfully released change log lock
[INFO] Command execution complete
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.046 s
[INFO] Finished at: 2023-06-09T15:53:34+05:30
[INFO] ------------------------------------------------------------------------
</pre>

The liquibase will grab the mysql connection details from the liquibase.properties file and connects to our tektutor database with mysql server and applies the changeset defined in the dbchangelog.xml file.

The pom.xml file points to the liquibase.properites and the liquibase.properites file points to dbchangelog.xml file. This is how, liquibase learns about these files.

You may now verify, if the changes are done in your mysql server
<pre>
jegan@tektutor:~/devops-june-2023/Day5/datical/db-ci$ docker exec -it mysql bash
bash-4.4# mysql -u root -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 10
Server version: 8.0.33 MySQL Community Server - GPL

Copyright (c) 2000, 2023, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> USE tektutor;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> SHOW TABLES;
+-----------------------+
| Tables_in_tektutor    |
+-----------------------+
| DATABASECHANGELOG     |
| DATABASECHANGELOGLOCK |
| training              |
+-----------------------+
3 rows in set (0.01 sec)

mysql> DESCRIBE training;
+----------+--------------+------+-----+---------+-------+
| Field    | Type         | Null | Key | Default | Extra |
+----------+--------------+------+-----+---------+-------+
| id       | int          | NO   | PRI | NULL    |       |
| name     | varchar(200) | NO   |     | NULL    |       |
| duration | varchar(200) | YES  |     | NULL    |       |
+----------+--------------+------+-----+---------+-------+
3 rows in set (0.00 sec)

mysql> exit
Bye
bash-4.4# exit
exit
</pre>

Assuming, you wish to add 2 more columns to the training table on tektutor database. We can update the dbchangelog.xml file as shown below
```
<?xml version="1.0" encoding="UTF-8"?>  
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
    xmlns:pro="http://www.liquibase.org/xml/ns/pro"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd
        http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd
        http://www.liquibase.org/xml/ns/pro http://www.liquibase.org/xml/ns/pro/liquibase-pro-latest.xsd">

     <changeSet  id="1"  author="Jeganathan Swaminathan">  
         <createTable  tableName="training">  
             <column  name="id"  type="int">  
                 <constraints  primaryKey="true"  nullable="false"/>  
             </column>  
             <column  name="name"  type="varchar(200)">  
                 <constraints  nullable="false"/>  
             </column>  
             <column  name="duration"  type="varchar(200)"/>  
         </createTable>  
    </changeSet>  
    <changeSet  id="2"  author="Jeganathan Swaminathan">  
         <addColumn tableName="training">  
             <column  name="from_date"  type="varchar(200)"/>  
             <column  name="to_date"  type="varchar(200)"/>  
	 </addColumn>
    </changeSet>  
</databaseChangeLog>
```

You may now update the liquibase as shown below
```
cd ~/devops-june-2023/Day5/datical/db-ci
mvn liquibase:update
```

Expected output
<pre>
jegan@tektutor:~/devops-june-2023/Day5/datical/db-ci$ mvn liquibase:update
[INFO] Scanning for projects...
[INFO] 
[INFO] -------------------< org.tektutor:tektutor-java-app >-------------------
[INFO] Building tektutor-java-app 1.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- liquibase-maven-plugin:4.22.0:update (default-cli) @ tektutor-java-app ---
[INFO] ------------------------------------------------------------------------
[INFO] there are no resolved artifacts for the Maven project.
[INFO] there are no resolved artifacts for the Maven project.
[INFO] Parsing Liquibase Properties File
[INFO]   File: liquibase.properties
[INFO] ------------------------------------------------------------------------
[INFO] ####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ## 
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ## 
##                                                ##
####################################################
Starting Liquibase at 15:55:46 (version 4.22.0 #9559 built at 2023-05-10 20:45+0000)
[INFO] Parsing Liquibase Properties File liquibase.properties for changeLog parameters
[INFO] Executing on Database: jdbc:mysql://localhost:3306/tektutor
[INFO] Successfully acquired change log lock
[INFO] Reading from DATABASECHANGELOG
[INFO] Using deploymentId: 6306347311
[INFO] Reading from DATABASECHANGELOG
Running Changeset: dbchangelog.xml::2::Jeganathan Swaminathan
[INFO] Columns from_date(varchar(200)),to_date(varchar(200)) added to training
[INFO] ChangeSet dbchangelog.xml::2::Jeganathan Swaminathan ran successfully in 29ms
[INFO] UPDATE SUMMARY
[INFO] Run:                          1
[INFO] Previously run:               1
[INFO] Filtered out:                 0
[INFO] -------------------------------
[INFO] Total change sets:            2


UPDATE SUMMARY
Run:                          1
Previously run:               1
Filtered out:                 0
-------------------------------
Total change sets:            2

[INFO] Update summary generated
[INFO] Update command completed successfully.
Liquibase: Update has been successful.
[INFO] Successfully released change log lock
[INFO] Successfully released change log lock
[INFO] Command execution complete
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.912 s
[INFO] Finished at: 2023-06-09T15:55:47+05:30
[INFO] ------------------------------------------------------------------------
</pre>

You may now verify if the schema changes are applied
<pre>
jegan@tektutor:~/devops-june-2023/Day5/datical/db-ci$ docker exec -it mysql bash
bash-4.4# mysql -u root -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 12
Server version: 8.0.33 MySQL Community Server - GPL

Copyright (c) 2000, 2023, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> USE tektutor;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> SHOW TABLES;
+-----------------------+
| Tables_in_tektutor    |
+-----------------------+
| DATABASECHANGELOG     |
| DATABASECHANGELOGLOCK |
| training              |
+-----------------------+
3 rows in set (0.01 sec)

mysql> DESCRIBE training;
+-----------+--------------+------+-----+---------+-------+
| Field     | Type         | Null | Key | Default | Extra |
+-----------+--------------+------+-----+---------+-------+
| id        | int          | NO   | PRI | NULL    |       |
| name      | varchar(200) | NO   |     | NULL    |       |
| duration  | varchar(200) | YES  |     | NULL    |       |
| from_date | varchar(200) | YES  |     | NULL    |       |
| to_date   | varchar(200) | YES  |     | NULL    |       |
+-----------+--------------+------+-----+---------+-------+
5 rows in set (0.00 sec)

mysql> exit
Bye
bash-4.4# exit
exit
</pre>

For official documentation about liquibase, you may check here
https://docs.liquibase.com/faq.html

## Digial AI - XL Release 

You may download the XLR Server as a zip file from their official website.  
```
https://digital.ai/products/release/
```

You need to extract the xlr zip file as shown below
<pre>
jegan@tektutor:~/Downloads$ ls
xl-release-10.2.1-server.zip

jegan@tektutor:~/Downloads$ unzip xl-release-10.2.1-server.zip 
Archive:  xl-release-10.2.1-server.zip
   creating: xl-release-10.2.1-server/
   creating: xl-release-10.2.1-server/bin/
  inflating: xl-release-10.2.1-server/bin/.wrapper-env.cmd  
  inflating: xl-release-10.2.1-server/bin/.wrapper-env.sh  
  inflating: xl-release-10.2.1-server/bin/install-service.cmd  
  inflating: xl-release-10.2.1-server/bin/install-service.sh  
  inflating: xl-release-10.2.1-server/bin/run.cmd  
  inflating: xl-release-10.2.1-server/bin/run.sh  
  inflating: xl-release-10.2.1-server/bin/server.cmd  
  inflating: xl-release-10.2.1-server/bin/server.sh  
  inflating: xl-release-10.2.1-server/bin/uninstall-service.cmd  
  inflating: xl-release-10.2.1-server/bin/uninstall-service.sh  
   creating: xl-release-10.2.1-server/conf/
  inflating: xl-release-10.2.1-server/conf/logback-access.xml  
  inflating: xl-release-10.2.1-server/conf/logback.xml  
  inflating: xl-release-10.2.1-server/conf/logging.properties  
  inflating: xl-release-10.2.1-server/conf/script.policy  
  inflating: xl-release-10.2.1-server/conf/wrapper-daemon.vm  
  inflating: xl-release-10.2.1-server/conf/xl-release-security.xml  
  inflating: xl-release-10.2.1-server/conf/xl-release.conf  
  inflating: xl-release-10.2.1-server/conf/xl-release.policy  
  inflating: xl-release-10.2.1-server/conf/xlr-wrapper-linux.conf  
  inflating: xl-release-10.2.1-server/conf/xlr-wrapper-win.conf  
   creating: xl-release-10.2.1-server/doc/
  inflating: xl-release-10.2.1-server/doc/license-notice-server.txt  
   creating: xl-release-10.2.1-server/ext/
  inflating: xl-release-10.2.1-server/ext/readme.txt  
  inflating: xl-release-10.2.1-server/ext/synthetic.xml  
   creating: xl-release-10.2.1-server/hotfix/
   creating: xl-release-10.2.1-server/hotfix/lib/
  inflating: xl-release-10.2.1-server/hotfix/lib/readme.txt  
   creating: xl-release-10.2.1-server/hotfix/plugins/
  inflating: xl-release-10.2.1-server/hotfix/plugins/readme.txt  
  inflating: xl-release-10.2.1-server/hotfix/readme.txt  
   creating: xl-release-10.2.1-server/lib/
  inflating: xl-release-10.2.1-server/lib/HikariCP-4.0.3.jar  
  inflating: xl-release-10.2.1-server/lib/JavaEWAH-1.1.7.jar  
  inflating: xl-release-10.2.1-server/lib/SparseBitSet-1.2.jar  
  inflating: xl-release-10.2.1-server/lib/accessors-smart-1.2.jar  
  inflating: xl-release-10.2.1-server/lib/activemq-broker-5.16.2.jar  
  inflating: xl-release-10.2.1-server/lib/activemq-client-5.16.2.jar  
  inflating: xl-release-10.2.1-server/lib/activemq-openwire-legacy-5.16.2.jar  
  inflating: xl-release-10.2.1-server/lib/agrona-1.9.0.jar  
  inflating: xl-release-10.2.1-server/lib/akka-actor_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-cluster-sharding_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-cluster-tools_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-cluster_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-coordination_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-distributed-data_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-http-core_2.13-10.2.4.jar  
  inflating: xl-release-10.2.1-server/lib/akka-http-spray-json_2.13-10.2.4.jar  
  inflating: xl-release-10.2.1-server/lib/akka-http-xml_2.13-10.2.4.jar  
  inflating: xl-release-10.2.1-server/lib/akka-http_2.13-10.2.4.jar  
  inflating: xl-release-10.2.1-server/lib/akka-kryo-serialization_2.13-1.1.5.jar  
  inflating: xl-release-10.2.1-server/lib/akka-parsing_2.13-10.2.4.jar  
  inflating: xl-release-10.2.1-server/lib/akka-persistence_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-pki_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-protobuf-v3_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-quartz-scheduler_2.13-1.8.5-akka-2.6.x.jar  
  inflating: xl-release-10.2.1-server/lib/akka-remote_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-slf4j_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/akka-stream_2.13-2.6.13.jar  
  inflating: xl-release-10.2.1-server/lib/animal-sniffer-annotations-1.20.jar  
  inflating: xl-release-10.2.1-server/lib/annotations-3.0.1.jar  
  inflating: xl-release-10.2.1-server/lib/annotations-4.1.1.4.jar  
  inflating: xl-release-10.2.1-server/lib/apache-mime4j-core-0.8.3.jar  
  inflating: xl-release-10.2.1-server/lib/apache-mime4j-dom-0.8.3.jar  
  inflating: xl-release-10.2.1-server/lib/apache-mime4j-storage-0.8.3.jar  
  inflating: xl-release-10.2.1-server/lib/api-common-1.10.1.jar  
  inflating: xl-release-10.2.1-server/lib/appserver-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/appserver-api-impl-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/appserver-core-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/appserver-main-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/appserver-migrations-sql-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/appserver-security-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/args4j-2.33.jar  
  inflating: xl-release-10.2.1-server/lib/asm-9.0.jar  
  inflating: xl-release-10.2.1-server/lib/asm-analysis-9.0.jar  
  inflating: xl-release-10.2.1-server/lib/asm-commons-9.0.jar  
  inflating: xl-release-10.2.1-server/lib/asm-tree-9.0.jar  
  inflating: xl-release-10.2.1-server/lib/asm-util-5.0.3.jar  
  inflating: xl-release-10.2.1-server/lib/asn-one-0.5.0.jar  
  inflating: xl-release-10.2.1-server/lib/asyncutil-0.1.0.jar  
  inflating: xl-release-10.2.1-server/lib/atlassian-annotations-1.1.0.jar  
  inflating: xl-release-10.2.1-server/lib/auto-value-annotations-1.7.4.jar  
  inflating: xl-release-10.2.1-server/lib/autolink-0.10.0.jar  
  inflating: xl-release-10.2.1-server/lib/aws-java-sdk-core-1.11.883.jar  
  inflating: xl-release-10.2.1-server/lib/aws-java-sdk-marketplacemeteringservice-1.11.883.jar  
  inflating: xl-release-10.2.1-server/lib/bcpkix-jdk15on-1.68.jar  
  inflating: xl-release-10.2.1-server/lib/bcprov-jdk15on-1.68.jar  
  inflating: xl-release-10.2.1-server/lib/btf-1.3.jar  
  inflating: xl-release-10.2.1-server/lib/caffeine-3.0.1.jar  
  inflating: xl-release-10.2.1-server/lib/checker-compat-qual-2.5.5.jar  
  inflating: xl-release-10.2.1-server/lib/checker-qual-3.11.0.jar  
  inflating: xl-release-10.2.1-server/lib/chill-java-0.9.5.jar  
  inflating: xl-release-10.2.1-server/lib/chill_2.13-0.9.5.jar  
  inflating: xl-release-10.2.1-server/lib/commonmark-0.17.0.jar  
  inflating: xl-release-10.2.1-server/lib/commonmark-ext-autolink-0.17.0.jar  
  inflating: xl-release-10.2.1-server/lib/commonmark-ext-gfm-strikethrough-0.17.0.jar  
  inflating: xl-release-10.2.1-server/lib/commonmark-ext-gfm-tables-0.17.0.jar  
  inflating: xl-release-10.2.1-server/lib/commonmark-ext-ins-0.17.0.jar  
  inflating: xl-release-10.2.1-server/lib/commons-codec-1.15.jar  
  inflating: xl-release-10.2.1-server/lib/commons-collections4-4.4.jar  
  inflating: xl-release-10.2.1-server/lib/commons-compress-1.20.jar  
  inflating: xl-release-10.2.1-server/lib/commons-email-1.5.jar  
  inflating: xl-release-10.2.1-server/lib/commons-fileupload-1.4.jar  
  inflating: xl-release-10.2.1-server/lib/commons-io-2.8.0.jar  
  inflating: xl-release-10.2.1-server/lib/commons-lang-2.6.jar  
  inflating: xl-release-10.2.1-server/lib/commons-lang3-3.12.0.jar  
  inflating: xl-release-10.2.1-server/lib/commons-math3-3.6.1.jar  
  inflating: xl-release-10.2.1-server/lib/commons-net-3.3.jar  
  inflating: xl-release-10.2.1-server/lib/config-1.4.1.jar  
  inflating: xl-release-10.2.1-server/lib/conjur-api-2.1.0.jar  
  inflating: xl-release-10.2.1-server/lib/conscrypt-openjdk-uber-2.5.1.jar  
  inflating: xl-release-10.2.1-server/lib/content-type-2.1.jar  
  inflating: xl-release-10.2.1-server/lib/crowd-integration-api-3.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/crowd-integration-client-common-3.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/crowd-integration-client-rest-3.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/crowd-integration-springsecurity-3.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/crowd-integration-springsecurity-common-3.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/curvesapi-1.06.jar  
  inflating: xl-release-10.2.1-server/lib/derby-10.14.2.0.jar  
  inflating: xl-release-10.2.1-server/lib/dom4j-2.1.3.jar  
  inflating: xl-release-10.2.1-server/lib/eddsa-0.2.0.jar  
  inflating: xl-release-10.2.1-server/lib/embedded-crowd-api-3.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/engine-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/engine-spi-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/engine-xml-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/error_prone_annotations-2.5.1.jar  
  inflating: xl-release-10.2.1-server/lib/failureaccess-1.0.1.jar  
  inflating: xl-release-10.2.1-server/lib/findbugs-annotations-3.0.1.jar  
  inflating: xl-release-10.2.1-server/lib/fontbox-2.0.23.jar  
  inflating: xl-release-10.2.1-server/lib/gax-1.62.0.jar  
  inflating: xl-release-10.2.1-server/lib/gax-grpc-1.62.0.jar  
  inflating: xl-release-10.2.1-server/lib/geronimo-j2ee-management_1.1_spec-1.0.1.jar  
  inflating: xl-release-10.2.1-server/lib/geronimo-jms_1.1_spec-1.1.1.jar  
  inflating: xl-release-10.2.1-server/lib/github-api-1.127.jar  
  inflating: xl-release-10.2.1-server/lib/google-api-client-1.31.1.jar  
  inflating: xl-release-10.2.1-server/lib/google-api-services-compute-v1-rev20210310-1.31.0.jar  
  inflating: xl-release-10.2.1-server/lib/google-auth-library-credentials-0.24.1.jar  
  inflating: xl-release-10.2.1-server/lib/google-auth-library-oauth2-http-0.24.1.jar  
  inflating: xl-release-10.2.1-server/lib/google-cloud-core-1.94.3.jar  
  inflating: xl-release-10.2.1-server/lib/google-cloud-os-login-1.2.2.jar  
  inflating: xl-release-10.2.1-server/lib/google-http-client-1.39.0.jar  
  inflating: xl-release-10.2.1-server/lib/google-http-client-apache-v2-1.38.0.jar  
  inflating: xl-release-10.2.1-server/lib/google-http-client-gson-1.39.0.jar  
  inflating: xl-release-10.2.1-server/lib/google-http-client-jackson2-1.38.0.jar  
  inflating: xl-release-10.2.1-server/lib/google-oauth-client-1.31.2.jar  
  inflating: xl-release-10.2.1-server/lib/grizzled-slf4j_2.13-1.3.4.jar  
  inflating: xl-release-10.2.1-server/lib/groovy-all-2.4.21.jar  
  inflating: xl-release-10.2.1-server/lib/groovy-sandbox-1.19.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-alts-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-api-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-auth-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-context-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-core-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-grpclb-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-netty-shaded-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-protobuf-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-protobuf-lite-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/grpc-stub-1.36.0.jar  
  inflating: xl-release-10.2.1-server/lib/gson-2.8.6.jar  
  inflating: xl-release-10.2.1-server/lib/guava-30.1.1-jre.jar  
  inflating: xl-release-10.2.1-server/lib/h2-1.4.200.jar  
  inflating: xl-release-10.2.1-server/lib/hawtbuf-1.11.jar  
  inflating: xl-release-10.2.1-server/lib/hpack-1.0.2.jar  
  inflating: xl-release-10.2.1-server/lib/httpclient-4.5.13.jar  
  inflating: xl-release-10.2.1-server/lib/httpclient-cache-4.5.3.jar  
  inflating: xl-release-10.2.1-server/lib/httpcore-4.4.14.jar  
  inflating: xl-release-10.2.1-server/lib/httpmime-4.5.13.jar  
  inflating: xl-release-10.2.1-server/lib/ical4j-3.0.21.jar  
  inflating: xl-release-10.2.1-server/lib/ion-java-1.0.2.jar  
  inflating: xl-release-10.2.1-server/lib/istack-commons-runtime-3.0.12.jar  
  inflating: xl-release-10.2.1-server/lib/j2objc-annotations-1.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-annotations-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-core-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-coreutils-2.0.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-databind-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-dataformat-cbor-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-dataformat-yaml-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-datatype-jdk8-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-datatype-joda-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-datatype-jsr310-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-jaxrs-base-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-jaxrs-json-provider-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-module-jaxb-annotations-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-module-parameter-names-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jackson-module-scala_2.13-2.12.3.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.activation-1.2.2.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.activation-api-1.2.2.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.annotation-api-1.3.5.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.el-3.0.3.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.jms-api-2.0.3.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.json-2.0.1.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.json-api-2.0.0.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.mail-1.6.7.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.servlet-api-4.0.4.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.validation-api-2.0.2.jar  
  inflating: xl-release-10.2.1-server/lib/jakarta.websocket-api-1.1.2.jar  
  inflating: xl-release-10.2.1-server/lib/java-semver-0.9.0.jar  
  inflating: xl-release-10.2.1-server/lib/javassist-3.27.0-GA.jar  
  inflating: xl-release-10.2.1-server/lib/javax-websocket-client-impl-9.4.39.v20210325.jar  
  inflating: xl-release-10.2.1-server/lib/javax-websocket-server-impl-9.4.39.v20210325.jar  
  inflating: xl-release-10.2.1-server/lib/jaxb-api-2.3.1.jar  
  inflating: xl-release-10.2.1-server/lib/jaxb-runtime-2.3.4.jar  
  inflating: xl-release-10.2.1-server/lib/jaxen-1.2.0.jar  
  inflating: xl-release-10.2.1-server/lib/jboss-annotations-api_1.3_spec-2.0.1.Final.jar  
  inflating: xl-release-10.2.1-server/lib/jboss-jaxrs-api_2.1_spec-2.0.1.Final.jar  
  inflating: xl-release-10.2.1-server/lib/jboss-logging-3.4.1.Final.jar  
  inflating: xl-release-10.2.1-server/lib/jcifs-1.3.17.jar  
  inflating: xl-release-10.2.1-server/lib/jcip-annotations-1.0-1.jar  
  inflating: xl-release-10.2.1-server/lib/jcip-annotations-1.0.jar  
  inflating: xl-release-10.2.1-server/lib/jcl-over-slf4j-1.7.30.jar  
  inflating: xl-release-10.2.1-server/lib/jdom-2.0.2.jar  
  inflating: xl-release-10.2.1-server/lib/jettison-1.4.1.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-annotations-9.4.39.v20210325.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-client-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-continuation-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-http-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-io-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-jmx-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-plus-9.4.39.v20210325.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-security-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-server-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-servlet-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-servlets-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-util-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-util-ajax-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-webapp-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jetty-xml-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/jffi-1.2.18-native.jar  
  inflating: xl-release-10.2.1-server/lib/jffi-1.2.18.jar  
  inflating: xl-release-10.2.1-server/lib/jmespath-java-1.11.883.jar  
  inflating: xl-release-10.2.1-server/lib/jmustache-1.15.jar  
  inflating: xl-release-10.2.1-server/lib/jnr-a64asm-1.0.0.jar  
  inflating: xl-release-10.2.1-server/lib/jnr-constants-0.9.12.jar  
  inflating: xl-release-10.2.1-server/lib/jnr-ffi-2.1.9.jar  
  inflating: xl-release-10.2.1-server/lib/jnr-x86asm-1.0.2.jar  
  inflating: xl-release-10.2.1-server/lib/joda-convert-2.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/joda-time-2.10.10.jar  
  inflating: xl-release-10.2.1-server/lib/jsch-0.1.55.jar  
  inflating: xl-release-10.2.1-server/lib/json-patch-1.13.jar  
  inflating: xl-release-10.2.1-server/lib/json-path-2.5.0.jar  
  inflating: xl-release-10.2.1-server/lib/json-smart-2.3.jar  
  inflating: xl-release-10.2.1-server/lib/jsoup-1.13.1.jar  
  inflating: xl-release-10.2.1-server/lib/jsr305-3.0.2.jar  
  inflating: xl-release-10.2.1-server/lib/jul-to-slf4j-1.7.30.jar  
  inflating: xl-release-10.2.1-server/lib/jyson-1.0.2.jar  
  inflating: xl-release-10.2.1-server/lib/jython-standalone-2.7.2.patch2618.jar  
  inflating: xl-release-10.2.1-server/lib/jzlib-1.1.3.jar  
  inflating: xl-release-10.2.1-server/lib/kryo-4.0.2.jar  
  inflating: xl-release-10.2.1-server/lib/kryo-serializers-0.45.jar  
  inflating: xl-release-10.2.1-server/lib/lang-tag-1.4.4.jar  
  inflating: xl-release-10.2.1-server/lib/liquibase-core-4.3.4.jar  
  inflating: xl-release-10.2.1-server/lib/liquibase-slf4j-4.0.0.jar  
  inflating: xl-release-10.2.1-server/lib/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar  
  inflating: xl-release-10.2.1-server/lib/lmdbjava-0.7.0.jar  
  inflating: xl-release-10.2.1-server/lib/local-booter-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/log4j-api-2.13.3.jar  
  inflating: xl-release-10.2.1-server/lib/log4j-over-slf4j-1.7.30.jar  
  inflating: xl-release-10.2.1-server/lib/log4j-to-slf4j-2.13.3.jar  
  inflating: xl-release-10.2.1-server/lib/logback-access-1.2.3.jar  
  inflating: xl-release-10.2.1-server/lib/logback-classic-1.2.3.jar  
  inflating: xl-release-10.2.1-server/lib/logback-core-1.2.3.jar  
  inflating: xl-release-10.2.1-server/lib/lz4-java-1.7.1.jar  
  inflating: xl-release-10.2.1-server/lib/maven-artifact-3.6.3.jar  
  inflating: xl-release-10.2.1-server/lib/mbassador-1.3.2.jar  
  inflating: xl-release-10.2.1-server/lib/metrics-annotation-4.0.0+xebialabs.202107071600.jar  
  inflating: xl-release-10.2.1-server/lib/metrics-core-4.0.0+xebialabs.202107071600.jar  
  inflating: xl-release-10.2.1-server/lib/metrics-healthchecks-4.0.0+xebialabs.202107071600.jar  
  inflating: xl-release-10.2.1-server/lib/metrics-jetty9-4.0.0+xebialabs.202107071600.jar  
  inflating: xl-release-10.2.1-server/lib/metrics-jmx-4.0.0+xebialabs.202107071600.jar  
  inflating: xl-release-10.2.1-server/lib/metrics-jvm-4.0.0+xebialabs.202107071600.jar  
  inflating: xl-release-10.2.1-server/lib/metrics-spring-3.1.3.jar  
  inflating: xl-release-10.2.1-server/lib/microprofile-config-api-2.0.jar  
  inflating: xl-release-10.2.1-server/lib/minlog-1.3.0.jar  
  inflating: xl-release-10.2.1-server/lib/msg-simple-1.2.jar  
  inflating: xl-release-10.2.1-server/lib/mxparser-1.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/netty-buffer-4.1.63.Final.jar  
  inflating: xl-release-10.2.1-server/lib/netty-codec-4.1.63.Final.jar  
  inflating: xl-release-10.2.1-server/lib/netty-codec-http-4.1.63.Final.jar  
  inflating: xl-release-10.2.1-server/lib/netty-common-4.1.63.Final.jar  
  inflating: xl-release-10.2.1-server/lib/netty-handler-4.1.63.Final.jar  
  inflating: xl-release-10.2.1-server/lib/netty-resolver-4.1.63.Final.jar  
  inflating: xl-release-10.2.1-server/lib/netty-transport-4.1.63.Final.jar  
  inflating: xl-release-10.2.1-server/lib/netty-transport-native-epoll-4.1.63.Final-linux-x86_64.jar  
  inflating: xl-release-10.2.1-server/lib/netty-transport-native-kqueue-4.1.63.Final-osx-x86_64.jar  
  inflating: xl-release-10.2.1-server/lib/netty-transport-native-unix-common-4.1.63.Final.jar  
  inflating: xl-release-10.2.1-server/lib/nimbus-jose-jwt-8.21.jar  
  inflating: xl-release-10.2.1-server/lib/oauth2-oidc-sdk-8.36.1.jar  
  inflating: xl-release-10.2.1-server/lib/objenesis-2.5.1.jar  
  inflating: xl-release-10.2.1-server/lib/opencensus-api-0.28.0.jar  
  inflating: xl-release-10.2.1-server/lib/opencensus-contrib-http-util-0.28.0.jar  
  inflating: xl-release-10.2.1-server/lib/org.eclipse.jgit-5.11.0.202103091610-r.jar  
  inflating: xl-release-10.2.1-server/lib/org.eclipse.jgit.http.apache-5.11.0.202103091610-r.jar  
  inflating: xl-release-10.2.1-server/lib/overthere-5.3.2.jar  
  inflating: xl-release-10.2.1-server/lib/owasp-java-html-sanitizer-20200713.1.jar  
  inflating: xl-release-10.2.1-server/lib/packager-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/paranamer-2.8.jar  
  inflating: xl-release-10.2.1-server/lib/pdfbox-2.0.23.jar  
  inflating: xl-release-10.2.1-server/lib/perfmark-api-0.23.0.jar  
  inflating: xl-release-10.2.1-server/lib/plexus-utils-3.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/poi-4.1.2.jar  
  inflating: xl-release-10.2.1-server/lib/poi-ooxml-4.1.2.jar  
  inflating: xl-release-10.2.1-server/lib/poi-ooxml-schemas-4.1.2.jar  
  inflating: xl-release-10.2.1-server/lib/proto-google-cloud-os-login-v1-1.2.2.jar  
  inflating: xl-release-10.2.1-server/lib/proto-google-common-protos-2.1.0.jar  
  inflating: xl-release-10.2.1-server/lib/proto-google-iam-v1-1.0.10.jar  
  inflating: xl-release-10.2.1-server/lib/protobuf-java-3.15.5.jar  
  inflating: xl-release-10.2.1-server/lib/protobuf-java-util-3.15.5.jar  
  inflating: xl-release-10.2.1-server/lib/proton-j-0.33.8.jar  
  inflating: xl-release-10.2.1-server/lib/qpid-jms-client-0.58.0.jar  
  inflating: xl-release-10.2.1-server/lib/quartz-2.3.2.jar  
  inflating: xl-release-10.2.1-server/lib/quartz-jobs-2.3.2.jar  
  inflating: xl-release-10.2.1-server/lib/reactive-streams-1.0.3.jar  
  inflating: xl-release-10.2.1-server/lib/reflectasm-1.11.3.jar  
  inflating: xl-release-10.2.1-server/lib/remote-booter-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/resteasy-client-4.6.0.Final.jar  
  inflating: xl-release-10.2.1-server/lib/resteasy-client-api-4.6.0.Final.jar  
  inflating: xl-release-10.2.1-server/lib/resteasy-core-4.6.0.Final.jar  
  inflating: xl-release-10.2.1-server/lib/resteasy-core-spi-4.6.0.Final.jar  
  inflating: xl-release-10.2.1-server/lib/resteasy-jackson2-provider-4.6.0.Final.jar  
  inflating: xl-release-10.2.1-server/lib/resteasy-jaxb-provider-4.6.0.Final.jar  
  inflating: xl-release-10.2.1-server/lib/resteasy-multipart-provider-4.6.0.Final.jar  
  inflating: xl-release-10.2.1-server/lib/resteasy-spring-4.6.0.Final.jar  
  inflating: xl-release-10.2.1-server/lib/scala-arm_2.13-2.1.jar  
  inflating: xl-release-10.2.1-server/lib/scala-collection-compat_2.13-2.1.2.jar  
  inflating: xl-release-10.2.1-server/lib/scala-java8-compat_2.13-0.9.1.jar  
  inflating: xl-release-10.2.1-server/lib/scala-library-2.13.5.jar  
  inflating: xl-release-10.2.1-server/lib/scala-parallel-collections_2.13-1.0.2.jar  
  inflating: xl-release-10.2.1-server/lib/scala-parser-combinators_2.13-1.1.2.jar  
  inflating: xl-release-10.2.1-server/lib/scala-reflect-2.13.5.jar  
  inflating: xl-release-10.2.1-server/lib/scala-xml_2.13-1.3.0.jar  
  inflating: xl-release-10.2.1-server/lib/scannit-1.4.1.jar  
  inflating: xl-release-10.2.1-server/lib/slf4j-api-1.7.30.jar  
  inflating: xl-release-10.2.1-server/lib/slick_2.13-3.3.3.jar  
  inflating: xl-release-10.2.1-server/lib/smallrye-config-1.6.2.jar  
  inflating: xl-release-10.2.1-server/lib/smallrye-config-common-1.6.2.jar  
  inflating: xl-release-10.2.1-server/lib/smbj-0.10.0.jar  
  inflating: xl-release-10.2.1-server/lib/snakeyaml-1.28.jar  
  inflating: xl-release-10.2.1-server/lib/spray-json_2.13-1.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-aop-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-beans-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-boot-2.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/spring-boot-autoconfigure-2.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/spring-boot-starter-2.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/spring-boot-starter-jetty-2.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/spring-boot-starter-json-2.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/spring-boot-starter-logging-2.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/spring-boot-starter-web-2.4.5.jar  
  inflating: xl-release-10.2.1-server/lib/spring-context-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-context-support-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-core-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-data-commons-2.4.7.jar  
  inflating: xl-release-10.2.1-server/lib/spring-expression-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-jcl-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-jdbc-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-jms-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-ldap-core-2.3.3.RELEASE.jar  
  inflating: xl-release-10.2.1-server/lib/spring-messaging-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-security-config-5.4.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-security-core-5.4.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-security-ldap-5.4.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-security-oauth2-client-5.4.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-security-oauth2-core-5.4.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-security-oauth2-jose-5.4.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-security-oauth2-resource-server-5.4.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-security-web-5.4.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-session-core-2.4.3.jar  
  inflating: xl-release-10.2.1-server/lib/spring-session-jdbc-2.4.3.jar  
  inflating: xl-release-10.2.1-server/lib/spring-tx-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-vault-core-2.3.1.jar  
  inflating: xl-release-10.2.1-server/lib/spring-web-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/spring-webmvc-5.3.6.jar  
  inflating: xl-release-10.2.1-server/lib/sshj-0.27.0.jar  
  inflating: xl-release-10.2.1-server/lib/ssl-config-core_2.13-0.4.2.jar  
  inflating: xl-release-10.2.1-server/lib/svnkit-1.10.3.jar  
  inflating: xl-release-10.2.1-server/lib/t2-bus-1.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/threeten-extra-1.6.0.jar  
  inflating: xl-release-10.2.1-server/lib/threetenbp-1.5.0.jar  
  inflating: xl-release-10.2.1-server/lib/tika-core-1.26.jar  
  inflating: xl-release-10.2.1-server/lib/truezip-driver-file-7.7.10.jar  
  inflating: xl-release-10.2.1-server/lib/truezip-driver-tar-7.7.10.jar  
  inflating: xl-release-10.2.1-server/lib/truezip-driver-zip-7.7.10.jar  
  inflating: xl-release-10.2.1-server/lib/truezip-file-7.7.10.jar  
  inflating: xl-release-10.2.1-server/lib/truezip-kernel-7.7.10.jar  
  inflating: xl-release-10.2.1-server/lib/truezip-swing-7.7.10.jar  
  inflating: xl-release-10.2.1-server/lib/txw2-2.3.4.jar  
  inflating: xl-release-10.2.1-server/lib/udm-plugin-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/websocket-api-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/websocket-client-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/websocket-common-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/websocket-server-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/websocket-servlet-9.4.40.v20210413.jar  
  inflating: xl-release-10.2.1-server/lib/xbean-asm7-shaded-4.15.jar  
  inflating: xl-release-10.2.1-server/lib/xl-auth-oidc-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-base-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-chain-of-custody-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-cluster-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-core-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-devops-as-code-common-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-endpoints-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-endpoints-routes-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-license-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-license-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-license-web-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-localisation-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-plugin-manager-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-repository-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-repository-sql-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-script-engine-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-security-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-security-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-security-converter-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-security-core-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-ui-components-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-ui-components-rest-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-utils-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-webhooks-common-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-webhooks-endpoint-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xl-webhooks-queue-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-activity-logs-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-api-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-api-domain-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-chainofcustody-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-dashboard-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-devops-as-code-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-devops-as-code-versioning-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-domain-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-domain-base-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-domain-utils-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-environment-management-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-external-provider-lookup-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-external-script-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-fixtures-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-groovy-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-json-serialization-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-lookup-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-notifications-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-planner-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-quartz-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-release-delivery-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-release-group-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-release-summary-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-reports-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-risk-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-scm-connector-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-script-executor-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-security-sql-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-server-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-server-sql-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-template-versioning-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-trigger-management-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-udm-reporting-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-ui-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-webhooks-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xlr-xlpm-module-10.2.1.jar  
  inflating: xl-release-10.2.1-server/lib/xmlbeans-3.1.0.jar  
  inflating: xl-release-10.2.1-server/lib/xmlpull-1.1.3.1.jar  
  inflating: xl-release-10.2.1-server/lib/xstream-1.4.16.jar  
  inflating: xl-release-10.2.1-server/lib/xz-1.5.jar  
   creating: xl-release-10.2.1-server/log/
  inflating: xl-release-10.2.1-server/log/readme.txt  
   creating: xl-release-10.2.1-server/plugins/
   creating: xl-release-10.2.1-server/plugins/__local__/
  inflating: xl-release-10.2.1-server/plugins/__local__/readme.txt  
  inflating: xl-release-10.2.1-server/plugins/readme.txt  
   creating: xl-release-10.2.1-server/plugins/xlr-official/
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-audit-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-auth-default-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-auth-oidc-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-git-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-github-webhooks-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-jenkins-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-jira-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-nexus-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-remotescript-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-svn-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-time-plugin-10.2.1.jar  
  inflating: xl-release-10.2.1-server/readme.txt  
   creating: xl-release-10.2.1-server/serviceWrapper/
  inflating: xl-release-10.2.1-server/serviceWrapper/LICENSE.txt  
   creating: xl-release-10.2.1-server/serviceWrapper/bat/
   creating: xl-release-10.2.1-server/serviceWrapper/bat/demos/
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/demos/installServicesManagerServer.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/demos/jnlpDemo.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/demos/remoteLaunchTomcatDemo.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/demos/runScript.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/demos/testMail.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/genConfig.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/installService.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/keystore.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/queryService.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/runConsole.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/runConsoleW.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/runHelloWorld.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/runServicesManagerClient.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/runServicesManagerServer.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/setenv.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/startService.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/stopService.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/systemTrayIcon.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/sytemTrayIconW.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/uninstallService.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/wrapper.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/bat/wrapperW.bat  
   creating: xl-release-10.2.1-server/serviceWrapper/bin/
   creating: xl-release-10.2.1-server/serviceWrapper/bin/demos/
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/demos/jnlpDemo.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/demos/remoteLaunchTomcatDemo.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/genConfig.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/installDaemon.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/installDaemonD.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/installDaemonNoPriv.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/installDaemonNoPrivD.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/queryDaemon.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/queryDaemonNoPriv.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/runConsole.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/runHelloWorld.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/setenv.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/startDaemon.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/startDaemonNoPriv.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/stopDaemon.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/stopDaemonNoPriv.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/systemTrayIcon.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/uninstallDaemon.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/uninstallDaemonD.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/uninstallDaemonNoPriv.sh  
  inflating: xl-release-10.2.1-server/serviceWrapper/bin/wrapper.sh  
   creating: xl-release-10.2.1-server/serviceWrapper/build/
  inflating: xl-release-10.2.1-server/serviceWrapper/build/MANIFEST.MF  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/ReadMe.txt  
   creating: xl-release-10.2.1-server/serviceWrapper/build/abeille/
  inflating: xl-release-10.2.1-server/serviceWrapper/build/abeille/ConsoleForm.xml  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/abeille/ReadMe.txt  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/abeille/WSForm.xml  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/abeille/srvmgr_install_dialog.xml  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/abeille/srvmgr_newHostDialog.xml  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/abeille/srvmgr_reload_console_dialog.xml  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/abeille/srvmgr_uninstall_dialog.xml  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/abeille/srvmgr_window.xml  
   creating: xl-release-10.2.1-server/serviceWrapper/build/gradle/
   creating: xl-release-10.2.1-server/serviceWrapper/build/gradle/ahessian/
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/ahessian/build.gradle  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/build.gradle  
   creating: xl-release-10.2.1-server/serviceWrapper/build/gradle/gradle/
   creating: xl-release-10.2.1-server/serviceWrapper/build/gradle/gradle/wrapper/
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/gradle/wrapper/gradle-wrapper.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/gradle/wrapper/gradle-wrapper.properties  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/gradlew.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/gradlew.sh  
   creating: xl-release-10.2.1-server/serviceWrapper/build/gradle/groovy-patch/
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/groovy-patch/build.gradle  
   creating: xl-release-10.2.1-server/serviceWrapper/build/gradle/hessian4/
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/hessian4/build.gradle  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/readMe.txt  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/settings.gradle  
   creating: xl-release-10.2.1-server/serviceWrapper/build/gradle/wrapper-app/
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/wrapper-app/build.gradle  
   creating: xl-release-10.2.1-server/serviceWrapper/build/gradle/wrapper/
  inflating: xl-release-10.2.1-server/serviceWrapper/build/gradle/wrapper/build.gradle  
   creating: xl-release-10.2.1-server/serviceWrapper/build/ws/
  inflating: xl-release-10.2.1-server/serviceWrapper/build/ws/ReadMe.txt  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/ws/genKeyStore.bat  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/ws/jaxb.keys  
  inflating: xl-release-10.2.1-server/serviceWrapper/build/ws/sign.bat  
   creating: xl-release-10.2.1-server/serviceWrapper/conf/
   creating: xl-release-10.2.1-server/serviceWrapper/conf/samples/
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/ReadMe.txt  
   creating: xl-release-10.2.1-server/serviceWrapper/conf/samples/luceneNutch/
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/luceneNutch/ReadMe.txt  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/luceneNutch/wrapper.nutch_crawl.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/luceneNutch/wrapper.nutch_crawl_groovy.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/luceneNutch/wrapper.nutch_recrawl_groovy.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/luceneNutch/wrapper.nutch_recrawl_solr_groovy.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/luceneNutch/wrapper.nutch_tomcat.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/tomcat.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/tomcat.stop.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.activemq.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.derby.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.derby.stop.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.equinox.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.groovy_helloworld.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.jar.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.jboss.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.jboss7.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.jetty.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.oracle.odi.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.ping.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/samples/wrapper.wso2_cep.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/wrapper.conf  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/wrapper.conf.default  
  inflating: xl-release-10.2.1-server/serviceWrapper/conf/wrapper.javaws.conf  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/
   creating: xl-release-10.2.1-server/serviceWrapper/lib/core/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/ReadMe.txt  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/commons-cli-1.4.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/commons-collections-3.2.2.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/commons-configuration2-2.7.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/commons-io-2.6.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/commons-lang-2.6.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/commons-lang3-3.8.1.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/commons-logging-1.1.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/commons-text-1.8.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/commons/commons-vfs2-2.2.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/core/jna/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/jna/jna-5.3.1.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/jna/jna-platform-5.3.1.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/core/netty/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/netty/netty-all-4.1.46.Final.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/core/yajsw/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/core/yajsw/ahessian.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/ReadMe.txt  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/abeille/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/abeille/formsrt.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/commons/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/commons/commons-codec-1.14.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/commons/commons-httpclient-3.0.1.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/commons/commons-net-1.4.1.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/commons/commons-net-3.2.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/cron/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/cron/joda-time-2.7.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/cron/prevayler-core-2.6.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/cron/prevayler-factory-2.6.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/cron/yacron4j-00.03.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/glazedlists/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/glazedlists/commons-beanutils-1.9.4.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/glazedlists/glazedlists-1.10.0_java16.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/groovy/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/groovy/groovy-2.5.7.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/groovy/groovy-patch.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/jgoodies/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/jgoodies/forms-1.2.0.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/keystore/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/keystore/keystore.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/regex/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/regex/automaton-1.11.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/velocity/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/velocity/velocity-1.7.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/vfs-dbx/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/vfs-dbx/dropbox-core-sdk-1.7.7.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/vfs-dbx/jackson-core-2.8.6.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/vfs-dbx/vfs-dbx-00.02.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/vfs-webdav/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/vfs-webdav/jackrabbit-webdav-1.5.6.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/vfs-webdav/slf4j-api-1.8.0-beta4.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/vfs-webdav/slf4j-jdk14-1.8.0-beta4.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/vfs-webdav/xercesImpl.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/extended/yajsw/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/yajsw/hessian4.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/extended/yajsw/srvmgr.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/ReadMe.txt  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/jasypt/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/jasypt/jasypt-1.9.2.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/joda/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/joda/joda-time-2.7.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/mail/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/mail/activation.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/mail/javax.mail-1.5.2.jar  
   creating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/snmp/
  inflating: xl-release-10.2.1-server/serviceWrapper/lib/groovy/snmp/SNMP4J.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/readme.txt  
   creating: xl-release-10.2.1-server/serviceWrapper/scripts/
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/ReadMe.txt  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/cluster.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/commandCondition.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/decryptor.gv  
   creating: xl-release-10.2.1-server/serviceWrapper/scripts/equinox/
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/equinox/equinoxShutdown.gv  
   creating: xl-release-10.2.1-server/serviceWrapper/scripts/groovy/
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/groovy/ReadMe.txt  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/groovy/helloworld.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/linearRestartDelay.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/mapNetworkDrive.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/maxDuration.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/maxStartup.gv  
   creating: xl-release-10.2.1-server/serviceWrapper/scripts/nutch/
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/nutch/ReadMe.txt  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/nutch/nutch_base.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/nutch/nutch_crawl.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/nutch/nutch_recrawl.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/nutch/nutch_solr.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/sendMail.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/setenv.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/snmpTrap.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/threadDump.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/timeCondition.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/trayColor.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/trayMessage.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/vfsCommandCondition.gv  
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/winEventLog.gv  
   creating: xl-release-10.2.1-server/serviceWrapper/scripts/wso2/
  inflating: xl-release-10.2.1-server/serviceWrapper/scripts/wso2/wso2_shutdown.gv  
   creating: xl-release-10.2.1-server/serviceWrapper/templates/
  inflating: xl-release-10.2.1-server/serviceWrapper/templates/daemon.vm  
  inflating: xl-release-10.2.1-server/serviceWrapper/templates/launchd.plist.vm  
  inflating: xl-release-10.2.1-server/serviceWrapper/templates/systemd.vm  
  inflating: xl-release-10.2.1-server/serviceWrapper/wrapper.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/wrapperApp.jar  
  inflating: xl-release-10.2.1-server/serviceWrapper/yajsw.policy.txt  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-docker-compose-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-ansible-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-remoting-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-task-progress-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-xld-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-openshift-plugin-10.2.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-relationships-plugin-10.2.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-opsgenie-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-sonarqube-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-fortify-ssc-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-fortify-on-demand-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-blackduck-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-checkmarx-plugin-10.1.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-agility-integration-10.2.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-vsts-tfs-plugin-10.2.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-continuous-testing-plugin-10.2.0.jar  
  inflating: xl-release-10.2.1-server/plugins/xlr-official/xlr-remote-completion-plugin-10.2.0.jar  
</pre>

You may start the XL Release tool with basic configuration as shown below
<pre>
jegan@tektutor:~/Downloads/xl-release-10.2.1-server/bin$ ./run.sh 
WARNING: A command line option has enabled the Security Manager
WARNING: The Security Manager is deprecated and will be removed in a future release
2023-06-09 16:48:24.982 [main] {} INFO  c.xebialabs.xlrelease.ReleaseServer - XL Release version 10.2.1 (built at 21-07-23 15:15:46)
2023-06-09 16:48:24.985 [main] {} INFO  c.xebialabs.xlrelease.ReleaseServer - XL Release running with OpenJDK Runtime Environment (version 18.0.2-ea+9-Ubuntu-222.04) from Private Build
2023-06-09 16:48:24.987 [main] {} INFO  c.xebialabs.xlrelease.ReleaseServer - (c) 2012-2023 Digital.ai.
2023-06-09 16:48:25.431 [main] {} WARN  c.xebialabs.xlrelease.ReleaseServer - Configuration not found...
2023-06-09 16:48:25.438 [main] {} INFO  c.xebialabs.xlrelease.ReleaseServer - Initializing Repository

Welcome to the XL Release setup.
You can always exit by typing 'exitsetup'.
To re-run this setup and make changes to the XL Release configuration you can run run.cmd -setup on Windows or run.sh -setup on Unix.

Do you want to use the simple setup?
Default values are used for all properties. To make changes to the default properties, please answer no.
Options are yes or no.
[yes]: <b>yes</b>

Please enter the admin password you wish to use for XL Release
New password: 
Re-type password: 

The password encryption key protects the passwords stored in the repository. 
Do you want to generate a new password encryption key?
Options are yes or no.
[yes]: <b>no</b>

Do you agree with the following settings for XL Release and would you like to save them?
Changes will be saved in xl-release-server.conf
	SSL will be disabled
	HTTP bind address is 0.0.0.0
	HTTP port is 5516
	Context root is /
	Public Server URL is http://tektutor:5516/
	HTTP server will use a minimum of 30 and a maximum of 150 threads
[yes]: <b>yes</b>
Saving to /home/jegan/Downloads/xl-release-10.2.1-server/conf/xl-release-server.conf
Configuration saved.
You can now start your XL Release by executing the command run.cmd on Windows or run.sh on Unix.
Note: If your XL Release is running please restart it.
Finished setup.
Jun 09, 2023 4:48:59 PM de.schlichtherle.truezip.socket.sl.IOPoolLocator$Boot create
WARNING: Found two services with the same priority 100
First: de.schlichtherle.truezip.fs.nio.file.TempFilePoolService[priority=100]
Second: de.schlichtherle.truezip.fs.nio.file.TempFilePoolService[priority=100]
2023-06-09 16:49:01.994 [main] {} INFO  c.x.d.b.local.TypeSystemBootstrapper - Initializing type system.
2023-06-09 16:49:02.814 [main] {} INFO  c.x.d.b.l.LocalGlobalContextManager - Could not find 'conf/deployit-defaults.properties', continuing without loading defaults
2023-06-09 16:49:03.339 [main] {} INFO  c.x.x.s.c.XlrWebApplicationInitializer - Running in standalone mode
2023-06-09 16:49:03.341 [main] {} INFO  c.x.x.s.c.XlrWebApplicationInitializer - Authentication provider: default
2023-06-09 16:49:03.350 [main] {} INFO  c.x.xlrelease.XLReleaseBootstrapper$ - Starting XLReleaseBootstrapper. using Java 18.0.2-ea on tektutor with PID 344526 (/home/jegan/Downloads/xl-release-10.2.1-server/lib/xlr-server-10.2.1.jar started by jegan in /home/jegan/Downloads/xl-release-10.2.1-server)
2023-06-09 16:49:03.350 [main] {} INFO  c.x.xlrelease.XLReleaseBootstrapper$ - The following profiles are active: default,defaultAuth
2023-06-09 16:49:07.690 [main] {} INFO  c.x.x.s.j.ReleaseJettyServletWebServerFactory - Server initialized with port: 5516
2023-06-09 16:49:07.691 [main] {} INFO  c.x.x.s.j.ReleaseJettyServerCustomizer - Server listens on 0.0.0.0:5516 (not secure)
2023-06-09 16:49:07.884 [main] {} INFO  c.x.x.s.c.SqlConfiguration$$EnhancerBySpringCGLIB$$93655d5c - Repository database is h2:1.4.200 (2019-10-14)
2023-06-09 16:49:07.894 [main] {} INFO  c.x.x.db.sql.SqlDialectDetector - SQL dialect for h2 is CommonDialect(h2)
2023-06-09 16:49:09.747 [main] {} INFO  c.x.x.db.sql.SqlDialectDetector - SQL dialect for apache derby is DerbyDialect(apache derby)
2023-06-09 16:49:09.862 [main] {} INFO  c.x.x.customscripts.ScriptTypes - Custom Tasks: 
  ansible.RunPlaybook
  blackduck.checkCompliance
  checkmarx.checkCompliance
  checkmarx.checkOsaCompliance
  checkmarx.gitScan
  checkmarx.svnScan
  continuousTesting.CreateTestView
  continuousTesting.GetTestRunStatusAndResultForUnitTests
  continuousTesting.GetTestViewResults
  continuousTesting.TriggerEspressoTestCase
  continuousTesting.TriggerXCUITestCase
  delivery.CreateDelivery
  delivery.FindDelivery
  delivery.FindOrCreateDelivery
  delivery.MarkTrackedItems
  delivery.RegisterTrackedItems
  delivery.WaitForStage
  delivery.WaitForTrackedItems
  dockerCompose.command
  dockerCompose.down
  dockerCompose.start
  dockerCompose.stop
  dockerCompose.up
  fortify.checkCompliance
  fortifyOnDemand.checkCompliance
  jenkins.Build
  jira.CheckIssue
  jira.CheckQuery
  jira.CreateIssue
  jira.CreateIssueJson
  jira.CreateSubtask
  jira.GetAllSprints
  jira.GetIssueDetails
  jira.GetVersions
  jira.Query
  jira.QueryForIssueIds
  jira.UpdateIssue
  jira.UpdateIssues
  jira.UpdateIssuesByQuery
  openshift.checkService
  openshift.createConf
  openshift.imageTag
  openshift.imageTagApi
  openshift.removeConf
  openshift.serviceStatus
  openshift.startBuild
  openshift.startBuildApi
  openshift.startDeployment
  openshift.startDeploymentApi
  opsgenie.closeAlert
  opsgenie.createAlert
  opsgenie.deleteAlert
  opsgenie.disableIntegration
  opsgenie.enableIntegration
  opsgenie.executeCustomAction
  opsgenie.snoozeAlert
  remoteScript.Unix
  remoteScript.Windows
  remoteScript.WindowsSmb
  remoteScript.WindowsSsh
  remoteScript.Zos
  sonar.checkCompliance
  sonarCloud.checkCompliance
  versionone.AssetFromTemplate
  versionone.CreateAsset
  versionone.CreateIssue
  versionone.checkAsset
  versionone.checkIssue
  versionone.checkQuery
  versionone.getAsset
  versionone.getStories
  versionone.getStory
  versionone.updateAssetStatuses
  versionone.updateStoryStatus
  vsts.AddBuildTag
  vsts.ApproveDeploymentInRelease
  vsts.CreateRelease
  vsts.CreateWorkItem
  vsts.GetWorkItem
  vsts.QueryWorkItems
  vsts.QueueBuild
  vsts.StartDeploymentInRelease
  vsts.TfsQueryWorkItems
  vsts.UpdateWorkItem
  vsts.UpdateWorkItemsByQuery
  vsts.UpdateWorkItemsByTfsQuery
  vsts.WaitForRelease
  vsts.WaitForWorkItemState
  webhook.JsonWebhook
  webhook.WaitForJsonEvent
  webhook.XmlWebhook
  xld.AddCITag
  xld.CreateCI
  xld.CreateFolderTree
  xld.DeleteCI
  xld.DeleteInfrastructure
  xld.DoesCIExist
  xld.GetAllVersions
  xld.GetCIBooleanProperty
  xld.GetCIDateProperty
  xld.GetCIIntegerProperty
  xld.GetCIListProperty
  xld.GetCIMapProperty
  xld.GetCIMapPropertyKey
  xld.GetCISetProperty
  xld.GetCIStringProperty
  xld.GetCITags
  xld.GetCITask
  xld.GetLastVersionDeployed
  xld.GetLatestVersion
  xld.ImportTask
  xld.Migrate
  xld.SetCITags
  xld.UpdateCIProperty
  xld.cli
  xld.cliFile
  xld.cliUrl
  xldeploy.Controltask
  xldeploy.Deploy
  xldeploy.Undeploy

2023-06-09 16:49:09.862 [main] {} INFO  c.x.x.customscripts.ScriptTypes - Release triggers: 
  git.Poll
  jira.IssueTrigger
  nexus.PublishedArtifact
  svn.Poll
  time.Schedule
  vsts.GitCommitTrigger
  vsts.TfvcChangesetTrigger

2023-06-09 16:49:09.862 [main] {} INFO  c.x.x.customscripts.ScriptTypes - Export hooks: 

2023-06-09 16:49:11.142 [main] {} WARN  o.s.b.GenericTypeAwarePropertyDescriptor - Invalid JavaBean property 'logoutHandlers' being accessed! Ambiguous write methods found next to actually used [public void org.springframework.security.web.session.ConcurrentSessionFilter.setLogoutHandlers(org.springframework.security.web.authentication.logout.LogoutHandler[])]: [public void org.springframework.security.web.session.ConcurrentSessionFilter.setLogoutHandlers(java.util.List)]
16:49:11,232 |-INFO in LogbackRequestLog - Will use configuration file [conf/logback-access.xml]
16:49:11,236 |-INFO in ch.qos.logback.access.joran.action.ConfigurationAction - debug attribute not set
16:49:11,236 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [ch.qos.logback.core.rolling.RollingFileAppender]
16:49:11,236 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [FILE]
16:49:11,237 |-INFO in c.q.l.core.rolling.TimeBasedRollingPolicy@1475610516 - setting totalSizeCap to 50 MB
16:49:11,237 |-INFO in c.q.l.core.rolling.TimeBasedRollingPolicy@1475610516 - Will use zip compression
16:49:11,238 |-INFO in c.q.l.core.rolling.TimeBasedRollingPolicy@1475610516 - Will use the pattern log/access.%d{yyyy-MM-dd} for the active file
16:49:11,238 |-INFO in c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy - The date pattern is 'yyyy-MM-dd' from file name pattern 'log/access.%d{yyyy-MM-dd}.zip'.
16:49:11,238 |-INFO in c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy - Roll-over at midnight.
16:49:11,238 |-INFO in c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy - Setting initial period to Fri Jun 09 16:49:11 IST 2023
16:49:11,239 |-INFO in ch.qos.logback.core.joran.action.NestedComplexPropertyIA - Assuming default type [ch.qos.logback.access.PatternLayoutEncoder] for [encoder] property
16:49:11,246 |-INFO in ch.qos.logback.core.rolling.RollingFileAppender[FILE] - Active log file name: log/access.log
16:49:11,246 |-INFO in ch.qos.logback.core.rolling.RollingFileAppender[FILE] - File property is set to [log/access.log]
16:49:11,246 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [FILE] to null
16:49:11,246 |-INFO in ch.qos.logback.access.joran.action.ConfigurationAction - End of configuration.
16:49:11,246 |-INFO in ch.qos.logback.access.joran.JoranConfigurator@723c84d6 - Registering current configuration as safe fallback point

2023-06-09 16:49:15.306 [main] {} INFO  c.x.x.p.r.email.SignatureService - Email case sensitive is configured to: false
2023-06-09 16:49:15.428 [main] {} INFO  c.x.x.r.ReleaseExtensionsRepository - Registering generic ReleaseExtension: /summary
2023-06-09 16:49:16.452 [main] {} INFO  c.x.x.r.ReleaseExtensionsRepository - Registering ReleaseExtension: /Risk
2023-06-09 16:49:18.348 [main] {} INFO  c.x.x.r.ReleaseExtensionsRepository - Registering generic ReleaseExtension: /progress
2023-06-09 16:49:21.720 [main] {} INFO  c.x.x.p.r.NonClusteredRemoteCompletionInitializer - Remote completion initialising in cluster STANDALONE/HOT_STANDBY profile
2023-06-09 16:49:21.721 [main] {} INFO  c.x.x.actors.ActorSystemHolder - Starting up actor system.
2023-06-09 16:49:21.800 [XlReleaseSystem-7] {akkaAddress=akka://XlReleaseSystem, sourceThread=main, akkaSource=akka.serialization.Serialization(akka://XlReleaseSystem), sourceActorSystem=XlReleaseSystem, akkaTimestamp=11:19:21.799UTC} WARN  a.s.Serialization(akka://XlReleaseSystem) - Using serializer [io.altoo.akka.serialization.kryo.KryoSerializer] for message [akka.actor.ActorRef]. Note that this serializer is not implemented by Akka. It's not recommended to replace serializers for messages provided by Akka.
2023-06-09 16:49:21.835 [XlReleaseSystem-8] {akkaAddress=akka://XlReleaseSystem, sourceThread=XlReleaseSystem-7, akkaSource=akka://XlReleaseSystem/user/$a, sourceActorSystem=XlReleaseSystem, akkaTimestamp=11:19:21.833UTC} INFO  c.x.x.p.r.RemoteCompletionActor - Mail fetching scheduler successfully started
2023-06-09 16:49:21.882 [main] {} INFO  c.x.deployit.event.EventBusHolder - Finding all Listeners...
2023-06-09 16:49:21.884 [main] {} INFO  c.x.deployit.event.EventBusHolder - Registering Listener: class com.xebialabs.deployit.repository.RepositoryEventListener
2023-06-09 16:49:21.908 [main] {} INFO  c.x.deployit.event.EventBusHolder - Registering Listener: class com.xebialabs.deployit.audit.TextLoggingAuditableEventListener
2023-06-09 16:49:21.911 [main] {} INFO  c.x.deployit.event.EventBusHolder - Registering spring-instantiated object: com.xebialabs.xlrelease.events.handlers.PreArchiveServiceInitializer@5e5c4a5e
2023-06-09 16:49:21.912 [main] {} INFO  c.x.deployit.event.EventBusHolder - Registering spring-instantiated object: com.xebialabs.plugin.manager.event.UpdateRepositoryEventListener@318eb2cf
2023-06-09 16:49:21.913 [main] {} INFO  c.x.deployit.event.EventBusHolder - Registering spring-instantiated object: com.xebialabs.xlrelease.server.jetty.ShutdownEventListener@4238ac1c
2023-06-09 16:49:22.171 [main] {} INFO  o.jboss.resteasy.plugins.spring.i18n - RESTEASY013075: ResteasyHandlerMapping has the default order and throwNotFound settings.  Consider adding explicit ordering to your HandlerMappings, with ResteasyHandlerMapping being lsat, and set throwNotFound = true.
2023-06-09 16:49:25.130 [main] {} INFO  c.x.xlrelease.service.ServiceStarter - Configuring scripting engine - Java security is configured, script sandbox is enabled.
2023-06-09 16:49:25.368 [main] {} INFO  c.x.xlrelease.service.ServiceStarter - Initializing databases.
2023-06-09 16:49:27.941 [main] {} INFO  c.x.d.upgrade.RepositoryInitializer - Initializing repository for component [xl-release] using: [DatabasesInitializer, DefaultEnvironmentStagesInitializer, DefaultRiskProfileInitializer, EmailNotificationSettingsInitializer, RiskGlobalThresholdsInitializer, SampleTemplatesInitialization, SettingsInitializer, SystemMessageInitializer, TemplateVersioningInitializer, TriggerDataPurgeSettingsInitializer, TutorialsFolderInitializer, UserInitializer, XLRelease100MentionEmailNotificationsUpgrade, XLRelease100PermissionSnapshotUpgrader, XLRelease100ReportJobEmailNotificationsUpgrade, XLRelease1010SmtpServerUpgrader, XLRelease720BulkOperationsEmailNotificationsUpgrade, XLRelease800LockTaskPermissionsUpgrade, XLRelease820EditFailureHandlerAndPreconditionPermissionsUpgrade, XLRelease900ReportAuditReportUpgrade, XLRelease900ReportJobEmailNotificationsUpgrade, XLRelease950ReportJobEmailNotificationsUpgrade, XLRelease970EmailNotificationsRebrandingUpgrade]
2023-06-09 16:49:27.942 [main] {} INFO  c.x.x.upgrade.DatabasesInitializer - Initializing reporting database...
2023-06-09 16:49:28.155 [main] {} INFO  c.x.x.upgrade.DatabasesInitializer - Initializing repository database...
2023-06-09 16:49:28.330 [main] {} INFO  c.x.x.e.i.DefaultEnvironmentStagesInitializer - Creating default environment stages: Development, Test, Acceptance, Production
2023-06-09 16:49:28.357 [main] {} INFO  c.x.x.r.i.DefaultRiskProfileInitializer - Creating default risk profile
2023-06-09 16:49:28.446 [main] {} INFO  c.x.x.n.i.EmailNotificationSettingsInitializer - Creating default email notification settings
2023-06-09 16:49:28.454 [main] {} INFO  c.x.x.r.i.RiskGlobalThresholdsInitializer - Creating default risk global thresholds
2023-06-09 16:49:28.456 [main] {} INFO  c.x.x.i.SampleTemplatesInitialization - Importing sample templates
2023-06-09 16:49:28.964 [main] {} INFO  c.x.x.i.SampleTemplatesInitialization - Imported template Applications/ReleaseTemplate_welcome from templates/welcome.json
2023-06-09 16:49:29.037 [main] {} INFO  c.x.x.i.SampleTemplatesInitialization - Imported template Applications/ReleaseTemplate_configure from templates/configure.json
2023-06-09 16:49:29.127 [main] {} INFO  c.x.x.i.SampleTemplatesInitialization - Imported template Applications/ReleaseTemplate_tour from templates/tour.json
2023-06-09 16:49:29.237 [main] {} INFO  c.x.x.i.SampleTemplatesInitialization - Imported template Applications/ReleaseTemplate_sample from templates/sample.json
2023-06-09 16:49:29.245 [main] {} WARN  c.x.x.r.sql.SqlRepositoryAdapter - Could not load ConfigurationItem 'Configuration/Custom/XL Deploy' from SQL
2023-06-09 16:49:29.338 [main] {} INFO  c.x.x.i.SampleTemplatesInitialization - Imported template Applications/ReleaseTemplate_blue_green_deployment from templates/blue-green.json
2023-06-09 16:49:29.422 [main] {} INFO  c.x.x.i.SampleTemplatesInitialization - Imported template Applications/ReleaseTemplate_sample_with_Deployit from templates/sample-with-xl-deploy.json
2023-06-09 16:49:29.429 [main] {} WARN  c.x.x.r.sql.SqlRepositoryAdapter - Could not load ConfigurationItem 'Configuration/Custom/XL Deploy' from SQL
2023-06-09 16:49:29.431 [main] {} WARN  c.x.x.r.sql.SqlRepositoryAdapter - Could not load ConfigurationItem 'Configuration/Custom/XL Deploy' from SQL
2023-06-09 16:49:29.432 [main] {} WARN  c.x.x.r.sql.SqlRepositoryAdapter - Could not load ConfigurationItem 'Configuration/Custom/XL Deploy' from SQL
2023-06-09 16:49:29.491 [main] {} INFO  c.x.x.i.SampleTemplatesInitialization - Imported template Applications/ReleaseTemplate_canary_deployment from templates/canary.json
2023-06-09 16:49:29.512 [main] {} INFO  c.x.xlrelease.service.FolderService - Creating folder Samples & Tutorials with id Applications/FolderSamplesAndTutorials
2023-06-09 16:49:30.604 [main] {} INFO  c.x.x.n.i.XLRelease100MentionEmailNotificationsUpgrade - Adding email notification settings for user mentions
2023-06-09 16:49:30.612 [main] {} INFO  c.x.x.n.i.XLRelease100PermissionSnapshotUpgrader - Creating global permissions snapshot
2023-06-09 16:49:30.621 [main] {} INFO  c.x.x.n.i.XLRelease100PermissionSnapshotUpgrader - Global permissions snapshot created
2023-06-09 16:49:30.624 [main] {} INFO  c.x.x.n.i.XLRelease100PermissionSnapshotUpgrader - Creating permissions snapshot for folder [/FolderSamplesAndTutorials]
2023-06-09 16:49:30.634 [main] {} INFO  c.x.x.n.i.XLRelease100PermissionSnapshotUpgrader - Permissions snapshot for folder [/FolderSamplesAndTutorials] created
2023-06-09 16:49:30.646 [main] {} INFO  c.x.x.n.i.XLRelease100ReportJobEmailNotificationsUpgrade - Adding back notification settings for audit report
2023-06-09 16:49:30.674 [auxiliary-pool-1-thread-1] {} INFO  c.x.xlrelease.service.ReleaseService - Creating new release Applications/FolderSamplesAndTutorials/Release40d7e5bcf8d747eca646a2b3e922b9e4 from template Applications/FolderSamplesAndTutorials/ReleaseTemplate_welcome
2023-06-09 16:49:30.681 [main] {} INFO  c.x.x.n.i.XLRelease720BulkOperationsEmailNotificationsUpgrade - Adding email notification settings for release bulk operations
2023-06-09 16:49:30.684 [main] {} INFO  c.x.x.u.d.XLRelease800LockTaskPermissionsUpgrade - 'Lock task permission upgrade.
2023-06-09 16:49:30.739 [main] {} INFO  c.x.x.u.d.XLRelease820EditFailureHandlerAndPreconditionPermissionsUpgrade - Edit failure handler and precondition permissions upgrade
2023-06-09 16:49:30.813 [main] {} INFO  c.x.x.n.i.XLRelease900ReportJobEmailNotificationsUpgrade - Adding email notification settings for report jobs
2023-06-09 16:49:30.820 [main] {} INFO  c.x.x.n.i.XLRelease950ReportJobEmailNotificationsUpgrade - Adding email notification settings for aborted report jobs
2023-06-09 16:49:30.848 [main] {} INFO  c.x.d.upgrade.RepositoryInitializer - Initializing repository for component [xlr-xlpm-module] using: [XlrPluginManagerDbInitializer]
2023-06-09 16:49:30.957 [main] {} INFO  c.x.d.upgrade.RepositoryInitializer - Initializing repository for component [xlr-quartz-module] using: [QuartzDbInitializer]
2023-06-09 16:49:31.498 [main] {} INFO  c.x.x.a.i.ActorSystemInitializer - Initializing actor system in Standalone mode.
2023-06-09 16:49:31.522 [main] {} INFO  c.x.x.q.config.QuartzInitializer - Starting Quartz Scheduler
2023-06-09 16:49:31.532 [XlReleaseSystem-8] {akkaAddress=akka://XlReleaseSystem, sourceThread=XlReleaseSystem-7, akkaSource=akka://XlReleaseSystem/user/$b, sourceActorSystem=XlReleaseSystem, akkaTimestamp=11:19:31.532UTC} INFO  c.x.xlrelease.actors.ArchivingActor - Scheduling archiving job using cron 16 * * * * ?
2023-06-09 16:49:31.674 [main] {} INFO  o.a.activemq.broker.BrokerService - Loaded the Bouncy Castle security provider at position: -1
2023-06-09 16:49:31.699 [main] {activemq.broker=embedded-broker} INFO  o.a.activemq.broker.BrokerService - Using Persistence Adapter: MemoryPersistenceAdapter
2023-06-09 16:49:31.754 [XlReleaseSystem-11] {activity=RELEASE_STARTED, targetId=Applications/FolderSamplesAndTutorials/Release40d7e5bcf8d747eca646a2b3e922b9e4, targetType=xlrelease.Release, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Started Release
2023-06-09 16:49:31.791 [XlReleaseSystem-11] {activity=PHASE_STARTED, targetId=Applications/FolderSamplesAndTutorials/Release40d7e5bcf8d747eca646a2b3e922b9e4/Phase2437552, targetType=xlrelease.Phase, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Started Phase 'Introduction'
2023-06-09 16:49:31.810 [XlReleaseSystem-11] {activity=TASK_STARTED, targetId=Applications/FolderSamplesAndTutorials/Release40d7e5bcf8d747eca646a2b3e922b9e4/Phase2437552/Task680725, targetType=xlrelease.Task, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Started Task 'Welcome! Click me to get started'
2023-06-09 16:49:31.853 [main] {activemq.broker=embedded-broker} INFO  o.a.activemq.broker.BrokerService - Apache ActiveMQ 5.16.2 (embedded-broker, ID:tektutor-33983-1686309571712-0:1) is starting
2023-06-09 16:49:31.858 [main] {activemq.broker=embedded-broker} INFO  o.a.activemq.broker.BrokerService - Apache ActiveMQ 5.16.2 (embedded-broker, ID:tektutor-33983-1686309571712-0:1) started
2023-06-09 16:49:31.858 [main] {activemq.broker=embedded-broker} INFO  o.a.activemq.broker.BrokerService - For help or more information please see: http://activemq.apache.org
2023-06-09 16:49:31.869 [auxiliary-pool-1-thread-1] {} WARN  c.x.xlrelease.service.ReleaseService - Variable user not found on a template $Applications/FolderSamplesAndTutorials/ReleaseTemplate_configure. It will be added.
2023-06-09 16:49:31.871 [auxiliary-pool-1-thread-1] {} INFO  c.x.xlrelease.service.ReleaseService - Creating new release Applications/FolderSamplesAndTutorials/Releasedab943bc0fd944e2966d3cf67ba3c941 from template Applications/FolderSamplesAndTutorials/ReleaseTemplate_configure
2023-06-09 16:49:31.881 [main] {activemq.broker=embedded-broker} INFO  o.a.a.broker.TransportConnector - Connector vm://embedded-broker started
2023-06-09 16:49:31.899 [auxiliary-pool-1-thread-1] {activity=TEAM_DELETED, targetId=Applications/FolderSamplesAndTutorials/Team35617de4292e43dbbc45dae23f726c6f, targetType=xlrelease.Team, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Removed Team 'Folder Owner'
2023-06-09 16:49:31.901 [auxiliary-pool-1-thread-1] {activity=TEAM_DELETED, targetId=Applications/FolderSamplesAndTutorials/Teamd3a26f719a0c4874b6a7b0716173a2cc, targetType=xlrelease.Team, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Removed Team 'Ops'
2023-06-09 16:49:31.901 [auxiliary-pool-1-thread-1] {activity=TEAM_DELETED, targetId=Applications/FolderSamplesAndTutorials/Team434b89af7f4144159ed72ad3ab26f64b, targetType=xlrelease.Team, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Removed Team 'Dev'
2023-06-09 16:49:31.902 [auxiliary-pool-1-thread-1] {activity=TEAM_DELETED, targetId=Applications/FolderSamplesAndTutorials/Teambd4ef33749b144bd8b5d97e2c061f28d, targetType=xlrelease.Team, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Removed Team 'Template Owner'
2023-06-09 16:49:31.902 [auxiliary-pool-1-thread-1] {activity=TEAM_DELETED, targetId=Applications/FolderSamplesAndTutorials/Team70f61d0feb634b1a891cd956c80a4bd3, targetType=xlrelease.Team, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Removed Team 'Release mgmt.'
2023-06-09 16:49:31.903 [auxiliary-pool-1-thread-1] {activity=TEAM_DELETED, targetId=Applications/FolderSamplesAndTutorials/Teamc8a2caa58272403db869908fb20070b3, targetType=xlrelease.Team, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Removed Team 'Release Admin'
2023-06-09 16:49:31.903 [auxiliary-pool-1-thread-1] {activity=TEAM_DELETED, targetId=Applications/FolderSamplesAndTutorials/Team4ed88d8fc3954daf97c5819f63f6c3d7, targetType=xlrelease.Team, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Removed Team 'Release Managers'
2023-06-09 16:49:31.904 [auxiliary-pool-1-thread-1] {activity=TEAM_DELETED, targetId=Applications/FolderSamplesAndTutorials/Team7ab81c811b314dc48c9560581e21403f, targetType=xlrelease.Team, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Removed Team 'Release Manager'
2023-06-09 16:49:31.904 [auxiliary-pool-1-thread-1] {activity=TEAM_DELETED, targetId=Applications/FolderSamplesAndTutorials/Teamaff5a0889d2b422d9519fca636a94d09, targetType=xlrelease.Team, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Removed Team 'QA'
2023-06-09 16:49:31.908 [auxiliary-pool-1-thread-1] {activity=RELEASE_CREATED_FROM_TEMPLATE, targetId=Applications/FolderSamplesAndTutorials/Releasedab943bc0fd944e2966d3cf67ba3c941, targetType=xlrelease.Release, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Created Release 'Configure Release' from template 'Configure Release'
2023-06-09 16:49:31.972 [risk-calculation-pool-1-thread-1] {} INFO  c.x.x.risk.service.RiskService - Created risk score for release Applications/FolderSamplesAndTutorials/Releasedab943bc0fd944e2966d3cf67ba3c941: 0 and total risk score 0
2023-06-09 16:49:32.018 [XlReleaseSystem-15] {activity=RELEASE_STARTED, targetId=Applications/FolderSamplesAndTutorials/Releasedab943bc0fd944e2966d3cf67ba3c941, targetType=xlrelease.Release, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Started Release
2023-06-09 16:49:32.020 [XlReleaseSystem-15] {activity=PHASE_STARTED, targetId=Applications/FolderSamplesAndTutorials/Releasedab943bc0fd944e2966d3cf67ba3c941/Phase2437552, targetType=xlrelease.Phase, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Started Phase 'Setup mail server'
2023-06-09 16:49:32.023 [XlReleaseSystem-15] {activity=TASK_STARTED, targetId=Applications/FolderSamplesAndTutorials/Releasedab943bc0fd944e2966d3cf67ba3c941/Phase2437552/Task3066132, targetType=xlrelease.Task, username=SYSTEM} INFO  c.x.x.domain.ActivityLogEntry - Started Task 'Configure email address and mail server'
2023-06-09 16:49:32.091 [main] {activemq.broker=embedded-broker} INFO  c.x.xlrelease.XLReleaseBootstrapper$ - Started XLReleaseBootstrapper. in 29.144 seconds (JVM running for 68.029)
2023-06-09 16:49:32.106 [main] {activemq.broker=embedded-broker} INFO  c.x.x.service.PreArchiveService - Starting PreArchiveService
2023-06-09 16:49:32.114 [main] {activemq.broker=embedded-broker} INFO  c.x.p.m.r.n.NexusPluginRepository - Downloading metadata for plugin repository xlr-official from https://plugins.xebialabs.com/nexus
2023-06-09 16:49:35.433 [main] {activemq.broker=embedded-broker} INFO  c.x.x.s.jetty.JettyServerListener - XL Release has started.
2023-06-09 16:49:35.434 [main] {activemq.broker=embedded-broker} INFO  c.x.x.s.jetty.JettyServerListener - You can now point your browser to http://tektutor:5516/
</pre>

You also need a license to run the XLR CI/CD Pipeline server.


# Feedback and Post-test URL

Appreciate if you can complete the feedback survey at the below URL
<pre>
https://tcheck.co/2Hldv3
</pre>

DevOps - Post test link for DevOps training.
<pre>
https://app.mymapit.in/code4/tiny/dfahoe
</pre>
