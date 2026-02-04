package com.subtrack.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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

public class PaymentPanel {
    private static JPanel contentWrapper;
    private static CardLayout contentLayout;

    // Add form components
    private static JComboBox<String> cbPlatform;
    private static JComboBox<String> cbSubscription;
    private static JTextField tfAmount;
    private static JTextField tfDate;
    private static JPanel addFormPanel;

    // List components
    private static JPanel listPanel;
    private static DefaultTableModel listModel;
    private static JTable table;

    // Search & Sort components
    private static JComboBox<String> cbFilterType;
    private static JTextField tfFilter;
    private static JComboBox<String> cbSortField;
    private static JComboBox<String> cbSortOrder;

    public static JPanel build(JPanel mainPanel, JFrame frame) {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        // Header and sub-menu
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        topWrapper.add(UIUtils.createWindowControl(frame));

        // Header bar
        JPanel headerBar = new JPanel();
        headerBar.setBackground(UIUtils.PRIMARY_LIGHT);
        headerBar.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerBar.setLayout(new BoxLayout(headerBar, BoxLayout.X_AXIS));
        headerBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel headerLabel = new JLabel("Payments");
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
        JButton btnAdd = UIUtils.createMenuButton("Add Payment");
        btnAdd.setPreferredSize(new Dimension(150, 40));
        JButton btnList = UIUtils.createMenuButton("Manage Payments");
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

        refreshList();
        contentLayout.show(contentWrapper, "list");
        mainPanel.revalidate();
        mainPanel.repaint();
        return mainPanel;
    }

    private static void createAddDropdowns(){
        SubscriptionDAO subDao = new SubscriptionDAO();
        List<Subscription> active = subDao.findByIsActive(true);
        LocalDate today = LocalDate.now();
        List<Subscription> eligible = new ArrayList<>();
        for (Subscription s : active){
            LocalDate startWindow = s.getNextDueDate().minusDays(s.getDuration());
            LocalDate endWindow = s.getNextDueDate();
            if(!today.isBefore(startWindow) && !today.isAfter(endWindow) && !s.isAutoPay()){
                eligible.add(s);
            }
        }

        //Dropdowns
        java.util.Set<String> platforms = new java.util.LinkedHashSet<>();  //prevents duplicates
        platforms.add("None");
        for(Subscription s : eligible){
            platforms.add(s.getPlatform());
        }
        cbPlatform = new JComboBox<>(platforms.toArray(new  String[0]));
        cbPlatform.setSelectedItem("None");
        cbPlatform.setFont(UIUtils.SET_FONT());
        cbPlatform.setBackground(UIUtils.PRIMARY);
        cbPlatform.setForeground(UIUtils.TEXT_COLOR);
        cbPlatform.setFocusable(false);

        cbSubscription = new JComboBox<>();
        cbSubscription.setFont(UIUtils.SET_FONT());
        cbSubscription.setBackground(UIUtils.PRIMARY);
        cbSubscription.setForeground(UIUtils.TEXT_COLOR);
        cbSubscription.setFocusable(false);

        //repopulate box when platform changes
        cbPlatform.addActionListener(e -> {
            String selectedPlat = (String) cbPlatform.getSelectedItem();
            cbSubscription.removeAllItems();
            for(Subscription s : eligible){
                if (s.getPlatform().equals(selectedPlat)) {
                    cbSubscription.addItem(s.getName());
                }
            }
        });
        // initial fill
        if (cbPlatform.getItemCount() > 0) {
            cbPlatform.setSelectedIndex(0);
            cbPlatform.getActionListeners()[0].actionPerformed(null);
        }   
        cbSubscription.addActionListener(e -> {
            String selectedSub = (String) cbSubscription.getSelectedItem();
            for(Subscription s : eligible){
                if (s.getName().equals(selectedSub)) {
                    tfAmount.setText(String.valueOf(s.getCost()));
                }
            }

        });
    }

