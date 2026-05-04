package tripplanner.storage;

import tripplanner.model.Activity;
import tripplanner.model.Flight;
import tripplanner.model.Hotel;
import tripplanner.model.ItineraryItem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading {@link ItineraryItem} collections to/from CSV files.
 *
 * <p>CSV format (one row per item):</p>
 * <pre>
 * type,title,date,cost,extra1,extra2
 * Flight,Cairo-London,2024-06-15,850.00,EgyptAir,MS-777
 * Hotel,Hilton Cairo,2024-06-16,200.00,Cairo,3
 * Activity,Pyramids Tour,2024-06-17,50.00,Giza,
 * </pre>
 */
public class TripStorage {

    /** The CSV header line written at the top of every exported file. */
    private static final String CSV_HEADER = "type,title,date,cost,extra1,extra2";

    /**
     * Saves a list of itinerary items to a CSV file.
     *
     * @param items    the list of items to save
     * @param filePath the destination file path (e.g. "C:/trips/mytrip.csv")
     * @throws IOException if the file cannot be written
     */
    public void saveToCSV(List<ItineraryItem> items, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writer.write(CSV_HEADER);
            writer.newLine();

            for (ItineraryItem item : items) {
                writer.write(buildCSVRow(item));
                writer.newLine();
            }
        }
    }

    /**
     * Loads itinerary items from a CSV file.
     *
     * <p>Rows that cannot be parsed are silently skipped.</p>
     *
     * @param filePath the source CSV file path
     * @return a list of parsed {@link ItineraryItem} objects
     * @throws IOException if the file cannot be read
     */
    public List<ItineraryItem> loadFromCSV(String filePath) throws IOException {
        List<ItineraryItem> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                // Skip the header row
                if (firstLine) { firstLine = false; continue; }
                if (line.isBlank()) continue;

                ItineraryItem item = parseCSVRow(line);
                if (item != null) result.add(item);
            }
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Converts a single {@link ItineraryItem} into a CSV row string.
     *
     * @param item the item to convert
     * @return a comma-separated string with 6 fields
     */
    private String buildCSVRow(ItineraryItem item) {
        String extra1 = "";
        String extra2 = "";

        if (item instanceof Flight f) {
            extra1 = f.getAirline();
            extra2 = f.getFlightNumber();
        } else if (item instanceof Hotel h) {
            extra1 = h.getCity();
            extra2 = String.valueOf(h.getNumberOfNights());
        } else if (item instanceof Activity a) {
            extra1 = a.getLocation();
            // extra2 remains empty for Activity
        }

        return String.join(",",
                item.getType(),
                escapeCsv(item.getTitle()),
                item.getDate(),
                String.format("%.2f", item.getCost()),
                escapeCsv(extra1),
                escapeCsv(extra2));
    }

    /**
     * Parses a single CSV row and returns the corresponding {@link ItineraryItem}.
     *
     * @param line a raw CSV row string
     * @return the parsed item, or {@code null} if the row is malformed
     */
    private ItineraryItem parseCSVRow(String line) {
        try {
            String[] parts = line.split(",", -1);
            if (parts.length < 6) return null;

            String type   = parts[0].trim();
            String title  = unescapeCsv(parts[1].trim());
            String date   = parts[2].trim();
            double cost   = Double.parseDouble(parts[3].trim());
            String extra1 = unescapeCsv(parts[4].trim());
            String extra2 = unescapeCsv(parts[5].trim());

            return switch (type) {
                case "Flight"   -> new Flight(title, date, cost, extra1, extra2);
                case "Hotel"    -> new Hotel(title, date, cost, extra1,
                                             extra2.isEmpty() ? 1 : Integer.parseInt(extra2));
                case "Activity" -> new Activity(title, date, cost, extra1);
                default         -> null;
            };
        } catch (Exception e) {
            // Malformed row — skip silently
            return null;
        }
    }

    /** Wraps a value in quotes if it contains a comma. */
    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.contains(",") ? "\"" + value + "\"" : value;
    }

    /** Strips surrounding quotes if present. */
    private String unescapeCsv(String value) {
        if (value == null) return "";
        if (value.startsWith("\"") && value.endsWith("\""))
            return value.substring(1, value.length() - 1);
        return value;
    }
}
