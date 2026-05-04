package tripplanner.ui;

import tripplanner.model.Activity;
import tripplanner.model.Flight;
import tripplanner.model.Hotel;
import tripplanner.model.ItineraryItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A modal dialog that lets the user enter details for a new itinerary item.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>GridBagLayout for flexible field placement</li>
 *   <li>JSpinner (SpinnerDateModel) for safe date selection — no manual typing</li>
 *   <li>CardLayout panel that swaps specific fields based on item type</li>
 *   <li>Visual validation: highlights empty required fields in red</li>
 * </ul>
 *
 * <p>After the dialog closes, call {@link #getCreatedItem()} to retrieve
 * the item the user saved, or {@code null} if they cancelled.</p>
 */
public class InputForm extends JDialog {

    // ---- Common fields ----
    private JTextField titleField;
    private JSpinner   dateSpinner;   // replaces the old plain text dateField
    private JTextField costField;

    // ---- Type selector ----
    private JComboBox<String> typeComboBox;

    // ---- CardLayout for type-specific fields ----
    private JPanel     cardsPanel;
    private CardLayout cardLayout;

    // Flight-specific
    private JTextField airlineField;
    private JTextField flightNumberField;

    // Hotel-specific
    private JTextField hotelCityField;
    private JSpinner   nightsSpinner;

    // Activity-specific
    private JTextField locationField;

    /** The item created on Save; remains null if the user cancelled. */
    private ItineraryItem createdItem = null;

    // Date format used when reading the JSpinner value
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // =========================================================================
    // Constructor
    // =========================================================================

    /**
     * Constructs and displays the input form dialog.
     *
     * @param parent the parent JFrame (used to centre the dialog)
     */
    public InputForm(JFrame parent) {
        super(parent, "Add New Plan", true);
        setSize(460, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBorder(new EmptyBorder(16, 16, 12, 16));

        root.add(buildTypeRow(),    BorderLayout.NORTH);
        root.add(buildCenterForm(), BorderLayout.CENTER);
        root.add(buildButtons(),    BorderLayout.SOUTH);

        setContentPane(root);
    }

    // =========================================================================
    // Panel builders
    // =========================================================================

    /** Builds the top row containing the "Type" label and ComboBox. */
    private JPanel buildTypeRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel typeLabel = new JLabel("Item Type:");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        String[] types = {"Flight", "Hotel", "Activity"};
        typeComboBox = new JComboBox<>(types);
        typeComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        typeComboBox.addActionListener(e -> {
            String selected = (String) typeComboBox.getSelectedItem();
            cardLayout.show(cardsPanel, selected);
        });

        row.add(typeLabel);
        row.add(typeComboBox);
        return row;
    }

    /**
     * Builds the center area: common fields at the top, type-specific card below.
     *
     * @return the assembled center panel
     */
    private JPanel buildCenterForm() {
        JPanel center = new JPanel(new BorderLayout(0, 10));

        // ---- Common section ----
        JPanel commonPanel = new JPanel(new GridBagLayout());
        commonPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 130), 1, true),
                "General Info"));

        GridBagConstraints gbc = defaultGbc();

        // Title row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        commonPanel.add(label("Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        titleField = new JTextField();
        commonPanel.add(titleField, gbc);

        // Date row — JSpinner with SpinnerDateModel
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        commonPanel.add(label("Date:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null,
                java.util.Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        commonPanel.add(dateSpinner, gbc);

        // Cost row
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        commonPanel.add(label("Cost ($):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        costField = new JTextField();
        commonPanel.add(costField, gbc);

        // ---- Type-specific section (CardLayout) ----
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 130), 1, true),
                "Specific Info"));

        cardsPanel.add(buildFlightCard(),   "Flight");
        cardsPanel.add(buildHotelCard(),    "Hotel");
        cardsPanel.add(buildActivityCard(), "Activity");

        center.add(commonPanel, BorderLayout.NORTH);
        center.add(cardsPanel,  BorderLayout.CENTER);
        return center;
    }

    // ---- Type-specific cards ------------------------------------------------

    /** @return the Flight-specific input card */
    private JPanel buildFlightCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        card.add(label("Airline:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        airlineField = new JTextField();
        card.add(airlineField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        card.add(label("Flight No:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        flightNumberField = new JTextField();
        card.add(flightNumberField, gbc);

        return card;
    }

    /** @return the Hotel-specific input card */
    private JPanel buildHotelCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        card.add(label("City:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        hotelCityField = new JTextField();
        card.add(hotelCityField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        card.add(label("Nights:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        nightsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        nightsSpinner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        card.add(nightsSpinner, gbc);

        return card;
    }

    /** @return the Activity-specific input card */
    private JPanel buildActivityCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        card.add(label("Location:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        locationField = new JTextField();
        card.add(locationField, gbc);

        return card;
    }

    // ---- Buttons bar --------------------------------------------------------

    /** @return the bottom panel containing Save and Cancel buttons */
    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));

        JButton saveBtn   = coloredButton("Save",   new Color(137, 180, 130), Color.BLACK);
        JButton cancelBtn = coloredButton("Cancel", new Color(243, 139, 168), Color.BLACK);

        saveBtn  .addActionListener(e -> saveItem());
        cancelBtn.addActionListener(e -> dispose());

        panel.add(saveBtn);
        panel.add(cancelBtn);
        return panel;
    }

    // =========================================================================
    // Save logic
    // =========================================================================

    /**
     * Validates all required fields and creates the appropriate {@link ItineraryItem}
     * subclass (POLYMORPHISM + INHERITANCE).
     *
     * <p>On validation failure, offending fields are highlighted with a red border
     * and an error message is shown.</p>
     */
    private void saveItem() {
        // Reset any previous validation highlights
        resetBorders();

        String title = titleField.getText().trim();
        String cost  = costField.getText().trim();
        String type  = (String) typeComboBox.getSelectedItem();

        boolean valid = true;

        if (title.isEmpty())  { highlight(titleField); valid = false; }
        if (cost.isEmpty())   { highlight(costField);  valid = false; }

        if (!valid) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all highlighted fields.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double costValue;
        try {
            costValue = Double.parseDouble(cost);
            if (costValue < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            highlight(costField);
            JOptionPane.showMessageDialog(this,
                    "Cost must be a positive number (e.g. 150.00).",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Read the date from JSpinner (always valid — no free-text input)
        Date   selectedDate = (Date) dateSpinner.getValue();
        String dateStr      = DATE_FORMAT.format(selectedDate);

        // INHERITANCE + POLYMORPHISM: create the right subclass
        try {
            createdItem = switch (type) {
                case "Flight" -> {
                    String airline   = airlineField.getText().trim();
                    String flightNum = flightNumberField.getText().trim();
                    if (airline.isEmpty())   { highlight(airlineField);       valid = false; }
                    if (flightNum.isEmpty()) { highlight(flightNumberField);  valid = false; }
                    if (!valid) yield null;
                    yield new Flight(title, dateStr, costValue, airline, flightNum);
                }
                case "Hotel" -> {
                    String city   = hotelCityField.getText().trim();
                    int    nights = (int) nightsSpinner.getValue();
                    if (city.isEmpty()) { highlight(hotelCityField); valid = false; }
                    if (!valid) yield null;
                    yield new Hotel(title, dateStr, costValue, city, nights);
                }
                case "Activity" -> {
                    String location = locationField.getText().trim();
                    if (location.isEmpty()) { highlight(locationField); valid = false; }
                    if (!valid) yield null;
                    yield new Activity(title, dateStr, costValue, location);
                }
                default -> null;
            };
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!valid) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in the highlighted type-specific fields.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            createdItem = null;
            return;
        }

        dispose(); // Close dialog — success
    }

    // =========================================================================
    // Utilities
    // =========================================================================

    /**
     * Returns the itinerary item created by the user, or {@code null} if cancelled.
     *
     * @return the new {@link ItineraryItem}, or {@code null}
     */
    public ItineraryItem getCreatedItem() {
        return createdItem;
    }

    /** Builds a pre-configured GridBagConstraints for standard two-column form rows. */
    private GridBagConstraints defaultGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(5, 6, 5, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;
        return gbc;
    }

    /** Creates a bold form label. */
    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        return lbl;
    }

    /** Creates a button with custom background and foreground colors. */
    private JButton coloredButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Applies a red border to a text field to signal a validation error. */
    private void highlight(JTextField field) {
        field.setBorder(new LineBorder(Color.RED, 2, true));
    }

    /** Resets all text-field borders to their default look. */
    private void resetBorders() {
        for (JTextField f : new JTextField[]{
                titleField, costField,
                airlineField, flightNumberField,
                hotelCityField, locationField}) {
            if (f != null) f.setBorder(UIManager.getBorder("TextField.border"));
        }
    }
}
