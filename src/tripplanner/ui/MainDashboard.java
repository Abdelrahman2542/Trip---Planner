package tripplanner.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import tripplanner.TripManager;
import tripplanner.model.ItineraryItem;
import tripplanner.storage.ReminderService;
import tripplanner.storage.TripStorage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/** Main modern dashboard window for the Trip Planner app. */
public class MainDashboard extends JFrame {

    private final TripManager tripManager;
    private final TripStorage tripStorage;
    private final ReminderService reminderService;
    private boolean isDarkMode = true;

    private DefaultTableModel tableModel;
    private JTable tripTable;
    private JLabel totalCostLabel;
    private JLabel planCountLabel;
    private JLabel emptyStateLabel;
    private JScrollPane tableScroll;
    private JButton themeToggleBtn;

    private static final String[] COLUMNS = {"Type", "Title", "Date", "Cost ($)", "Details"};

    private static final Color DARK_BG = new Color(17, 24, 39);
    private static final Color DARK_CARD = new Color(31, 41, 55);
    private static final Color DARK_CARD_2 = new Color(38, 48, 65);
    private static final Color DARK_TEXT = new Color(243, 244, 246);
    private static final Color DARK_MUTED = new Color(156, 163, 175);
    private static final Color BRAND = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(52, 211, 153);
    private static final Color DANGER = new Color(248, 113, 113);
    private static final Color WARNING = new Color(251, 191, 36);
    private static final Color INFO = new Color(56, 189, 248);

    public MainDashboard() {
        tripManager = new TripManager();
        tripStorage = new TripStorage();
        reminderService = new ReminderService(tripManager);

        buildUI();
        reminderService.start();
    }

