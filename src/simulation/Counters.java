package simulation;

/**
 * This class handles the synchronized counters
 * for the simulation variables
 * Handles days, sick agents, dead agents,
 * immune agents and vulnerable agents
 */
public class Counters {
    private static int sickCounter = 0;
    private static int deadCounter = 0;
    private static int vulnerableCounter = 0;
    private static int immuneCounter = 0;

    private static int days = 0;

    /**
     * increment sick counter
     */
    public static synchronized void incrementSick () {
        sickCounter++;
    }

    /**
     * Decrement sick counter
     */
    public static synchronized void decrementSick () {
        sickCounter--;
    }

    /**
     * Increment dead counter
     */
    public static synchronized void incrementDead () {
        deadCounter++;
    }

    /**
     * Decrement vulnerable counter
     */
    public static synchronized void decrementVulnerable () {
        vulnerableCounter--;
    }

    /**
     * Increment immune counter
     */
    public static synchronized void incrementImmune () {
        immuneCounter++;
    }

    /**
     * Increment days counter
     */
    public static synchronized void incrementDays () {
        days++;
    }

    /**
     * Return the sick counter
     * @return sickCounter
     */
    public static int getSickCounter() {
        return sickCounter;
    }

    /**
     * Return the dead counter
     * @return deadCounter
     */
    public static int getDeadCounter() {
        return deadCounter;
    }

    /**
     * Return the vulnerable counter
     * @return vulnerableCounter
     */
    public static int getVulnerableCounter() {
        return vulnerableCounter;
    }

    /**
     * Return the immune counter
     * @return immuneCounter
     */
    public static int getImmuneCounter() {
        return immuneCounter;
    }

    /**
     * Return the days counter
     * @return days
     */
    public static int getDays() {
        return days;
    }

    /**
     * Set the vulnerable counter
     * @param n the value to set
     */
    public static synchronized void setVulnerableCounter(int n) {
        vulnerableCounter=n;
    }

    /**
     * Set the dead counter
     * @param n the value to set
     */
    public static synchronized void setDeadCounter(int n) {
        deadCounter=n;
    }

    /**
     * Set the immune counter
     * @param n the value to set
     */
    public static synchronized void setImmuneCounter(int n) {
        immuneCounter=n;
    }

    /**
     * Set the sick counter
     * @param n the value to set
     */
    public static synchronized void setSickCounter(int n) {
        sickCounter=n;
    }

    /**
     * Set the days counter
     * @param n the value to set
     */
    public static synchronized void setDays(int n) {
        days=n;
    }
}
