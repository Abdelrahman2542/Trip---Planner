package tripplanner.model;

/**
 * Abstract base class representing a generic itinerary item.
 *
 * <p>Demonstrates <b>Encapsulation</b>: all fields are private and accessed
 * only through public getters/setters.</p>
 *
 * <p>Demonstrates <b>Polymorphism</b>: {@link #getDisplayInfo()} is abstract
 * and overridden by each subclass to return type-specific details.</p>
 */
public abstract class ItineraryItem {

    // --- ENCAPSULATION: private fields ---
    private String title;
    private String date;   // format: YYYY-MM-DD
    private double cost;

    /**
     * Constructs an ItineraryItem with the given common fields.
     *
     * @param title the display title of the item
     * @param date  the date in YYYY-MM-DD format
     * @param cost  the cost in USD
     */
    public ItineraryItem(String title, String date, double cost) {
        this.title = title;
        this.date  = date;
        this.cost  = cost;
    }

    // --- Getters & Setters ---

    /** @return the title of this itinerary item */
    public String getTitle() { return title; }

    /** @param title the new title */
    public void setTitle(String title) { this.title = title; }

    /** @return the date string (YYYY-MM-DD) */
    public String getDate() { return date; }

    /** @param date the new date string */
    public void setDate(String date) { this.date = date; }

    /** @return the cost in USD */
    public double getCost() { return cost; }

    /** @param cost the new cost value */
    public void setCost(double cost) { this.cost = cost; }

    /**
     * Returns a human-readable summary of this item.
     *
     * <p>Each subclass must override this method to include its own
     * type-specific fields (POLYMORPHISM).</p>
     *
     * @return formatted display string
     */
    public abstract String getDisplayInfo();

    /**
     * Returns the type name of this item (e.g. "Flight", "Hotel", "Activity").
     * Used when serialising to CSV / JSON.
     *
     * @return type label string
     */
    public abstract String getType();
}
