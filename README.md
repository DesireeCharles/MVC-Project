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

Start application and DB together in 2 docker container wired together, using the `docker-compose.yml` file:
```
docker-compose up
```
From a browser access the start page http://localhost:8080/

![Login & Register](/doc/login-register.png)
![Login](/doc/login.png)
![Register](/doc/register.png)

stop the 2 containers and remove them:
```
docker-compose down
```
if you modify the source code of the App, start again from the top

## Or run only the DB in container
run MariaDB docker container, accessible on local port `3307`
```
docker run --detach -p 3307:3306 --name school-mariadb --env MARIADB_DATABASE=school --env MARIADB_USER=school --env MARIADB_PASSWORD=school --env MARIADB_ROOT_PASSWORD=root mariadb:latest
```
The above command
- creates a MariaDB container from image `mariadb:latest`
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
The database tables get dropped at each application start, but preserve their content after stopping the app. So the db content can be inspected after run.

An administrative user gets created after the the app starts with the following credentials:
```
username:admin@school.ie
password:admin
```
This makes it possible to test the login without first registering a new user.