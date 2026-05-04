package tripplanner.model;

/**
 * Represents a planned activity in the trip itinerary.
 *
 * <p>Demonstrates <b>Inheritance</b>: Activity extends {@link ItineraryItem},
 * adding a location field for the activity venue or destination.</p>
 */
public class Activity extends ItineraryItem {

    private String location;

    /**
     * Constructs an Activity item.
     *
     * @param title    the activity name (e.g. "Pyramids Tour")
     * @param date     the activity date (YYYY-MM-DD)
     * @param cost     the activity cost in USD
     * @param location the venue or location name
     */
    public Activity(String title, String date, double cost, String location) {
        super(title, date, cost);
        this.location = location;
    }

    // --- Getters & Setters ---

    /** @return the activity location */
    public String getLocation() { return location; }

    /** @param location the new location */
    public void setLocation(String location) { this.location = location; }

    /**
     * {@inheritDoc}
     *
     * <p>POLYMORPHISM: provides activity-specific display information.</p>
     */
    @Override
    public String getDisplayInfo() {
        return String.format("🎯 Activity: %s (%s) | Location: %s | Cost: $%.2f",
                getTitle(), getDate(), location, getCost());
    }

    /** {@inheritDoc} */
    @Override
    public String getType() { return "Activity"; }
}
