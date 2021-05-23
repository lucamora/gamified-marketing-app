# Gamified Marketing Application

Optional project for the DB2 course 2020/21.

This project is based on JEE framework and uses JPA APIs 
to manage the persistence and the mapping with the relational database (MySQL).

The webapps are built using servlets that run inside TomEE application server.
The user interface is rendered using Thymeleaf template engine.

## DB schema
![DB schema](db/Gamified%20Marketing%20Application.png)

## Modules
### gma-admin
Admin web application that allows administrators to create products and manage questionnaires of the day.

### gma-web
Web application that is used by the users to login and compile questionnaire of the day.

### gma-ejb
Business services that implements the backend logic of both the web applications.
This module is imported and used in both the frontend modules.

## Technologies
* TomEE: application server
* JPA: object-relational mapping
* EclipseLink: persistence provider
* MySQL: relational DBMS
* Thymeleaf: server-side Java template engine