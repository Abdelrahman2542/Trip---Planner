package tripplanner;

import tripplanner.model.ItineraryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the in-memory collection of {@link ItineraryItem} objects.
 *
 * <p>Acts as the central data store for the application. The UI layer
 * communicates with this class to add, remove, and query items.</p>
 */
public class TripManager {

    /** Internal list — stores Flight, Hotel, and Activity objects as ItineraryItem references. */
    private final List<ItineraryItem> items;

    /** Constructs an empty TripManager. */
    public TripManager() {
        items = new ArrayList<>();
    }

    /**
     * Adds a new itinerary item to the collection.
     *
     * @param item the item to add (Flight, Hotel, or Activity)
     */
    public void addItem(ItineraryItem item) {
        items.add(item);
    }

    /**
     * Removes the item at the specified index.
     *
     * @param index 0-based index in the list
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public void removeItem(int index) {
        items.remove(index);
    }

    /**
     * Replaces the item at the given index with a new item.
     *
     * @param index   0-based index to update
     * @param newItem the replacement item
     */
    public void updateItem(int index, ItineraryItem newItem) {
        items.set(index, newItem);
    }

    /**
     * Returns the full list of itinerary items.
     *
     * @return unmodifiable-style list (direct reference — do not modify externally)
     */
    public List<ItineraryItem> getItems() {
        return items;
    }

    /**
     * Replaces all current items with the given list (used after CSV/JSON import).
     *
     * @param imported list of items loaded from file
     */
    public void setItems(List<ItineraryItem> imported) {
        items.clear();
        items.addAll(imported);
    }

    /**
     * Calculates the sum of costs for all itinerary items.
     *
     * @return total cost in USD
     */
    public double calculateTotalCost() {
        double total = 0.0;
        for (ItineraryItem item : items) {
            total += item.getCost();
        }
        return total;
    }

    /**
     * Returns the number of items currently managed.
     *
     * @return item count
     */
    public int size() {
        return items.size();
    }
}
