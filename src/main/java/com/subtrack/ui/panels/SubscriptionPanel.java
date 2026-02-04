package com.subtrack.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.subtrack.DAO.PaymentDAO;
import com.subtrack.DAO.SubscriptionDAO;
import com.subtrack.models.Payment;
import com.subtrack.models.Subscription;
import com.subtrack.service.SubscriptionRenewalService;
import com.subtrack.ui.utils.UIUtils;

public class SubscriptionPanel {
    private static JPanel contentWrapper;
    private static CardLayout contentLayout;

    // Add form components
    private static JTextField tfPlat, tfName, tfCost, tfDur, tfStart, tfNext;
    private static JCheckBox chkAuto;
    private static JComboBox<String> cbCycle;
    private static JPanel addFormPanel;

    // List components
    private static JPanel listPanel;
    private static DefaultTableModel listModel;
    private static JTable table;

    public static JPanel build(JPanel mainPanel, JFrame frame) {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        // Header and sub-menu
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        //topWrapper.add(Box.createVerticalStrut(40));
        topWrapper.add(UIUtils.createWindowControl(frame));

        // Header bar
        JPanel headerBar = new JPanel();
        headerBar.setBackground(UIUtils.PRIMARY_LIGHT);
        headerBar.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerBar.setLayout(new BoxLayout(headerBar, BoxLayout.X_AXIS));
        headerBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel headerLabel = new JLabel("Subscriptions");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 26f));
        headerLabel.setForeground(UIUtils.TEXT_COLOR);
        headerBar.add(headerLabel);
        headerBar.add(Box.createHorizontalGlue());
        JButton settingsBtn = new JButton(new ImageIcon("src/main/resources/images/settings-icon.png"));
        settingsBtn.setBackground(UIUtils.PRIMARY_LIGHT);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        headerBar.add(settingsBtn);
        topWrapper.add(headerBar);

        // Menu bar
        JPanel menuBar = new JPanel();
        menuBar.setBackground(UIUtils.PRIMARY);
        menuBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));
        menuBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JButton btnAdd = UIUtils.createMenuButton("Add Subscription");
        btnAdd.setPreferredSize(new Dimension(150, 40));
        JButton btnList = UIUtils.createMenuButton("Manage Subscriptions");
        btnList.setPreferredSize(new Dimension(150, 40));
        menuBar.add(btnAdd);
        menuBar.add(Box.createHorizontalStrut(10));
        menuBar.add(btnList);
        topWrapper.add(menuBar);

        // Content wrapper
        contentWrapper = new JPanel();
        contentLayout = new CardLayout();
        contentWrapper.setLayout(contentLayout);
        contentWrapper.setBackground(UIUtils.BACKGROUND_COLOR);

        initAddPanel();
        initListPanel();
        contentWrapper.add(listPanel, "list");
        contentWrapper.add(addFormPanel, "add");

        btnList.addActionListener(e -> {
            refreshList();
            contentLayout.show(contentWrapper, "list");
        });
        btnAdd.addActionListener(e -> contentLayout.show(contentWrapper, "add"));

        mainPanel.add(topWrapper, BorderLayout.NORTH);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        // Default to list view
        refreshList();
        contentLayout.show(contentWrapper, "list");
        mainPanel.revalidate();
        mainPanel.repaint();
        return mainPanel;
    }

    private static void initAddPanel() {
        addFormPanel = new JPanel(new BorderLayout());
        addFormPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        addFormPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        formWrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIUtils.TEXT_COLOR),
            "Add New Subscription",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 20),
            UIUtils.TEXT_COLOR
        ));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.weightx = 0.2;
        JLabel lblPlat = new JLabel("Platform:"); 
        lblPlat.setForeground(UIUtils.TEXT_COLOR);
        lblPlat.setFont(UIUtils.SET_FONT());
        tfPlat = new JTextField(20);
        tfPlat.setFont(UIUtils.SET_FONT());
        gbc.gridx = 0; gbc.gridy = 0; form.add(lblPlat, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfPlat, gbc);

        JLabel lblName = new JLabel("Subscription Name/Catagory:"); lblName.setForeground(UIUtils.TEXT_COLOR);
        lblName.setFont(UIUtils.SET_FONT());
        tfName = new JTextField(20);
        tfName.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 1; form.add(lblName, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfName, gbc);

        JLabel lblCost = new JLabel("Cost(including taxes):"); lblCost.setForeground(UIUtils.TEXT_COLOR);
        lblCost.setFont(UIUtils.SET_FONT());
        tfCost = new JTextField(20);
        tfCost.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 2; form.add(lblCost, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfCost, gbc);
 
        chkAuto = new JCheckBox("Auto Pay"); chkAuto.setForeground(UIUtils.TEXT_COLOR); 
        chkAuto.setFont(UIUtils.SET_FONT());
        chkAuto.setBackground(UIUtils.BACKGROUND_COLOR);
        chkAuto.setFocusable(false);
        cbCycle = new JComboBox<>(new String[]{"None","Daily","Weekly","Monthly","Quarterly","Yearly"});
        cbCycle.setFont(UIUtils.SET_FONT());
        cbCycle.setEnabled(false);
        cbCycle.setForeground(UIUtils.TEXT_COLOR);
        cbCycle.setBackground(UIUtils.PRIMARY);
        cbCycle.setFocusable(false);
        JPanel autoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        autoPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        autoPanel.add(chkAuto);
        autoPanel.add(Box.createHorizontalStrut(10));
        autoPanel.add(cbCycle);
        JLabel billingcycle = new JLabel("Billing Cycle:"); 
        billingcycle.setForeground(UIUtils.TEXT_COLOR);
        billingcycle.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 3; form.add(billingcycle, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(autoPanel, gbc);

        JLabel lblDur = new JLabel("Duration (days):"); lblDur.setForeground(UIUtils.TEXT_COLOR);
        lblDur.setFont(UIUtils.SET_FONT());
        tfDur = new JTextField(20);
        tfDur.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 4; form.add(lblDur, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfDur, gbc);

        JLabel lblStart = new JLabel("Start Date (YYYY-MM-DD):"); lblStart.setForeground(UIUtils.TEXT_COLOR);
        lblStart.setFont(UIUtils.SET_FONT());
        tfStart = new JTextField(20);
        tfStart.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 5; form.add(lblStart, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfStart, gbc);

        JLabel lblNext = new JLabel("Next Due Date:"); lblNext.setForeground(UIUtils.TEXT_COLOR);
        lblNext.setFont(UIUtils.SET_FONT());
        tfNext = new JTextField("Auto generated...",20); 
        tfNext.setFocusable(true);
        tfNext.setEnabled(false);
        tfNext.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 6; form.add(lblNext, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfNext, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnSave = UIUtils.createMenuButton("Save Subscription"); btnSave.setPreferredSize(new Dimension(220, 45));
        btnSave.setFont(UIUtils.SET_FONT());
        form.add(btnSave, gbc);

        chkAuto.addActionListener(e -> {
            boolean auto = chkAuto.isSelected();
            cbCycle.setEnabled(auto);
            recalcNext();
        });
        cbCycle.addActionListener(e -> {
            if (!chkAuto.isSelected()) return;
            int days = switch ((String) cbCycle.getSelectedItem()) {
                case "Daily" -> 1;
                case "Weekly" -> 7;
                case "Monthly" -> 30;
                case "Quarterly" -> 90;
                case "Yearly" -> 365;
                default -> 0;
            };
            tfDur.setEnabled(false);
            if(days > 0) {
                tfDur.setText(String.valueOf(days));
            } else {
                tfDur.setText("");
                tfDur.setEnabled(true);
            }
            recalcNext();
        });
        tfStart.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { recalcNext(); }
            @Override
            public void focusGained(FocusEvent e) {
                tfStart.setText(LocalDate.now().toString());
            }
        });

        formWrapper.add(form, BorderLayout.CENTER);
        addFormPanel.add(formWrapper, BorderLayout.NORTH);

        btnSave.addActionListener(e -> {
            if(saveSubscription()) {
                // Clear all fields after saving
                tfPlat.setText("");
                tfName.setText("");
                tfCost.setText("");
                tfDur.setText("");
                tfStart.setText("");
                tfNext.setText("Auto generated...");
                chkAuto.setSelected(false);
                cbCycle.setSelectedIndex(0);
                cbCycle.setEnabled(false);
            }
        });
    }

    private static void initListPanel() {
        listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        listPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Wrap list in titled border
        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        listWrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIUtils.TEXT_COLOR),
            "Subscriptions List",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            listPanel.getFont().deriveFont(Font.BOLD, 20f),
            UIUtils.TEXT_COLOR
        ));

        // Search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        JLabel fltr = new JLabel("Filter:"); fltr.setForeground(UIUtils.TEXT_COLOR);
        fltr.setFont(UIUtils.SET_FONT());
        fltr.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(fltr);
        searchPanel.add(Box.createHorizontalStrut(8));
        String[] opts = {"All","Platform","Name","Next Due Date","Active"};
        JComboBox<String> cbType = new JComboBox<>(opts);
        cbType.setFont(UIUtils.SET_FONT());
        cbType.setBackground(UIUtils.PRIMARY);
        cbType.setForeground(UIUtils.TEXT_COLOR);
        cbType.setFocusable(false);
        cbType.setMaximumSize(new Dimension(150, 28));
        
        searchPanel.add(cbType);
        searchPanel.add(Box.createHorizontalStrut(10));
        JTextField tfSearch = new JTextField(15);
        tfSearch.setFont(UIUtils.SET_FONT());
        tfSearch.setPreferredSize(new Dimension(150,26));
        tfSearch.setMaximumSize(new Dimension(150, 26));
        searchPanel.add(tfSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        JButton btnSearch = UIUtils.createMenuButton("Search");
        btnSearch.setPreferredSize(new Dimension(100, 25));
        btnSearch.setMaximumSize(new Dimension(100, 25));
        btnSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton btnReset = UIUtils.createMenuButton("Reset");
        btnReset.setPreferredSize(new Dimension(100, 25));
        btnReset.setMaximumSize(new Dimension(100, 25));
        btnReset.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(btnSearch);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(btnReset);

        listWrapper.add(searchPanel, BorderLayout.NORTH);

        // Table setup
        String[] cols = {"ID","Platform","Name","Cost","Auto Pay","Cycle","Duration","Start Date","Next Due","Active"};
        listModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(listModel);
        // Styling
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(UIUtils.PRIMARY);
        table.setBackground(UIUtils.BACKGROUND_COLOR);
        table.setForeground(UIUtils.TEXT_COLOR);
        table.setFont(UIUtils.SET_FONT());
        table.setSelectionBackground(UIUtils.PRIMARY);
        table.setSelectionForeground(UIUtils.TEXT_COLOR);
        table.setFocusable(false);
        // Header style
        JTableHeader header = table.getTableHeader();
        header.setBackground(UIUtils.PRIMARY_LIGHT);
        header.setForeground(UIUtils.TEXT_COLOR);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        header.setReorderingAllowed(false);
        // Center first column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        // Adjust column widths
        int[] widths = {50, 100, 120, 60, 70, 80, 60, 90, 90, 60};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        listWrapper.add(scroll, BorderLayout.CENTER);

        // Delete & Auto-Pay toggle panel
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(UIUtils.BACKGROUND_COLOR);

        // Delete Selected button
        JButton btnDelete = UIUtils.createMenuButton("Delete Selected");
        //btnDelete.setPreferredSize(new Dimension(120, 25));
        bottom.add(btnDelete);

        // Turn Off Auto-Pay button (initially disabled)
        JButton btnToggleAuto = UIUtils.createMenuButton("Turn Auto-Pay");
        //btnToggleAuto.setPreferredSize(new Dimension(150, 25));
        btnToggleAuto.setEnabled(false);
        btnToggleAuto.setFocusable(false);
        bottom.add(Box.createHorizontalStrut(5));
        bottom.add(btnToggleAuto);

        listWrapper.add(bottom, BorderLayout.SOUTH);

        listPanel.add(listWrapper, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0 || !(boolean) listModel.getValueAt(row, 9)) {
                btnToggleAuto.setText("Turn Auto-Pay");
                btnToggleAuto.setEnabled(false);
            } else {
                boolean isAuto = (boolean) listModel.getValueAt(row, 4);
                btnToggleAuto.setText(isAuto  ? "Turn Auto-Pay Off" : "Turn Auto-Pay On");
                btnToggleAuto.setEnabled(true);
            }
        });


        // Actions (as before)
        btnSearch.addActionListener(e -> {
            List<Subscription> results = new ArrayList<>();
            try {
                String type = (String) cbType.getSelectedItem();
                String q = tfSearch.getText().trim();
                SubscriptionDAO dao = new SubscriptionDAO();
                switch (type) {
                    case "Platform":
                        results.addAll(dao.findByPlatform(q));
                        break;
                    case "Name":
                        results.addAll(dao.findByName(q));
                        break;
                    case "Next Due Date":
                        LocalDate ld = LocalDate.parse(q);
                        Date d = Date.valueOf(ld);
                        results.addAll(dao.findByNextDueDate(d));
                        break;
                    case "Active":
                        boolean active = Boolean.parseBoolean(q);
                        results.addAll(dao.findByIsActive(active));
                        break;
                    default:
                        results = dao.findAll();
                }
                // repopulate
                listModel.setRowCount(0);
                for (Subscription s : results) {
                    listModel.addRow(new Object[]{
                        s.getID(), s.getPlatform(), s.getName(), s.getCost(),
                        s.isAutoPay(), s.getBillingCycle(), s.getDuration(),
                        s.getStartDate(), s.getNextDueDate(), s.isActive()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(listPanel,
                    "Search error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnToggleAuto.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
        
            // Pull data out of the model, casting date columns to LocalDate directly:
            Subscription s = new Subscription(
                (String)    listModel.getValueAt(row, 1),
                (String)    listModel.getValueAt(row, 2),
                (double)    listModel.getValueAt(row, 3),
                (boolean)   listModel.getValueAt(row, 4),
                (String)    listModel.getValueAt(row, 5),
                (int)       listModel.getValueAt(row, 6),
                (LocalDate) listModel.getValueAt(row, 7),
                (LocalDate) listModel.getValueAt(row, 8),
                (boolean)   listModel.getValueAt(row, 9)
            );
            s.setID((int) listModel.getValueAt(row, 0));
        
            if(s.isAutoPay()){
                int confirm = JOptionPane.showConfirmDialog(
                    listPanel,
                    "Turn Off Auto-pay for subscription ID " + s.getID() + "?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        s.setAutoPay(false);
                        s.setBillingCycle("None");
                        new SubscriptionDAO().update(s);
                        JOptionPane.showMessageDialog(
                            listPanel,
                            "Auto-Pay turned off successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        refreshList();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(
                            listPanel,
                            "Invalid number entered.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } else {
                int confirm = JOptionPane.showConfirmDialog(
                    listPanel,
                    "Turn On Auto-pay for subscription ID " + s.getID() + "?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        s.setAutoPay(true);
                        new SubscriptionDAO().update(s);
                        JOptionPane.showMessageDialog(
                            listPanel,
                            "Auto-Pay turned on successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        refreshList();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(
                            listPanel,
                            "Invalid number entered.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
        
        
        btnReset.addActionListener(e -> {
            tfSearch.setText("");
            cbType.setSelectedIndex(0);
            refreshList();
        });
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(listPanel,
                    "Please select a row to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) listModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(
                listPanel,
                "Delete subscription ID " + id + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new SubscriptionDAO().deleteByID(id);
                listModel.removeRow(row);
                JOptionPane.showMessageDialog(listPanel,
                    "Deleted successfully.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private static void refreshList() {
        listModel.setRowCount(0);
        for (Subscription s : new SubscriptionDAO().findAll()) {
            listModel.addRow(new Object[]{
                s.getID(), s.getPlatform(), s.getName(), s.getCost(),
                s.isAutoPay(), s.getBillingCycle(), s.getDuration(),
                s.getStartDate(), s.getNextDueDate(), s.isActive()
            });
        }
    }

    private static void recalcNext() {
        try {
            LocalDate st = LocalDate.parse(tfStart.getText().trim());
            int d = Integer.parseInt(tfDur.getText().trim());
            tfNext.setText(st.plusDays(d).toString());
        } catch (Exception ignored) {}
    }

    private static boolean saveSubscription() {
        try {
            String plat = tfPlat.getText().trim();
            String name = tfName.getText().trim();
            double cost = Double.parseDouble(tfCost.getText().trim());
            boolean auto = chkAuto.isSelected();
            String cycle = auto ? (String)cbCycle.getSelectedItem() : "None";
            int dur = Integer.parseInt(tfDur.getText().trim());
            LocalDate stDate = LocalDate.parse(tfStart.getText().trim());
            LocalDate nxtDate = LocalDate.parse(tfNext.getText().trim());

            if (plat.isEmpty() || name.isEmpty() || cost <= 0 || dur <= 0 || stDate == null) {
                JOptionPane.showMessageDialog(addFormPanel,
                    "All fields are required and must be valid.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            } 
            else if (stDate.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(addFormPanel,
                    "Start date cannot be in the future.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }

            SubscriptionDAO dao = new SubscriptionDAO();
            Subscription newSub = new Subscription(
                plat, name, cost, auto, cycle, dur, stDate, nxtDate
            );

            List<Subscription> matches = dao.findByName(name);
            for (Subscription existing : matches) {
                boolean samePlatform = existing.getPlatform().equals(plat);
                boolean sameActive = existing.isActive() == true;

                if (samePlatform && sameActive) {
                    JOptionPane.showMessageDialog(addFormPanel,
                        "This subscription already exists.",
                        "Duplicate",
                        JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
            }
            Payment payment = PaymentPanel.showAddPaymentDialog(
                SwingUtilities.getWindowAncestor(addFormPanel),
                newSub
            );

            // If user cancelled
            if (payment == null) {
                return false;
            }
            new PaymentDAO().add(payment);
            new SubscriptionDAO().add(newSub);

            if (!auto) {
                JOptionPane.showMessageDialog(addFormPanel,
                    "Subscription and payment saved!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(addFormPanel,
                    "Subscription saved!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                SubscriptionRenewalService.processAutoPayments();
            }

            SubscriptionRenewalService.processSubscriptionsRenewal();
            refreshList();
            contentLayout.show(contentWrapper, "list");
            return true;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(addFormPanel,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
