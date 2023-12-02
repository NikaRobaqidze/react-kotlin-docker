# About

The project involves the collaboration of two server applications: a React server designed for the client side (front-end) and a Kotlin server built on the Ktor framework (back-end) responsible for receiving and processing REST API requests from the React server. The primary scenario involves users entering a domain to initiate domain research.

In this scenario, users input a domain for research initiation. As a result, the client receives information on discovered sub-domains and any additional details if the domain has been previously researched. All successfully completed searches are stored, allowing clients to access and review them on the history page.

## React

The React server is responsible for rendering two pages: Home and History, utilizing routing for seamless navigation. The server specifically uses port 8000 (localhost:3000) The Home page features an input field where users enter a domain. The entered string content is then sent to the backend using the Fetch API. Before initiating the fetch, the server performs checks to ensure the user has filled in the domain field and that the entered value is a valid domain, employing Regular Expressions for validation.

To enhance user-friendliness, the Bootstrap framework is employed, providing visual cues to users in case of errors, such as input validation issues or thrown errors during the process. Upon a successful response from the server, an object containing information about sub-domains and execution time is retrieved. This information is then presented to the user for a comprehensive overview of the domain research results.

## Kotlin

The Kotlin server utilizes the Ktor framework to establish an HTTP server on localhost, specifically using port 8000 (localhost:8000). Initially, when the server is launched, it begins the process of building necessary Docker images. Once the building is successfully completed, the server will be started. The server is designed to handle two distinct GET methods: 1) `/domain-data?domain=[your domain]` and 2) `/history`.

1) Upon receiving a request for the first method and until the process is completed, the server systematically closes all new HTTP request connections. This closing of connections occurs after the request has been validated. During validation, the server checks for the required query key, specifically the 'domain'. Subsequently, it validates the domain using Regular Expressions and confirms its existence on the World Wide Web. The request is considered validated only if these conditions are met.

Upon successful validation, the server constructs a script command (string) to execute theHarvester module within a Docker container. The developed Docker management system provides the capability to check and manage containers by their ID, enabling the use of only one container for efficient performance optimization. It is responsible for two main functions: 

1) Execute the theHarvester project within a Docker container and provide the necessary parameters to it. 
2) One for copying and the other for removing the generated file from the Docker container. 

The copied file is stored in the Kotlin project's "data" folder, and the server reads string data from it.

Before responding to the user, all data, along with the execution time, is inserted into the MySQL Database to preserve it for historical purposes. Finally, the server builds a JSON string response, which is delivered to the user. It needs to log this process to be able to read it in the future.

2) To get logs of research, the server connects to a database and builds JSON strings to receive users.

## Notice
If while kotlin server working you get an error with the database connection, please, re-install the .jar file. In my case in IntelliJ -> File -> Project Structure -> Project Settings -> Libraries -> select "mysql-connector-j-8.2.0" and click on "-" in same column -> click on "+" near -> select java -> `/kotlin-server/kotlin-server/lib/mysql-connector-j-8.2.0.jar` -> OK -> Apply.

# Usage
 - npm ([Node.js](https://nodejs.org/en)) (version ^10.2.3);
 - [Kotlin](https://kotlinlang.org/) (version ^1.9.255-SNAPSHOT (JRE ^17.0.9+8)), using JetBrains - IntelliJ;
 - MySQL, using [XAMPP](https://www.apachefriends.org/);
 - [docker](https://www.docker.com/) (version 24.0.7, build afdd53b4e3);
    [docker-desktop](https://www.docker.com/products/docker-desktop/)
 - [theHarvester](https://github.com/laramies/theHarvester) cloned;

# Installation

## Notice
The project was developed on the Manjaro Linux OS. The suggestions might not be useful for users using a different operating system.

## Docker

 - Launch `docker-dekstop`. Sometimes, without it, Docker methods do not work well.

```bash
systemctl --user start docker-desktop
```
 - To handle the created Docker container ID to work with theHarvester in the Kotlin server in two ways:

1) Enter the container ID and paste it into a file:
`/kotlin-server/kotlin-server/src/main/kotlin/Main.kt`

or

 - Set it as an argument in the project running command. In my case: InteliJ -> Run/Debug Configurations -> Program arguments.

## MySQL - Database

 - Create a user and set username and password copied from the environment file:
`/kotlin-server/kotlin-server/.env`;

 - Create database `domain_researches`;

 - Import/Create a table using .sql file:
`/Database/domain_researches.sql`

# Run servers

## Koltin (ktor)
 - Run InteliJ (in my case) File -> Open -> "/kotlin-server/kotlin-server";
 - Run project (Launcher - Main.kt);
 - It starts building the required Docker image(s); 
 - Now server listening on `localhost:8080`;

## React
 - Change directory to `/react-server` folder;
 - To launch the React server
```bash
serve -s build
```
 - Now server listening on `localhost:3000`;

------------------------------ The end. -------------------------------
Project is ready for testing!

Author: Niko Robaqidze
Thanks