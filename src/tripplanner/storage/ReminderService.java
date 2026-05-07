package tripplanner.storage;

import tripplanner.TripManager;
import tripplanner.model.ItineraryItem;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Periodically checks whether any planned trip date matches today's date
 * and shows a reminder dialog if so.
 *
 * <p>Uses {@link javax.swing.Timer} (not {@link java.util.Timer}) so all
 * UI interactions happen on the Swing Event Dispatch Thread (EDT).</p>
 *
 * <p>Usage:</p>
 * <pre>
 *     ReminderService service = new ReminderService(tripManager);
 *     service.start();   // call once when the dashboard opens
 *     // service.stop(); // call when closing the app
 * </pre>
 */
public class ReminderService {

    /** Check interval: every 60 seconds (60,000 ms). */
    private static final int CHECK_INTERVAL_MS = 60_000;

    private final TripManager tripManager;
    private final Timer       swingTimer;

    /**
     * Constructs the ReminderService.
     *
     * @param tripManager the manager to read trip dates from
     */
    public ReminderService(TripManager tripManager) {
        this.tripManager = tripManager;

        // Create a repeating Swing timer
        this.swingTimer = new Timer(CHECK_INTERVAL_MS, e -> checkTodaysTrips());
    }

    /** Starts the periodic reminder check. Call once on application startup. */
    public void start() {
        // Run one immediate check, then continue on interval
        checkTodaysTrips();
        swingTimer.start();
    }

    /** Stops the reminder service. Call when the application is closing. */
    public void stop() {
        swingTimer.stop();
    }

    // -------------------------------------------------------------------------
    // Internal check logic
    // -------------------------------------------------------------------------

    /**
     * Inspects all managed items and shows a reminder dialog for any whose
     * date matches today.
     */
    private void checkTodaysTrips() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE); // "YYYY-MM-DD"
        List<ItineraryItem> items = tripManager.getItems();

        for (ItineraryItem item : items) {
            if (isTodayOrSoon(item.getDate(), today)) {
                showReminder(item);
            }
        }
    }

    /**
     * Checks whether the item date is today or within the next 2 days.
     *
     * @param itemDate the item's date string (YYYY-MM-DD)
     * @param today    today's date string (YYYY-MM-DD)
     * @return {@code true} if the item date is today or up to 2 days away
     */
    private boolean isTodayOrSoon(String itemDate, String today) {
        try {
            LocalDate date      = LocalDate.parse(itemDate, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate todayDate = LocalDate.parse(today,    DateTimeFormatter.ISO_LOCAL_DATE);
            long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(todayDate, date);
            return daysUntil >= 0 && daysUntil <= 2;
        } catch (DateTimeParseException e) {
            return false; // Skip items with non-standard date formats
        }
    }

    /**
     * Shows a popup reminder notification for the given item.
     *
     * @param item the upcoming itinerary item
     */
    private void showReminder(ItineraryItem item) {
        String message = String.format(
                "⏰  Upcoming Trip Reminder!\n\n%s\nDate: %s",
                item.getDisplayInfo(), item.getDate());

        JOptionPane.showMessageDialog(
                null,
                message,
                "Trip Reminder",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
