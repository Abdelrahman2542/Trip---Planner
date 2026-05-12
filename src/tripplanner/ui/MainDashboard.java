/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package tripplanner.ui;

/**
 *
 * @author Compumarts
 */
public class MainDashboard extends javax.swing.JFrame {
    private final tripplanner.TripManager tripManager;
    private final tripplanner.storage.TripStorage tripStorage;
    private final tripplanner.storage.ReminderService reminderService;
    private javax.swing.table.DefaultTableModel tableModel;

    private static final java.awt.Color C_BG      = new java.awt.Color(17,  24,  39);
    private static final java.awt.Color C_CARD    = new java.awt.Color(31,  41,  55);
    private static final java.awt.Color C_MUTED   = new java.awt.Color(156, 163, 175);
    private static final java.awt.Color C_TEXT    = new java.awt.Color(243, 244, 246);
    private static final java.awt.Color C_PURPLE  = new java.awt.Color(99,  102, 241);
    private static final java.awt.Color C_GREEN   = new java.awt.Color(52,  211, 153);
    private static final java.awt.Color C_RED     = new java.awt.Color(239, 68,  68);
    private static final java.awt.Color C_VIOLET  = new java.awt.Color(168, 85,  247);
    private static final java.awt.Color C_CYAN    = new java.awt.Color(56,  189, 248);
    private static final java.awt.Color C_AMBER   = new java.awt.Color(251, 191, 36);
    private static final java.awt.Color C_DARK    = new java.awt.Color(17,  24,  39);
    private static final java.awt.Color C_SURFACE = new java.awt.Color(55,  65,  81);
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainDashboard.class.getName());

    /**
     * Creates new form MainDashboard
     */
    public MainDashboard() {
        tripManager     = new tripplanner.TripManager();
        tripStorage     = new tripplanner.storage.TripStorage();
        reminderService = new tripplanner.storage.ReminderService(tripManager);
        initComponents();
        applyColors();
        setupTable();
        reminderService.start();

        addWindowListener(new java.awt.event.WindowAdapter() {
        public void windowClosing(java.awt.event.WindowEvent e) {
        reminderService.stop();
        }
    });
    }
    private void applyColors() {
    // Root background â€” this is the reliable way
    getContentPane().setBackground(C_BG);

    // Stat cards
    styleCard(statCard1, C_PURPLE);
    styleCard(statCard2, C_GREEN);

    // Labels inside cards
    planCountLabel.setForeground(C_TEXT);
    totalCostLabel.setForeground(C_TEXT);

    // Muted small labels â€” find them by going to Navigator,
    // they are the first label inside each stat card
    // Set their foreground here by variable name if you named them,
    // or just set them all at once in the label properties in Design view

    // Toolbar buttons
    styleButton(addBtn,     C_PURPLE, C_TEXT);
    styleButton(deleteBtn,  C_RED,    C_TEXT);
    styleButton(summaryBtn, C_VIOLET, C_TEXT);
    styleButton(exportBtn,  C_CYAN,   C_DARK);
    styleButton(importBtn,  C_AMBER,  C_DARK);
    themeToggleBtn.setBackground(C_SURFACE);
    themeToggleBtn.setForeground(C_TEXT);

    // Table card
    tableCard.setBackground(C_CARD);
    tableCard.setOpaque(true);

    // Table itself
    tripTable.setBackground(new java.awt.Color(38, 48, 65));
    tripTable.setForeground(C_TEXT);
    tripTable.setGridColor(C_SURFACE);
    tripTable.setSelectionBackground(C_PURPLE);
    tripTable.setSelectionForeground(C_TEXT);

    // Title label colors (the ones you dragged onto canvas)
    titleLabel.setForeground(C_TEXT);
}

private void styleCard(javax.swing.JPanel card, java.awt.Color accentColor) {
    card.setBackground(C_CARD);
    card.setOpaque(true);
    card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
        javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14)
    ));
}

private void styleButton(javax.swing.JButton btn,
                          java.awt.Color bg, java.awt.Color fg) {
    btn.setBackground(bg);
    btn.setForeground(fg);
    btn.setOpaque(true);
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setCursor(java.awt.Cursor.getPredefinedCursor(
        java.awt.Cursor.HAND_CURSOR));
}

