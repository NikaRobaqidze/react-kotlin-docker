package utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File
import java.net.Socket
import java.net.UnknownHostException
import java.util.UUID

import kotlin.io.path.Path

/**
 * Class responsible for handling domain research requests.
 */
class DomainResearcher {

    /**
     * Handle the home endpoint for domain research.
     *
     * @param call ApplicationCall representing the HTTP call.
     * @param isBusy BusyManager to check and set the busy status.
     * @param dockerManager Docker manager for working with theHarvester.
     */
    suspend fun handleHome(call: ApplicationCall, isBusy: BusyManager, dockerManager: DockerManager) {

        // Record the start time for measuring execution time
        val startTimer = System.currentTimeMillis()

        // Get the domain parameter from the request
        val domain = call.request.queryParameters["domain"]

        // Check if the domain parameter is missing or invalid
        if (domain == null || !isValidDomain(domain)) {

            call.response.status(HttpStatusCode.BadRequest)
            call.respondText("Invalid domain", ContentType.Text.Html)
            return
        }

        // Check if the domain exists
        if(!checkDomainExists(domain)){

            call.response.status(HttpStatusCode.BadRequest)
            call.respondText("Domain not exist", ContentType.Text.Html)
            return
        }

        // Check if the system is currently busy, close the connection if it is
        if (isBusy.checkIsBusy()) {

            return withContext(Dispatchers.IO) {
                Socket().close()
            }

        } else {

            // Set the system as busy
            isBusy.setBusyStatus(true)

            println("Start working in docker")

            // Set the Docker image name to "theharvester"
            dockerManager.setImageName("theharvester")

            // Check if an active Docker container exists by ID
            if (!dockerManager.isActiveContainerExists()) {

                // If no active container exists, run a new Docker container and set its ID
                dockerManager.setContainerID(dockerManager.runImage())
            }

            // Generate a random UUID for the research session
            val myUuid = UUID.randomUUID()
            val researchID = "$myUuid-$domain"
            val fileName = "$researchID.json"

            // Execute theHarvester command in the Docker container
            val renderDataCommand = "theHarvester -d $domain -l 500 -b duckduckgo -f ./$fileName"
            dockerManager.executeCommandInContainer(renderDataCommand)

            // Define the path to store the research data
            val path = Path(System.getProperty("user.dir"))
                .resolve("src")
                .resolve("main")
                .resolve("kotlin")
                .resolve("data")
                .resolve(fileName)

            // Copy the research data from the Docker container to the local path
            dockerManager.copyFileFromContainer("/app/$fileName", path.toString())

            // Remove the temporary file from the Docker container
            val removeCommand = "find /app/ -type f -name '$researchID*' -delete"
            dockerManager.executeCommandInContainer(removeCommand)

            println("End working in docker")

            // Read the research data from the local file
            val domainData: List<String> = File(path.toString()).useLines { it.toList() }

            // Establish a database connection
            DatabaseManager.connection()

            // Record the stop time for measuring execution time
            val stopTimer = System.currentTimeMillis()
            val milsSecLeft = stopTimer - startTimer

            // Insert the research history into the database
            DatabaseManager.insertHistory(
                researchID, domain, domainData.toString(), milsSecLeft
            )

            // Set the system as not busy
            isBusy.setBusyStatus(false)

            // Prepare the response JSON
            val response = "{\"executeTime\": $milsSecLeft, \"items\": ${domainData}}"
            call.respond(response)
        }
    }

    /**
     * Check if a domain is valid based on a regular expression.
     *
     * @param domain The domain to validate.
     * @return True if the domain is valid, false otherwise.
     */
    private fun isValidDomain(domain: String?): Boolean {

        val regex = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$"
        return domain != null && Regex(regex).containsMatchIn(domain.toString())
    }

    /**
     * Check if a domain exists by trying to resolve its IP address.
     *
     * @param domain The domain to check.
     * @return True if the domain exists, false otherwise.
     */
    private fun checkDomainExists(domain: String): Boolean {

        return try {

            val ipAddresses = java.net.InetAddress.getAllByName(domain)
            ipAddresses.isNotEmpty()

        } catch (e: UnknownHostException) {

            e.printStackTrace()
            false
        }
    }
}
