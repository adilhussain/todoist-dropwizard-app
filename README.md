# Todoist Demo Dropwizard Application

How to start the application 
---

This is an example that exposes a REST interface for performing CRUD operations on mongo db Collections Todo and Tasks

## Technologies

- Dropwizard v. 1.3
- MongoDB Java Driver 3.8
- Swagger 1.0.6
- Docker

## Development

Please Make sure you have maven installed in your system.

1. Run `mvn clean install` to build your application which will create a tar file in the `target` directory
2. Run `docker-compose up` for starting the containers
3. Create the users for database with the following commands:
    - Enter into the running mongo db container:
        `docker exec -it [container_name or container_id] /bin/bash`
        your container_name will be mongodb
    - Get into the mongo console
        `mongo -u admin -p admin`
    - Create the database
        `use todos`
    - Create the user and give it permission to read and Write to `todos` database.    
    - `db.createUser({ user: "user_todos", pwd: "pAsw0Rd123", roles: [ { role: "readWrite", db: "todos"} ]});`      

Now your are ready to go


Swagger
---
To check that your application is running enter url `http://localhost:8080/todoist-demo-dropwizard/swagger`   

You can test the api's directly from the swagger interface

Browser
---
To check the api on browser enter url `http://localhost:8080/todoist-demo-dropwizard/todos`

Health Check
---

To see your applications health enter url `http://localhost:8091/healthcheck`
