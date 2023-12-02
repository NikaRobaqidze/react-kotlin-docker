package utils

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Manages Docker operations such as image creation, container management via command execution.
 *
 * @property imageName Name of the Docker image.
 * @property containerID ID of the Docker container.
 */
class DockerManager(imgName: String, containerId: String = "") {

    private var imageName: String
    private lateinit var containerID: String

    init {

        // Validate and initialize Docker image name
        if (imgName.isBlank()) {

            throw IllegalArgumentException("Docker image name is required.")
        }

        imageName = imgName.trim()

        // Initialize Docker container ID if provided
        if (containerId.isNotBlank()) {

            containerID = containerId.trim()
        }
    }

    /**
     * Sets the Docker container ID.
     *
     * @param containerID The new Docker container ID.
     */
    fun setContainerID(containerID: String) {

        this.containerID = containerID.trim()
    }

    /**
     * Sets the Docker image name.
     *
     * @param imageName The new Docker image name.
     */
    fun setImageName(imageName: String) {

        this.imageName = imageName.trim()
    }

    /**
     * Executes a command in the system's shell.
     *
     * @param command The command to execute.
     * @return The Process object representing the command execution.
     */
    private fun execute(command: String): Process {

        return Runtime.getRuntime().exec(command)
    }

    /**
     * Executes a command in the system's shell and retrieves the result as a string.
     *
     * @param command The command to execute.
     * @return The result of the command execution as a string.
     */
    private fun executeAndGetResult(command: String): String {

        val executedCommand = execute(command)
        val executionResult = String(executedCommand.inputStream.readBytes(), UTF_8)

        if (executedCommand.waitFor() == 1) {

            val errTxt = String(executedCommand.errorStream.readBytes(), UTF_8)
            System.err.println("Error with docker: $errTxt")
        }

        return executionResult
    }

    /**
     * Checks if the Docker image exists locally.
     *
     * @return True if the Docker image exists; false otherwise.
     */
    fun isImageExists(): Boolean {

        val executeResult = executeAndGetResult("docker images -aq ${this.imageName}")
        return executeResult.isNotBlank()
    }

    /**
     * Checks if the specified Docker container is active.
     *
     * @return True if the Docker container is active; false otherwise.
     */
    fun isActiveContainerExists(): Boolean {

        if (this.containerID == null || this.containerID.isBlank()) {

            return false
        }

        val executeResult = executeAndGetResult("docker ps -aqf id=${this.containerID}")
        return executeResult.isNotBlank()
    }

    /**
     * Checks if the specified Docker container is active.
     *
     * @param containerID The ID of the Docker container to check.
     * @return True if the Docker container is active; false otherwise.
     */
    fun isActiveContainerExists(containerID: String): Boolean {

        if (containerID.isBlank()) {

            return false
        }

        val executeResult = executeAndGetResult("docker ps -aqf id=${this.containerID}")
        return executeResult.isNotBlank()
    }

    /**
     * Builds a new Docker image from the specified directory path.
     *
     * @param imagePath The path to the directory containing the Docker image files.
     */
    fun buildNewImage(imagePath: Path) {

        // Validate image directory existence
        if (!Files.isDirectory(imagePath)) {

            throw IllegalArgumentException("Image directory not exists: $imagePath")
        }

        // Build the Docker image
        val commandTxt = "docker build -t ${this.imageName} $imagePath"
        val executeResult = execute(commandTxt)

        if (executeResult.waitFor() == 1) {

            val errTxt = String(executeResult.errorStream.readBytes(), UTF_8)
            System.err.println("Error docker image has not built: $errTxt")

        } else {

            println("Docker image [$imageName] successfully created.")
        }
    }

    /**
     * Runs the Docker image as a detached container and returns the container ID.
     *
     * @return The ID of the running Docker container.
     */
    fun runImage(): String {

        // Check if the Docker image exists
        if (!isImageExists()) {

            throw RuntimeException("Docker image not exists.")
        }

        // Run the Docker image
        val commandTxt = "docker run --detach ${this.imageName}"
        val executeResult = execute(commandTxt)

        val dockerContainerID = String(executeResult.inputStream.readBytes(), UTF_8)

        if (executeResult.waitFor() == 1) {

            val errTxt = String(executeResult.errorStream.readBytes(), UTF_8)
            System.err.println("Error docker image has not run: $errTxt")
        }

        return dockerContainerID
    }

    /**
     * Executes a command within the active Docker container.
     *
     * @param command4Container The command to execute within the Docker container.
     * @return The result of the command execution within the Docker container.
     */
    fun executeCommandInContainer(command4Container: String): String {

        // Check if the Docker container exists
        if (!this.isActiveContainerExists()) {

            throw RuntimeException("Docker container not exists.")
        }

        // Execute the command within the Docker container
        val commandTxt = "docker exec -i ${this.containerID} $command4Container"

        val executeResult = execute(commandTxt)
        val executeResultTxt = String(executeResult.inputStream.readBytes(), UTF_8)

        if (executeResult.waitFor() == 1) {

            val errTxt = String(executeResult.errorStream.readBytes(), UTF_8)
            System.err.println("Failure result: $errTxt")

        } else {

            println("Command successfully executed.")
        }

        return executeResultTxt
    }

    /**
     * Copies a file from the Docker container to the local file system.
     *
     * @param virtualPath The virtual path of the file within the Docker container.
     * @param localPath The local path to copy the file to.
     */
    fun copyFileFromContainer(virtualPath: String, localPath: String) {

        // Check if the Docker container exists
        if (!this.isActiveContainerExists()) {

            throw RuntimeException("Docker container not exists.")
        }

        println("Copying file from docker container command:")

        // Copy the file from the Docker container to the local file system
        val commandTxt = "docker cp $containerID:$virtualPath $localPath"
        val executeResult = execute(commandTxt)

        if (executeResult.waitFor() == 1) {

            val errTxt = String(executeResult.errorStream.readBytes(), UTF_8)
            System.err.println("Error at copying: $errTxt")

        } else {

            println("File created: ${Path(localPath).fileName}")
            println("File successfully copied.")
        }
    }

    /**
     * Returns a string representation of the DockerManager.
     *
     * @return A string representation of the DockerManager.
     */
    override fun toString(): String {

        return "DockerManager(imageName='$imageName', containerID='$containerID')"
    }
}