    private void buildUI() {
        setTitle("Trip Planner");
        setMinimumSize(new Dimension(980, 640));
        setSize(1080, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                reminderService.stop();
            }
        });

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(DARK_BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        root.add(buildHeaderPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildFooterPanel(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(4, 4, 18, 4));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);

        JLabel title = new JLabel("✈ Trip Planner");
        title.setForeground(DARK_TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));

        JLabel subtitle = new JLabel("Plan flights, hotels, and activities in one clean itinerary");
        subtitle.setForeground(DARK_MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        titleBox.add(title);
        titleBox.add(subtitle);

        themeToggleBtn = new JButton("☀ Light Mode");
        themeToggleBtn.putClientProperty("JButton.buttonType", "roundRect");
        themeToggleBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        themeToggleBtn.setFocusPainted(false);
        themeToggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeToggleBtn.addActionListener(e -> toggleTheme());

        header.add(titleBox, BorderLayout.WEST);
        header.add(themeToggleBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(buildStatsPanel(), BorderLayout.NORTH);
        center.add(buildTableCard(), BorderLayout.CENTER);
        return center;
    }

    private JPanel buildStatsPanel() {
        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 2, 14, 0));
        stats.setOpaque(false);

        planCountLabel = new JLabel("0 plans");
        totalCostLabel = new JLabel("$0.00");

        stats.add(statCard("Itinerary Items", planCountLabel, "Your saved trip plans", BRAND));
        stats.add(statCard("Estimated Budget", totalCostLabel, "Total cost of all items", SUCCESS));

        wrap.add(stats, BorderLayout.NORTH);
        wrap.add(buildToolbar(), BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel statCard(String title, JLabel valueLabel, String hint, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(DARK_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(16, 18, 16, 18)));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setForeground(DARK_MUTED);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        valueLabel.setForeground(DARK_TEXT);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel hintLabel = new JLabel(hint);
        hintLabel.setForeground(DARK_MUTED);
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(hintLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton addBtn = styledButton("＋ Add Plan", BRAND, Color.WHITE);
        JButton deleteBtn = styledButton("🗑 Delete", DANGER, Color.WHITE);
        JButton summaryBtn = styledButton("📋 Summary", new Color(168, 85, 247), Color.WHITE);
        JButton exportBtn = styledButton("⬇ Export CSV", INFO, Color.BLACK);
        JButton importBtn = styledButton("⬆ Import CSV", WARNING, Color.BLACK);

        addBtn.addActionListener(e -> openInputForm());
        deleteBtn.addActionListener(e -> deleteSelectedRow());
        summaryBtn.addActionListener(e -> showTripSummary());
        exportBtn.addActionListener(e -> exportCSV());
        importBtn.addActionListener(e -> importCSV());

        toolbar.add(addBtn);
        toolbar.add(deleteBtn);
        toolbar.add(summaryBtn);
        toolbar.add(Box.createHorizontalStrut(18));
        toolbar.add(exportBtn);
        toolbar.add(importBtn);
        return toolbar;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(DARK_CARD);
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel tableTitle = new JLabel("Itinerary Overview");
        tableTitle.setForeground(DARK_TEXT);
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableTitle.setBorder(new EmptyBorder(0, 2, 12, 0));

        card.add(tableTitle, BorderLayout.NORTH);
        card.add(buildTable(), BorderLayout.CENTER);
        return card;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tripTable = new JTable(tableModel);
        tripTable.setRowHeight(42);
        tripTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tripTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripTable.setShowGrid(false);
        tripTable.setIntercellSpacing(new Dimension(0, 0));
        tripTable.setFillsViewportHeight(true);
        tripTable.setBackground(DARK_CARD_2);
        tripTable.setForeground(DARK_TEXT);
        tripTable.setSelectionBackground(new Color(67, 56, 202));
        tripTable.setSelectionForeground(Color.WHITE);

        JTableHeader header = tripTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(55, 65, 81));
        header.setForeground(DARK_TEXT);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new ModernTableRenderer();
        for (int i = 0; i < tripTable.getColumnCount(); i++) {
            tripTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        int[] colWidths = {90, 220, 120, 100, 420};
        for (int i = 0; i < colWidths.length; i++) {
            tripTable.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }

        emptyStateLabel = new JLabel("No plans yet — click ＋ Add Plan to start your itinerary", SwingConstants.CENTER);
        emptyStateLabel.setForeground(DARK_MUTED);
        emptyStateLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        tableScroll = new JScrollPane(tripTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 1));
        tableScroll.getViewport().setBackground(DARK_CARD_2);
        tableScroll.setColumnHeaderView(header);
        tableScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JPanel() {{ setBackground(new Color(55, 65, 81)); }});
        tripTable.setPreferredScrollableViewportSize(new Dimension(900, 360));
        updateEmptyState();
        return tableScroll;
    }

    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(14, 4, 0, 4));

        JLabel tip = new JLabel("Tip: Import/Export CSV to save your itinerary outside the app.");
        tip.setForeground(DARK_MUTED);
        tip.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel signature = new JLabel("OOP Project • Swing + FlatLaf");
        signature.setForeground(DARK_MUTED);
        signature.setFont(new Font("SansSerif", Font.BOLD, 12));

        footer.add(tip, BorderLayout.WEST);
        footer.add(signature, BorderLayout.EAST);
        return footer;
    }

    private void openInputForm() {
        InputForm form = new InputForm(this);
        form.setVisible(true);

        ItineraryItem newItem = form.getCreatedItem();
        if (newItem != null) {
            tripManager.addItem(newItem);
            addRowToTable(newItem);
            refreshStats();
        }
    }

    private void deleteSelectedRow() {
        int selected = tripTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete the selected item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            tripManager.removeItem(selected);
            tableModel.removeRow(selected);
            refreshStats();
        }
    }

    private void showTripSummary() {
        List<String> summaries = tripManager.getDisplaySummaries();

        StringBuilder message = new StringBuilder();
        message.append("Trip Planner helps users organize flights, hotels, and activities in one itinerary.\n\n");

        if (summaries.isEmpty()) {
            message.append("No plans added yet. Click 'Add Plan' to start building your trip.");
        } else {
            message.append("Current itinerary:\n");
            for (int i = 0; i < summaries.size(); i++) {
                message.append(i + 1).append(". ").append(summaries.get(i)).append('\n');
            }
            message.append("\nTotal Cost: $").append(String.format("%.2f", tripManager.calculateTotalCost()));
        }

        JTextArea textArea = new JTextArea(message.toString(), 14, 70);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Trip Summary / OOP Polymorphism Demo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export trips to CSV");
        chooser.setSelectedFile(new java.io.File("trips.csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";
            try {
                tripStorage.saveToCSV(tripManager.getItems(), path);
                JOptionPane.showMessageDialog(this, "Trips exported successfully to:\n" + path,
                        "Export OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed:\n" + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import trips from CSV");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<ItineraryItem> loaded = tripStorage.loadFromCSV(chooser.getSelectedFile().getAbsolutePath());
                tripManager.setItems(loaded);
                rebuildTable();
                refreshStats();

                JOptionPane.showMessageDialog(this, loaded.size() + " trip(s) imported successfully.",
                        "Import OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Import failed:\n" + ex.getMessage(),
                        "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleTheme() {
        try {
            if (isDarkMode) {
                FlatLightLaf.setup();
                themeToggleBtn.setText("🌙 Dark Mode");
            } else {
                FlatDarkLaf.setup();
                themeToggleBtn.setText("☀ Light Mode");
            }
            isDarkMode = !isDarkMode;
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addRowToTable(ItineraryItem item) {
        tableModel.addRow(new Object[]{
                item.getType(),
                item.getTitle(),
                item.getDate(),
                String.format("%.2f", item.getCost()),
                extractDetails(item)
        });
    }

    private void rebuildTable() {
        tableModel.setRowCount(0);
        for (ItineraryItem item : tripManager.getItems()) {
            addRowToTable(item);
        }
    }

    private String extractDetails(ItineraryItem item) {
        if (item instanceof tripplanner.model.Flight) {
            tripplanner.model.Flight f = (tripplanner.model.Flight) item;
            return "Airline: " + f.getAirline() + " | Flight No: " + f.getFlightNumber();
        } else if (item instanceof tripplanner.model.Hotel) {
            tripplanner.model.Hotel h = (tripplanner.model.Hotel) item;
            return "City: " + h.getCity() + " | Nights: " + h.getNumberOfNights();
        } else if (item instanceof tripplanner.model.Activity) {
            tripplanner.model.Activity a = (tripplanner.model.Activity) item;
            return "Location: " + a.getLocation();
        }
        return "";
    }

    private void refreshStats() {
        int count = tripManager.getItems().size();
        planCountLabel.setText(count + (count == 1 ? " plan" : " plans"));
        totalCostLabel.setText(String.format("$%.2f", tripManager.calculateTotalCost()));
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (emptyStateLabel != null && tripTable != null && tableScroll != null) {
            tableScroll.setViewportView(tableModel.getRowCount() == 0 ? emptyStateLabel : tripTable);
        }
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 14, 9, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private class ModernTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBorder(new EmptyBorder(0, 12, 0, 12));
            cell.setFont(new Font("SansSerif", column == 0 ? Font.BOLD : Font.PLAIN, 14));

            if (!isSelected) {
                cell.setBackground(row % 2 == 0 ? DARK_CARD_2 : new Color(34, 44, 60));
                cell.setForeground(DARK_TEXT);
            }

            if (column == 0) {
                String type = String.valueOf(value);
                if ("Flight".equals(type)) cell.setText("✈ " + type);
                else if ("Hotel".equals(type)) cell.setText("🏨 " + type);
                else if ("Activity".equals(type)) cell.setText("📍 " + type);
            }
            if (column == 3) {
                cell.setHorizontalAlignment(SwingConstants.RIGHT);
                cell.setText("$" + value);
            } else {
                cell.setHorizontalAlignment(SwingConstants.LEFT);
            }
            return cell;
        }
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
    }
}