private void setupTable() {
    String[] cols = {"Type", "Title", "Date", "Cost ($)", "Details"};
    tableModel = new javax.swing.table.DefaultTableModel(cols, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    tripTable.setModel(tableModel);
    tripTable.setRowHeight(30);

    // Header style
    javax.swing.table.JTableHeader h = tripTable.getTableHeader();
    h.setBackground(C_BG);
    h.setForeground(C_MUTED);
    h.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
    h.setReorderingAllowed(false);

    // Right-align cost column
    javax.swing.table.DefaultTableCellRenderer right =
        new javax.swing.table.DefaultTableCellRenderer();
    right.setHorizontalAlignment(javax.swing.JLabel.RIGHT);
    tripTable.getColumnModel().getColumn(3).setCellRenderer(right);

    // Column widths
    tripTable.getColumnModel().getColumn(0).setPreferredWidth(90);
    tripTable.getColumnModel().getColumn(1).setPreferredWidth(160);
    tripTable.getColumnModel().getColumn(2).setPreferredWidth(100);
    tripTable.getColumnModel().getColumn(3).setPreferredWidth(85);
    tripTable.getColumnModel().getColumn(4).setPreferredWidth(320);
}

private void refreshStats() {
    int count = tripManager.size();
    planCountLabel.setText(count + (count == 1 ? " plan" : " plans"));
    totalCostLabel.setText(String.format("$%.2f",
        tripManager.calculateTotalCost()));
}

private String getDetails(tripplanner.model.ItineraryItem item) {
    if (item instanceof tripplanner.model.Flight) {
        tripplanner.model.Flight f = (tripplanner.model.Flight) item;
        return "Airline: " + f.getAirline() + " | No: " + f.getFlightNumber();
    } else if (item instanceof tripplanner.model.Hotel) {
        tripplanner.model.Hotel h = (tripplanner.model.Hotel) item;
        return "City: " + h.getCity() + " | Nights: " + h.getNumberOfNights();
    } else if (item instanceof tripplanner.model.Activity) {
        tripplanner.model.Activity a = (tripplanner.model.Activity) item;
        return "Location: " + a.getLocation();
    }
    return "";
}

public static void main(String[] args) {
    com.formdev.flatlaf.FlatDarkLaf.setup();
    java.awt.EventQueue.invokeLater(() ->
        new MainDashboard().setVisible(true));
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        themeToggleBtn = new javax.swing.JButton();
        statCard1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        planCountLabel = new javax.swing.JLabel();
        statCard2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        totalCostLabel = new javax.swing.JLabel();
        addBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        summaryBtn = new javax.swing.JButton();
        exportBtn = new javax.swing.JButton();
        importBtn = new javax.swing.JButton();
        tableCard = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tripTable = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 26)); // NOI18N
        titleLabel.setText("âœˆ  Trip Planner");

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jLabel1.setText("Plan flights, hotels and activities");

        themeToggleBtn.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        themeToggleBtn.setText("â˜€  Light Mode ");
        themeToggleBtn.setBorderPainted(false);
        themeToggleBtn.setFocusPainted(false);
        themeToggleBtn.addActionListener(this::themeToggleBtnActionPerformed);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel2.setText("ITINERARY ITEMS");

        planCountLabel.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        planCountLabel.setText("0 plans");

        javax.swing.GroupLayout statCard1Layout = new javax.swing.GroupLayout(statCard1);
        statCard1.setLayout(statCard1Layout);
        statCard1Layout.setHorizontalGroup(
            statCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statCard1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(statCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(planCountLabel))
                .addContainerGap(144, Short.MAX_VALUE))
        );
        statCard1Layout.setVerticalGroup(
            statCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statCard1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(planCountLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel4.setText("ESTIMATED BUDGET");

        totalCostLabel.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        totalCostLabel.setText("$0.00");

        javax.swing.GroupLayout statCard2Layout = new javax.swing.GroupLayout(statCard2);
        statCard2.setLayout(statCard2Layout);
        statCard2Layout.setHorizontalGroup(
            statCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statCard2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(statCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(totalCostLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(132, Short.MAX_VALUE))
        );
        statCard2Layout.setVerticalGroup(
            statCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statCard2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(totalCostLabel)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        addBtn.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        addBtn.setText("ï¼‹  Add Plan");
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(this::addBtnActionPerformed);

        deleteBtn.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        deleteBtn.setText("âœ•  Delete");
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(this::deleteBtnActionPerformed);

        summaryBtn.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        summaryBtn.setText("â˜°  Summary");
        summaryBtn.setBorderPainted(false);
        summaryBtn.setFocusPainted(false);
        summaryBtn.addActionListener(this::summaryBtnActionPerformed);

        exportBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        exportBtn.setText("â†“  Export CSV");
        exportBtn.setBorderPainted(false);
        exportBtn.setFocusPainted(false);
        exportBtn.addActionListener(this::exportBtnActionPerformed);

        importBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        importBtn.setText("â†‘  Import CSV");
        importBtn.setBorderPainted(false);
        importBtn.setFocusPainted(false);
        importBtn.addActionListener(this::importBtnActionPerformed);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel3.setText("Itinerary Overview");

        tripTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Title", "Date", "Cost ($)", "Details "
            }
        ));
        tripTable.setRowHeight(30);
        jScrollPane1.setViewportView(tripTable);

        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel5.setText("â“˜  Select a row and click Delete to remove it");

        javax.swing.GroupLayout tableCardLayout = new javax.swing.GroupLayout(tableCard);
        tableCard.setLayout(tableCardLayout);
        tableCardLayout.setHorizontalGroup(
            tableCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableCardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(tableCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(23, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tableCardLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(193, 193, 193))
        );
        tableCardLayout.setVerticalGroup(
            tableCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableCardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(themeToggleBtn))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(statCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(statCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(tableCard, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(addBtn)
                                                .addGap(26, 26, 26)
                                                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(32, 32, 32)
                                                .addComponent(summaryBtn)
                                                .addGap(33, 33, 33)
                                                .addComponent(exportBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(importBtn)))
                                        .addGap(23, 23, 23)))))
                        .addGap(19, 19, 19))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(themeToggleBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(statCard2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statCard1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(exportBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(importBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(summaryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(tableCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    private void summaryBtnActionPerformed(java.awt.event.ActionEvent evt) {                                           
        java.util.List<String> list = tripManager.getDisplaySummaries();
        if (list.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "No plans yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++)
            sb.append(i + 1).append(". ").append(list.get(i)).append("\n");
            sb.append("\nTotal: $")
            .append(String.format("%.2f", tripManager.calculateTotalCost()));
            javax.swing.JTextArea ta = new javax.swing.JTextArea(sb.toString(), 12, 65);ta.setEditable(false);
            ta.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
            javax.swing.JOptionPane.showMessageDialog(this,new javax.swing.JScrollPane(ta), "Trip Summary",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }                                          

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {                                       
        InputForm form = new InputForm(this, true);
        form.setVisible(true);
        tripplanner.model.ItineraryItem item = form.getCreatedItem();
        if (item != null) {
            tripManager.addItem(item);
            tableModel.addRow(new Object[]{
                item.getType(), item.getTitle(), item.getDate(),
                String.format("%.2f", item.getCost()), getDetails(item)
            });
            refreshStats();
    }
    }                                      

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {                                          
        int row = tripTable.getSelectedRow();
        if (row == -1) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please select a row first.", "No Selection",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = javax.swing.JOptionPane.showConfirmDialog(this,
            "Delete this item?", "Confirm",
            javax.swing.JOptionPane.YES_NO_OPTION);
        if (ok == javax.swing.JOptionPane.YES_OPTION) {
            tripManager.removeItem(row);
            tableModel.removeRow(row);
            refreshStats();
        }
    }                                         

    private void exportBtnActionPerformed(java.awt.event.ActionEvent evt) {                                          
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setSelectedFile(new java.io.File("trips.csv"));
        if (fc.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
        String path = fc.getSelectedFile().getAbsolutePath();
        if (!path.endsWith(".csv")) path += ".csv";
        try {
            tripStorage.saveToCSV(tripManager.getItems(), path);
            javax.swing.JOptionPane.showMessageDialog(this,
                "Exported to:\n" + path);
        } catch (java.io.IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Export failed:\n" + ex.getMessage());
        }
    }
    }                                         

    private void importBtnActionPerformed(java.awt.event.ActionEvent evt) {                                          
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        if (fc.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
        try {
            java.util.List<tripplanner.model.ItineraryItem> loaded =
                tripStorage.loadFromCSV(
                    fc.getSelectedFile().getAbsolutePath());
            tripManager.setItems(loaded);
            tableModel.setRowCount(0);
            for (tripplanner.model.ItineraryItem item : loaded) {
                tableModel.addRow(new Object[]{
                    item.getType(), item.getTitle(), item.getDate(),
                    String.format("%.2f", item.getCost()), getDetails(item)
                });
            }
            refreshStats();
            javax.swing.JOptionPane.showMessageDialog(this,
                loaded.size() + " item(s) imported.");
        } catch (java.io.IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Import failed:\n" + ex.getMessage());
        }
    }
    }                                         

    private void themeToggleBtnActionPerformed(java.awt.event.ActionEvent evt) {                                               
        try {
        if (themeToggleBtn.getText().contains("Light")) {
            com.formdev.flatlaf.FlatLightLaf.setup();
            themeToggleBtn.setText("ðŸŒ™  Dark Mode");
        } else {
            com.formdev.flatlaf.FlatDarkLaf.setup();
            themeToggleBtn.setText("â˜€  Light Mode");
        }
        javax.swing.SwingUtilities.updateComponentTreeUI(this);
        }catch (Exception ex) { ex.printStackTrace(); }
    }                                              

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify                     
    private javax.swing.JButton addBtn;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JButton exportBtn;
    private javax.swing.JButton importBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel planCountLabel;
    private javax.swing.JPanel statCard1;
    private javax.swing.JPanel statCard2;
    private javax.swing.JButton summaryBtn;
    private javax.swing.JPanel tableCard;
    private javax.swing.JButton themeToggleBtn;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel totalCostLabel;
    private javax.swing.JTable tripTable;
    // End of variables declaration                   
}
