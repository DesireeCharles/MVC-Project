# MVC-Project

LM173 Software Architecture Project - Desirèe Charles, Pardis Norouzi and Tamara Orosz. 

## Run the App and DB at once with Docker

create `*.jar` from the project 
```
mvn clean package -DskipTests
```
build a docker image from the App utilizing the `Dockerfile` file:
```
docker-compose build
```

Start application and DB together in 2 docker containers wired together, using the `docker-compose.yml` file:
```
docker-compose up
```
From a browser access the start page http://localhost:8080/

![Login & Register](/doc/login-register.png)
![Login](/doc/login.png)
![After Login](/doc/after-login.png)
![Grades Empty Form](/doc/grades-blank.png)
![Grades Filled Out](/doc/grades-filled.png)
![Register](/doc/register.png)

To stop the 2 containers and remove them:
```
docker-compose down
```
If you modify the source code of the App, you must start the whole process again.

## Or run only the DB in container
run MariaDB docker container, accessible on local port `3307`
```
docker run --detach -p 3307:3306 --name school-mariadb --env MARIADB_DATABASE=school --env MARIADB_USER=school --env MARIADB_PASSWORD=school --env MARIADB_ROOT_PASSWORD=root mariadb:latest
```
The above command:
- creates a MariaDB container from the image `mariadb:latest`
- creates DB schema `school`
- creates DB user `school` with password `school`
- sets the password `root` for DB user `root`
- makes the DB accessible from outside on port `3307`

## Useful Docker commands
- `docker images` - list all downloaded/generated images
- `docker rmi <containerId>` - remove (delete) image (the id is the first 3 characters)

- `docker run <containerId>` - run an image (becomes a container), there are many other argumenst necessary!
- `docker ps` - list running containers (add `-a` for including stopped containers as well)
- `docker stop <containerId>` - stop a running container
- `docker start <containerId>` - restart a stopped (existing) container

## Notes
The database tables get dropped at every start of the application, but retain their data after stopping the application. This means the database can be inspected after every application run.

An administrative user is created after the the app starts. It is created with the following credentials:
```
email:admin@school.ie
password:admin
```
This makes it possible to test the login without first registering a new user.

When you want to run the Integration Tests in Visual Studio Code, install the 'Rest Client' extension.