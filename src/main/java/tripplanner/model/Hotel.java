package tripplanner.model;

/**
 * Represents a hotel booking in the trip itinerary.
 *
 * <p>Demonstrates <b>Inheritance</b>: Hotel extends {@link ItineraryItem},
 * adding city and number-of-nights fields specific to hotel stays.</p>
 */
public class Hotel extends ItineraryItem {

    private String city;
    private int    numberOfNights;

    /**
     * Constructs a Hotel item.
     *
     * @param title          the hotel name or booking title
     * @param date           the check-in date (YYYY-MM-DD)
     * @param cost           the total accommodation cost in USD
     * @param city           the city where the hotel is located
     * @param numberOfNights the number of nights booked
     */
    public Hotel(String title, String date, double cost,
                 String city, int numberOfNights) {
        super(title, date, cost);
        this.city            = city;
        this.numberOfNights  = numberOfNights;
    }

    // --- Getters & Setters ---

    /** @return the city name */
    public String getCity() { return city; }

    /** @param city the new city name */
    public void setCity(String city) { this.city = city; }

    /** @return the number of nights */
    public int getNumberOfNights() { return numberOfNights; }

    /** @param numberOfNights the new number of nights */
    public void setNumberOfNights(int numberOfNights) { this.numberOfNights = numberOfNights; }

    /**
     * {@inheritDoc}
     *
     * <p>POLYMORPHISM: provides hotel-specific display information.</p>
     */
    @Override
    public String getDisplayInfo() {
        return String.format("🏨 Hotel: %s (%s) | City: %s | Nights: %d | Cost: $%.2f",
                getTitle(), getDate(), city, numberOfNights, getCost());
    }

    /** {@inheritDoc} */
    @Override
    public String getType() { return "Hotel"; }
}
