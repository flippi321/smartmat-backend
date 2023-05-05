# SmartMat

## Description

The purpose of the project is to make an application that makes it easier for people to throw less food away. A core functionality in the application, is generating recipes based on the grocery items a user has in his fridge, and their expiration dates. This will allow the user to eat the food he already has, and the food that will soon expire.

Food production consumes energy and releases CO2 into the atmosphere. If people use less food, there will be less pollution. Also, the app-user will save money from the food they don’t need to buy. 


## Technologies
- The Vue.js Framework 
- MySQL - PHPMyAdmin
- SpringBoot 
- H2 Database Engine - In-memory database for testing
- JWT - Authentication for REST APIs
- Swagger - API documentation
- Docker


## How to run and install the project backend
### The technologies needed to run the program are:
- JDK(java development kit) 17 - can be installed [here](https://www.oracle.com/java/technologies/downloads/#java17)
- Maven(building tool) - can be installed [here](https://maven.apache.org/download.cgi)
- Docker - can be installed [here](https://www.docker.com/products/docker-desktop/)

### Clone the project by running this command in the terminal
```sh
git clone git@gitlab.stud.idi.ntnu.no:idatt2106-v21-09/backend.git
```

### Build the project
```sh
mvn clean install
```

### Build the Docker Image
```sh
docker-compose up
```

### To stop the Docker Image
```sh
docker-compose down
```






## How to run and install the project frontend
### The technologies needed to run the program are:

- Node.js - can be installed [here](https://nodejs.org/en/download)

### To be certain you have Node.js installed, run this command:
```sh
- node -v
```


### Do the same for NPM:
```sh
- npm -v
```

### Use this command to install Vue:

```sh
- npm install vue
```

### After installing Vue, the frontend project can be cloned. Run this command in the terminal:

```sh
git clone git@gitlab.stud.idi.ntnu.no:idatt2106-v21-09/frontend.git
```

### To download the necessary dependencies, run this command while inside the project:


```sh
npm install
```

### To start the application, type this command:

```sh
npm run dev
```

## Navigating the application
The first thing the user sees when he starts the application, is the welcome page. He can then choose to either log in, if the user already exists in the database, or register. When he registers, he can either join an existing household (if he has the invitation code), or create a new one. Either way, new users can join his household later if he chooses to invite them.

When the user is “in” his household, he can begin to administer his fridge and shoppinglist. He can also choose to have recipes generated for him based on the grocery items in his fridge. He can update his personal information, and general information regarding the household. 

Images for groceryitems and groceries makes it easier for the user to navigate the application. 

## Using Swagger UI for API Documentation

This project uses Swagger UI API documentation to access and use Swagger UI, follow these steps:

1. **Access Swagger UI:** Once the backend is up and running, open your web browser and navigate to the following URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

3. **Explore API endpoints:** Swagger UI presents a list of available API endpoints, organized by tags. Click on a tag to expand it and view the related API operations. For each operation, you will see the HTTP method, endpoint URL, a brief description, and the request/response details.


## Test users
The database already contains some test users 

Users in the Smith household (invitation_nr: 354674):
- willsmith@gmail.com
- johnsmith@gmail.com
- janesmith@gmail.com

Users in the Johnson household (invitation_nr: 847382)
- robertjohnson@gmail.com
- emilyjohnson@gmail.com

All users have password: passord123

## Tests

The backend-project has tests that can be run in the terminal with this command:

```sh
mvn clean test
```


The frontend-project has tests that can be run in the terminal with this command:
```sh
npm run test:unit
```


## CI

Both frontend and backend has a ".gitlab-ci.yml" file.. This file controls Continous Integration for the projects. It manages the pipeline which holds the jobs for GitLab to run.

The backend pipeline has these stages:
- build
- test


The frontend pipeline has these stages:
- build
- test


## Team Members
- Bjørn-Johnny Bendixen
- Håkon Hansen Bergrem
- Teodor Birkeland
- Christoffer Brevik
- Arnold Sverre Hansen
- Hadar Hayat
