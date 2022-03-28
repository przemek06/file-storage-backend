# File Storage Backend

## Setup
You need the Tomcat server with some users defined in conf/tomcat-users.xml (with USER role) file to run this application. You also need to fill the information about 
MySql connection in project's persisence.xml file and change Constants.FILE_PATH constant to a desired location.
Use Maven in project's root directory to build the application:
```
mvn clean package
```
And then copy created .war file into Tomcat's webapps directory. Then use this command line command:
```
path_to_tomcat/bin/startup
```
Application's endpoint will be available at:
```
http://localhost:port/war_file_name/
```

## Technologies
* Servlets
* CDI
* JPA

## Features
* Users can login with username and password
* Authenticated users can upload files, which are then stored in filesystem.
* Users can list all private files owned by them and public files owned by anyone.
* Users can download all their private files and any public files. 
