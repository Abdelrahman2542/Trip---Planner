package tripplanner.model;

/**
 * Represents a flight booking in the trip itinerary.
 *
 * <p>Demonstrates <b>Inheritance</b>: Flight extends {@link ItineraryItem},
 * gaining its encapsulated common fields while adding airline-specific ones.</p>
 */
public class Flight extends ItineraryItem {

    private String airline;
    private String flightNumber;

    /**
     * Constructs a Flight item.
     *
     * @param title        the booking title or route description
     * @param date         the departure date (YYYY-MM-DD)
     * @param cost         the ticket cost in USD
     * @param airline      the airline name (e.g. "EgyptAir")
     * @param flightNumber the flight number (e.g. "MS-777")
     */
    public Flight(String title, String date, double cost,
                  String airline, String flightNumber) {
        super(title, date, cost);   // INHERITANCE: call parent constructor
        this.airline      = airline;
        this.flightNumber = flightNumber;
    }

    // --- Getters & Setters ---

    /** @return the airline name */
    public String getAirline() { return airline; }

    /** @param airline the new airline name */
    public void setAirline(String airline) { this.airline = airline; }

    /** @return the flight number */
    public String getFlightNumber() { return flightNumber; }

    /** @param flightNumber the new flight number */
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    /**
     * {@inheritDoc}
     *
     * <p>POLYMORPHISM: overrides the abstract method in {@link ItineraryItem}
     * to include airline and flight number details.</p>
     */
    @Override
    public String getDisplayInfo() {
        return String.format("✈ Flight: %s (%s) | Airline: %s | Flight No: %s | Cost: $%.2f",
                getTitle(), getDate(), airline, flightNumber, getCost());
    }

    /** {@inheritDoc} */
    @Override
    public String getType() { return "Flight"; }
}
