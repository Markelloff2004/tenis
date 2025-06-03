# Ping Pong Tournament

This project can be used as a starting point to create your own Vaadin application with Spring Boot.
It contains all the necessary configuration and some placeholder files to get you started.

### Run from IDE

1. Make sure the **MySQL** (or other) database container is running correctly.
2. Verify that the database connection URL is correct in the `application.properties` file:
3. Open the project in your IDE (e.g., IntelliJ IDEA).
4. Locate the `Application` class (in `src/main/java`).
5. Run the `main` method in that class.


## Deploying using Docker

To build the Dockerized version of the project, you will need:
1. Make sure the database connection URL in `application.properties` is correctly set to connect to the containerized database.
2. Build the application and start the containers using:
    ```bash
      mvn clean package -Pproduction
      docker-compose up --build -d
     ```
3. The application will be available at: [http://localhost:8090](http://localhost:8090)
4. To stop and remove all containers, volumes, and networks created by Docker Compose:

   ```bash
   docker-compose down -v
   ```


## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `src/main/frontend` contains the client-side JavaScript views of your application.
- `themes` folder in `src/main/frontend` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorial at [vaadin.com/docs/latest/tutorial/overview](https://vaadin.com/docs/latest/tutorial/overview).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/docs/latest/components](https://vaadin.com/docs/latest/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Build any UI without custom CSS by discovering Vaadin's set of [CSS utility classes](https://vaadin.com/docs/styling/lumo/utility-classes). 
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Forum](https://vaadin.com/forum).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).
