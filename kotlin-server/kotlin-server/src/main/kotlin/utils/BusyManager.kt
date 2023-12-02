package utils

/**
 * Utility class to manage the busy status of a resource.
 */
class BusyManager {

    // Flag to represent the busy status
    private var isBusy: Boolean = false

    /**
     * Check the current busy status.
     *
     * @return True if the resource is busy, false otherwise.
     */
    fun checkIsBusy(): Boolean {
        return this.isBusy
    }

    /**
     * Set the busy status of the resource.
     *
     * @param isNowBusy True to set the resource as busy, false to set it as not busy.
     */
    fun setBusyStatus(isNowBusy: Boolean) {
        this.isBusy = isNowBusy
    }
}