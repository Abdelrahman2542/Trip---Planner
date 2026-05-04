package tripplanner.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import tripplanner.TripManager;
import tripplanner.model.ItineraryItem;
import tripplanner.storage.ReminderService;
import tripplanner.storage.TripStorage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * The main application window for Trip Planner.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>FlatLaf Dark theme (toggleable to Light)</li>
 *   <li>JTable-based itinerary display with 5 columns</li>
 *   <li>Add / Delete item buttons</li>
 *   <li>Export CSV / Import CSV buttons via JFileChooser</li>
 *   <li>Total cost display in the footer</li>
 *   <li>Automatic trip reminders via {@link ReminderService}</li>
 * </ul>
 */
public class MainDashboard extends JFrame {

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private final TripManager     tripManager;
    private final TripStorage     tripStorage;
    private final ReminderService reminderService;
    private       boolean         isDarkMode = true;

    // -------------------------------------------------------------------------
    // UI components
    // -------------------------------------------------------------------------

    private DefaultTableModel tableModel;
    private JTable            tripTable;
    private JLabel            totalCostLabel;
    private JButton           themeToggleBtn;

    // Column names shown in the table header
    private static final String[] COLUMNS = {"Type", "Title", "Date", "Cost ($)", "Details"};

    // =========================================================================
    // Constructor
    // =========================================================================

    /**
     * Builds and configures the main dashboard window.
     */
    public MainDashboard() {
        tripManager     = new TripManager();
        tripStorage     = new TripStorage();
        reminderService = new ReminderService(tripManager);

        buildUI();

        // Start the reminder service after the window is built
        reminderService.start();
    }

    // =========================================================================
    // UI Construction
    // =========================================================================

