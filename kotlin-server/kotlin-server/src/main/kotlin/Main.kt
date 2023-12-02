import utils.BusyManager

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.apache.log4j.BasicConfigurator
import utils.DatabaseManager
import utils.DockerManager
import utils.DomainResearcher
import java.lang.RuntimeException
import kotlin.io.path.Path

/**
 * Main function to start the Ktor server.
 *
 * @param args Command-line arguments.
 * Can be provided docker container ID.
 */
fun main(args: Array<String>) {

    // Configure basic logging for the application
    BasicConfigurator.configure()

    // Set a default Docker container ID for testing purposes
    val handleDockerID = "000" // Enter docker container here
    // Use the provided Docker container ID from command-line arguments, if any
    val containerID = if (args.isEmpty() || args[0].isBlank()) handleDockerID else args[0]

    //# --------------------- Check all required Docker images --------------------- #

    // Create a DockerManager instance for handling Docker operations
    val dockerManager = DockerManager("theharvester", containerID)

    // Check if the required Docker image exists; if not, build a new one
    if (!dockerManager.isImageExists()) {

        // Define the path to the Docker image
        val imagePath = Path(System.getProperty("user.dir"))
            .resolve("..")
            .resolve("..")
            .resolve("docker")
            .resolve("theHarvester")

        // Build a new Docker image
        dockerManager.buildNewImage(imagePath)
        // Run the Docker image and set the container ID
        dockerManager.setContainerID(dockerManager.runImage())
    }

    //# ---------------------------------------------------------------------------- #

    // Throw an exception if the Docker container ID is not provided
    if (containerID.isBlank()) throw RuntimeException("Docker container has not been selected.")

    // Initialize BusyManager for handling busy status
    val busyManager = BusyManager()

    // Start the Ktor server using Netty engine on port 8080
    embeddedServer(Netty, 8080) {

        // Install CORS plugin to handle Cross-Origin Resource Sharing
        install(CORS) {

            // Allow requests from the specified host and header
            allowHost("localhost:3000")
            allowHeader(HttpHeaders.ContentType)
        }

        // Define routing for different API endpoints
        routing {

            // Handle GET request for "/domain-data" endpoint
            get("/domain-data") {

                // Call DomainResearcher to handle the request and respond
                DomainResearcher().handleHome(call, busyManager, dockerManager)
            }

            // Handle GET request for "/history" endpoint
            get("/history") {

                // Connect to the database and respond with the history data
                DatabaseManager.connection()
                call.respond(DatabaseManager.getHistory().toString())
            }
        }

    }.start(wait = true) // Start the server and wait for it to finish
}
