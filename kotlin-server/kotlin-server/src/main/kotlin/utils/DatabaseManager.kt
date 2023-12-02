package utils

import io.github.cdimascio.dotenv.Dotenv
import java.sql.DriverManager
import java.sql.Connection
import java.sql.SQLException

import io.github.cdimascio.dotenv.dotenv
import java.sql.ResultSet
import java.util.StringJoiner

/**
 * Data class representing history information.
 *
 * @property id Unique identifier for the history entry.
 * @property domain Domain associated with the history entry.
 * @property details Details of the history entry.
 * @property executeTime Execution time in milliseconds.
 * @property date Date of the history entry.
 */
class History(
    private val id: String,
    private val domain: String,
    private val details: String,
    private val executeTime: Long,
    private val date: String
) {

    /**
     * Convert the history object to a JSON-like string representation.
     *
     * @return String representation of the history object.
     */
    override fun toString(): String {

        val stringJoiner = StringJoiner(",", "{", "}")

        stringJoiner.add("\"id\": \"$id\"")
        stringJoiner.add("\"domain\": \"$domain\"")
        stringJoiner.add("\"details\": \"${details.replace("\"", "\\\"")}\"")
        stringJoiner.add("\"executeTime\": $executeTime")
        stringJoiner.add("\"date\": \"${date}\"")

        return stringJoiner.toString()
    }
}

/**
 * Object responsible for managing the database connection and history operations.
 */
object DatabaseManager {

    // Database connection and Dotenv instance
    private lateinit var connectionGB: Connection
    private var dotEnvGB: Dotenv = dotenv()

    /**
     * Establish a connection to the database.
     */
    @JvmStatic
    fun connection() {

        try {

            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver")

            // Establish the database connection
            val connection = DriverManager.getConnection(
                "jdbc:mysql://localhost",
                this.dotEnvGB["DB_USERNAME"],
                this.dotEnvGB["DB_PASSWORD"]
            )

            this.connectionGB = connection

        } catch (e: SQLException) {

            // Handle database connection exception
            e.printStackTrace()
        }
    }

    /**
     * Insert a new history entry into the database.
     *
     * @param id Unique identifier for the history entry.
     * @param domain Domain associated with the history entry.
     * @param details Details of the history entry.
     * @param executeTime Execution time in milliseconds.
     */
    fun insertHistory(id: String, domain: String, details: String, executeTime: Long) {

        val dbName = this.dotEnvGB["DB_NAME"]
        val queryStr = "INSERT INTO $dbName.history (history_id, domain, details, execute_time_mils) VALUES (?, ?, ?, ?);"

        val preparedStatement = this.connectionGB.prepareStatement(queryStr)

        // Set values for the prepared statement
        preparedStatement.setString(1, id)
        preparedStatement.setString(2, domain)
        preparedStatement.setString(3, details)
        preparedStatement.setLong(4, executeTime)

        // Execute the insert query
        preparedStatement.execute()
        preparedStatement.close()

        println("History updated.")
    }

    /**
     * Retrieve a list of history entries from the database.
     *
     * @return List of history entries.
     */
    fun getHistory(): List<History> {
        val statement = this.connectionGB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_INSENSITIVE)

        val dbName = this.dotEnvGB["DB_NAME"]
        val queryStr = "SELECT * FROM $dbName.history ORDER BY date DESC;"

        val historyResultSet = statement.executeQuery(queryStr)

        // Use the ResultSet to generate a list of History objects
        val historyList = historyResultSet.use {

            generateSequence {

                if (!historyResultSet.next()) null

                else {

                    History(
                        historyResultSet.getString(1),
                        historyResultSet.getString(2),
                        historyResultSet.getString(3),
                        historyResultSet.getLong(4),
                        historyResultSet.getString(5)
                    )
                }

            }.toList()
        }

        return historyList
    }
}