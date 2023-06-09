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
,
You also need a license to run the XLR CI/CD Pipeline server.