    private static void initAddPanel() {
        addFormPanel = new JPanel(new BorderLayout());
        addFormPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        addFormPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        createAddDropdowns();
        
        //Form 
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        formWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.TEXT_COLOR),
                "Add New Payment",
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
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        JLabel platformLabel = new JLabel("Platform:");
        platformLabel.setForeground(UIUtils.TEXT_COLOR);
        platformLabel.setFont(UIUtils.SET_FONT());
        form.add(platformLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(cbPlatform, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        JLabel subscriptionLabel = new JLabel("Subscription:");
        subscriptionLabel.setFont(UIUtils.SET_FONT());
        subscriptionLabel.setForeground(UIUtils.TEXT_COLOR);
        form.add(subscriptionLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(cbSubscription, gbc);


        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(UIUtils.SET_FONT());
        amountLabel.setForeground(UIUtils.TEXT_COLOR);
        form.add(amountLabel, gbc);
        tfAmount = new JTextField(20);
        tfAmount.setFont(UIUtils.SET_FONT());
        tfAmount.setEnabled(false);
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(tfAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.2;
        JLabel paydateLabel = new JLabel("Payment Date (YYYY-MM-DD):");
        paydateLabel.setFont(UIUtils.SET_FONT());
        paydateLabel.setForeground(UIUtils.TEXT_COLOR);
        form.add(paydateLabel, gbc);
        tfDate = new JTextField(20);
        tfDate.setFont(UIUtils.SET_FONT());
        tfDate.setText(LocalDate.now().toString());
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(tfDate, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnSave = UIUtils.createMenuButton("Save Payment");
        btnSave.setFont(UIUtils.SET_FONT());
        btnSave.setPreferredSize(new Dimension(200, 45));
        form.add(btnSave, gbc);

        formWrapper.add(form, BorderLayout.CENTER);
        addFormPanel.add(formWrapper, BorderLayout.NORTH);

        btnSave.addActionListener(e -> savePayment());
    }

    private static void initListPanel() {
        listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        listPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        listWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.TEXT_COLOR),
                "Payment History",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                listPanel.getFont().deriveFont(Font.BOLD, 20f),
                UIUtils.TEXT_COLOR
        ));

        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlBar.setBackground(UIUtils.BACKGROUND_COLOR);

        // Filter controls
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(UIUtils.SET_FONT());
        filterLabel.setForeground(UIUtils.TEXT_COLOR);
        controlBar.add(filterLabel);
        cbFilterType = new JComboBox<>(new String[]{"All","Platform","Subscription","Auto-Paid","Payment Date","Renewed"});
        cbFilterType.setFont(UIUtils.SET_FONT());
        controlBar.add(cbFilterType);
        tfFilter = new JTextField(12);
        tfFilter.setFont(UIUtils.SET_FONT());
        controlBar.add(tfFilter);
        JButton btnFilter = UIUtils.createMenuButton("Search");
        controlBar.add(btnFilter);
        btnFilter.setPreferredSize(new Dimension(100, 25));
        btnFilter.setMaximumSize(new Dimension(100, 25));
        JButton btnReset = UIUtils.createMenuButton("Reset");
        btnReset.setPreferredSize(new Dimension(100, 25));
        btnReset.setMaximumSize(new Dimension(100, 25));
        controlBar.add(btnReset);

        // Sort controls
        controlBar.add(Box.createHorizontalStrut(10));
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(UIUtils.SET_FONT());
        sortLabel.setForeground(UIUtils.TEXT_COLOR);
        controlBar.add(sortLabel);
        cbSortField = new JComboBox<>(new String[]{"Amount","Payment Date"});
        cbSortField.setFont(UIUtils.SET_FONT());
        controlBar.add(cbSortField);
        cbSortOrder = new JComboBox<>(new String[]{"Asc","Desc"});
        cbSortOrder.setFont(UIUtils.SET_FONT());
        controlBar.add(cbSortOrder);
        JButton btnSort = UIUtils.createMenuButton("Sort");
        btnSort.setPreferredSize(new Dimension(100, 25));
        btnSort.setMaximumSize(new Dimension(100, 25));
        controlBar.add(btnSort);

        listWrapper.add(controlBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID","Platform","Subscription","Auto-Paid","Amount","Payment Date", "Renewed"};
        listModel = new DefaultTableModel(cols, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(listModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setBackground(UIUtils.BACKGROUND_COLOR);
        table.setForeground(UIUtils.TEXT_COLOR);
        table.setFont(UIUtils.SET_FONT());
        JTableHeader header = table.getTableHeader();
        header.setBackground(UIUtils.PRIMARY_LIGHT);
        header.setForeground(UIUtils.TEXT_COLOR);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        header.setReorderingAllowed(false);

        // Center ID
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        listWrapper.add(scroll, BorderLayout.CENTER);

        // Delete button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UIUtils.BACKGROUND_COLOR);
        JButton btnDelete = UIUtils.createMenuButton("Delete Selected");
        bottom.add(btnDelete);
        listWrapper.add(bottom, BorderLayout.SOUTH);

        listPanel.add(listWrapper, BorderLayout.CENTER);

        refreshList();

        // Action listeners
        btnFilter.addActionListener(e -> applyFilter());
        btnReset.addActionListener(e -> { tfFilter.setText(""); applyFilter(); });
        btnSort.addActionListener(e -> applySort());

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(listPanel, "Select a row to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) listModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(listPanel, "Delete payment ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new PaymentDAO().deleteByID(id);
                listModel.removeRow(row);
            }
        });
    }

    private static void applyFilter() {
        String type = (String) cbFilterType.getSelectedItem();
        String q = tfFilter.getText().trim().toLowerCase();
        listModel.setRowCount(0);
        for (Payment p : new PaymentDAO().findAll()) {
            boolean match = switch(type) {
                case "Platform" -> p.getPlatformName().toLowerCase().contains(q);
                case "Subscription" -> p.getSubscriptionName().toLowerCase().contains(q);
                case "Auto-Paid" -> Boolean.toString(p.isAutoPaid()).equalsIgnoreCase(q);
                case "Payment Date" -> p.getPaymentDate().toString().equals(q);
                case "Renewed" -> Boolean.toString(p.getRenewed()).equalsIgnoreCase(q);
                default -> true;
            };
            if (match) addRow(p);
        }
    }

    private static void applySort() {
        List<Payment> all = new PaymentDAO().findAll();
        String field = (String) cbSortField.getSelectedItem();
        boolean asc = cbSortOrder.getSelectedItem().equals("Asc");
        all.sort((a,b) -> {
            int cmp = 0;
            if ("Amount".equals(field)) cmp = Double.compare(a.getAmount(), b.getAmount());
            else if ("Payment Date".equals(field)) cmp = a.getPaymentDate().compareTo(b.getPaymentDate());
            return asc ? cmp : -cmp;
        });
        listModel.setRowCount(0);
        for (Payment p : all) addRow(p);
    }

    private static void addRow(Payment p) {
        listModel.addRow(new Object[]{
            p.getId(), p.getPlatformName(), p.getSubscriptionName(), p.isAutoPaid(),
            p.getAmount(), p.getPaymentDate(), p.getRenewed()
        });
    }

    private static void refreshList() {
        listModel.setRowCount(0);
        for (Payment p : new PaymentDAO().findAll()) addRow(p);
    }

    private static void savePayment() {
        try {
            String plat = (String) cbPlatform.getSelectedItem();
            String sub = (String) cbSubscription.getSelectedItem();
            boolean auto = false;
            double amt = Double.parseDouble(tfAmount.getText().trim());
            LocalDate dt = LocalDate.parse(tfDate.getText().trim());

            Payment p = new Payment(plat, sub, amt, dt, false);
            new PaymentDAO().add(p);

            JOptionPane.showMessageDialog(addFormPanel, "Payment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            SubscriptionRenewalService.processSubscriptionsRenewal();
            createAddDropdowns();
            refreshList(); contentLayout.show(contentWrapper, "list");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(addFormPanel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Payment showAddPaymentDialog(Window parent, com.subtrack.models.Subscription sub) {
        JDialog dlg = new JDialog(parent, "Add Payment for " + sub.getName(), 
        Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setBackground(UIUtils.BACKGROUND_COLOR.brighter());
        dlg.setSize(400, 300);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.BACKGROUND_COLOR);
        form.setBorder(new EmptyBorder(20,20,20,20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel pltLabel = new JLabel("Platform:");
        pltLabel.setFont(UIUtils.SET_FONT());
        pltLabel.setForeground(UIUtils.TEXT_COLOR);
        gbc.gridx=0; gbc.gridy=0; form.add(pltLabel, gbc);
        gbc.gridx=1; JTextField tfP = new JTextField(sub.getPlatform(),20);
        tfP.setEditable(false); 
        form.add(tfP, gbc);
 
        JLabel subLabel = new JLabel("Subscription Name:");
        subLabel.setFont(UIUtils.SET_FONT());
        subLabel.setForeground(UIUtils.TEXT_COLOR);
        gbc.gridx=0; gbc.gridy=1; form.add(subLabel, gbc);
        gbc.gridx=1; JTextField tfS = new JTextField(sub.getName(),20); tfS.setEditable(false); form.add(tfS, gbc);
       
        JLabel amtLabel = new JLabel("Amount:");
        amtLabel.setFont(UIUtils.SET_FONT());
        amtLabel.setForeground(UIUtils.TEXT_COLOR);
        gbc.gridx=0; gbc.gridy=3; form.add(amtLabel, gbc);
        gbc.gridx=1; JTextField tfA = new JTextField(String.valueOf(sub.getCost()), 20);
        tfA.setEditable(false);
        form.add(tfA, gbc);
        // Date
        JLabel pdLabel = new JLabel("Payment Date (YYYY-MM-DD):");
        pdLabel.setFont(UIUtils.SET_FONT());
        pdLabel.setForeground(UIUtils.TEXT_COLOR);
        gbc.gridx=0; gbc.gridy=4; form.add(pdLabel, gbc);
        gbc.gridx=1; JTextField tfD = new JTextField(String.valueOf(sub.getStartDate()),10); form.add(tfD, gbc);
        tfD.setEnabled(false);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        btns.add(cancel); btns.add(save);
        dlg.add(btns, BorderLayout.SOUTH);

        final Payment[] result = new Payment[1];
        save.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(tfA.getText().trim());
                LocalDate dt = LocalDate.parse(tfD.getText().trim());
                if(!sub.isAutoPay()){
                    result[0] = new Payment(sub.getPlatform(), sub.getName(), amt, dt, true);
                } else {
                    result[0] = new Payment(sub.getPlatform(), sub.getName(), amt, dt, true);
                }
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> dlg.dispose());

        dlg.pack(); dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
        return result[0];
    }
}