    /** Assembles all panels and adds them to the frame. */
    private void buildUI() {
        setTitle("Trip Planner");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Stop the reminder service when the window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                reminderService.stop();
            }
        });

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        root.add(buildHeaderPanel(),  BorderLayout.NORTH);
        root.add(buildCenterPanel(),  BorderLayout.CENTER);
        root.add(buildFooterPanel(),  BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ---- Header Panel -------------------------------------------------------

    /**
     * Builds the top header bar containing the app title and theme-toggle button.
     *
     * @return the configured header panel
     */
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 46));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Trip Planner");
        title.setForeground(new Color(205, 214, 244));
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        themeToggleBtn = new JButton(" Light Mode");
        themeToggleBtn.setFocusPainted(false);
        themeToggleBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        themeToggleBtn.addActionListener(e -> toggleTheme());

        header.add(title,          BorderLayout.WEST);
        header.add(themeToggleBtn, BorderLayout.EAST);
        return header;
    }

    // ---- Center Panel (toolbar + table) -------------------------------------

    /**
     * Builds the center area: action toolbar above a scrollable JTable.
     *
     * @return the configured center panel
     */
    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBorder(new EmptyBorder(10, 14, 6, 14));

        center.add(buildToolbar(), BorderLayout.NORTH);
        center.add(buildTable(),   BorderLayout.CENTER);
        return center;
    }

    /**
     * Builds the action toolbar with Add, Delete, Export, and Import buttons.
     *
     * @return the configured toolbar panel
     */
    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JButton addBtn    = styledButton(" Add Plan",    new Color(137, 180, 130));
        JButton deleteBtn = styledButton(" Delete",      new Color(243, 139, 168));
        JButton exportBtn = styledButton("  Export CSV",  new Color(137, 220, 235));
        JButton importBtn = styledButton("  Import CSV",  new Color(250, 179, 135));

        addBtn   .addActionListener(e -> openInputForm());
        deleteBtn.addActionListener(e -> deleteSelectedRow());
        exportBtn.addActionListener(e -> exportCSV());
        importBtn.addActionListener(e -> importCSV());

        toolbar.add(addBtn);
        toolbar.add(deleteBtn);
        toolbar.add(Box.createHorizontalStrut(16)); // visual spacer
        toolbar.add(exportBtn);
        toolbar.add(importBtn);
        return toolbar;
    }

    /**
     * Builds the JTable wrapped in a scroll pane.
     *
     * @return the configured scroll pane containing the trip table
     */
    private JScrollPane buildTable() {
        // Non-editable table model
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };

        tripTable = new JTable(tableModel);
        tripTable.setRowHeight(30);
        tripTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tripTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tripTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripTable.setShowGrid(false);
        tripTable.setIntercellSpacing(new Dimension(0, 0));

        // Column widths
        int[] colWidths = {80, 200, 100, 80, 360};
        for (int i = 0; i < colWidths.length; i++) {
            tripTable.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }

        JScrollPane scroll = new JScrollPane(tripTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // ---- Footer Panel -------------------------------------------------------

    /**
     * Builds the bottom footer bar that displays the running total cost.
     *
     * @return the configured footer panel
     */
    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(30, 30, 46));
        footer.setBorder(new EmptyBorder(10, 20, 10, 20));

        totalCostLabel = new JLabel("Total Cost:  $0.00");
        totalCostLabel.setForeground(new Color(166, 227, 161));
        totalCostLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        footer.add(totalCostLabel, BorderLayout.WEST);
        return footer;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    /** Opens the {@link InputForm} dialog, and if the user saves an item, adds it to the table. */
    private void openInputForm() {
        InputForm form = new InputForm(this);
        form.setVisible(true);

        // POLYMORPHISM: getCreatedItem() returns a Flight, Hotel, or Activity
        // but we handle it uniformly as ItineraryItem
        ItineraryItem newItem = form.getCreatedItem();
        if (newItem != null) {
            tripManager.addItem(newItem);
            addRowToTable(newItem);
            refreshTotalCost();
        }
    }

    /** Deletes the currently selected row from the table and the manager. */
    private void deleteSelectedRow() {
        int selected = tripTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a row to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete the selected item?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            tripManager.removeItem(selected);
            tableModel.removeRow(selected);
            refreshTotalCost();
        }
    }

    /** Exports all current items to a user-chosen CSV file. */
    private void exportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export trips to CSV");
        chooser.setSelectedFile(new java.io.File("trips.csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";
            try {
                tripStorage.saveToCSV(tripManager.getItems(), path);
                JOptionPane.showMessageDialog(this,
                        "Trips exported successfully to:\n" + path,
                        "Export OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Export failed:\n" + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Imports items from a user-chosen CSV file, replacing the current list. */
    private void importCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import trips from CSV");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<ItineraryItem> loaded =
                        tripStorage.loadFromCSV(chooser.getSelectedFile().getAbsolutePath());

                tripManager.setItems(loaded);
                rebuildTable();
                refreshTotalCost();

                JOptionPane.showMessageDialog(this,
                        loaded.size() + " trip(s) imported successfully.",
                        "Import OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Import failed:\n" + ex.getMessage(),
                        "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // Theme Toggle
    // =========================================================================

    /** Switches between FlatLaf Dark and Light themes at runtime. */
    private void toggleTheme() {
        try {
            if (isDarkMode) {
                FlatLightLaf.setup();
                themeToggleBtn.setText("Dark Mode");
            } else {
                FlatDarkLaf.setup();
                themeToggleBtn.setText("  Light Mode");
            }
            isDarkMode = !isDarkMode;
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // =========================================================================
    // Table helpers
    // =========================================================================

    /**
     * Appends a single row to the table model for the given item.
     *
     * @param item the item to display
     */
    private void addRowToTable(ItineraryItem item) {
        // Extract a short "Details" string based on the concrete type
        String details = extractDetails(item);
        tableModel.addRow(new Object[]{
                item.getType(),
                item.getTitle(),
                item.getDate(),
                String.format("%.2f", item.getCost()),
                details
        });
    }

    /** Clears the table and re-populates it from the current TripManager state. */
    private void rebuildTable() {
        tableModel.setRowCount(0);
        for (ItineraryItem item : tripManager.getItems()) {
            addRowToTable(item);
        }
    }

    /**
     * Returns a concise detail string appropriate for the item's type.
     *
     * @param item any itinerary item
     * @return a short human-readable detail string
     */
    private String extractDetails(ItineraryItem item) {
        if (item instanceof tripplanner.model.Flight f) {
            return "Airline: " + f.getAirline() + " | Flight No: " + f.getFlightNumber();
        } else if (item instanceof tripplanner.model.Hotel h) {
            return "City: " + h.getCity() + " | Nights: " + h.getNumberOfNights();
        } else if (item instanceof tripplanner.model.Activity a) {
            return "Location: " + a.getLocation();
        }
        return "";
    }

    /** Recalculates the total cost and updates the footer label. */
    private void refreshTotalCost() {
        totalCostLabel.setText(
                String.format("Total Cost:  $%.2f", tripManager.calculateTotalCost()));
    }

    // =========================================================================
    // Utility
    // =========================================================================

    /**
     * Creates a styled button with the given label and accent color.
     *
     * @param text  the button label
     * @param color the button background color
     * @return the configured JButton
     */
    private JButton styledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // Entry Point
    // =========================================================================

    /**
     * Application entry point.
     *
     * <p>Applies FlatLaf Dark theme before creating any Swing components.</p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Apply FlatLaf Dark theme BEFORE creating any Swing component
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
    }
}
