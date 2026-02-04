package com.subtrack.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.subtrack.DAO.BillDAO;
import com.subtrack.models.Bill;
import com.subtrack.ui.utils.UIUtils;

public class BillPanel {
    private static JPanel contentWrapper;
    private static CardLayout contentLayout;

    // Add form components
    private static JTextField tfServiceType, tfReference, tfBillDate, tfAmount, tfDueDate,   tfOverdueChargePerDay, tfPaidDate;
    private static JTextArea tfDescription;
    private static JCheckBox cbIsPaid;
    private static JPanel addFormPanel;

    // List components
    private static JPanel listPanel;
    private static DefaultTableModel listModel;
    private static JTable table;
    private static JTextField tfSearch;
    private static JComboBox<String> cbSearchType;

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
        JLabel headerLabel = new JLabel("Bills");
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
        JButton btnAdd = UIUtils.createMenuButton("Add Bill");
        btnAdd.setPreferredSize(new Dimension(150, 40));
        JButton btnList = UIUtils.createMenuButton("Manage Bills");
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
        // Wrap form in a titled border for visual grouping
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
        
        JLabel lblServiceTypLabel = new JLabel("Service Type:"); 
        lblServiceTypLabel.setForeground(UIUtils.TEXT_COLOR);
        lblServiceTypLabel.setFont(UIUtils.SET_FONT());
        tfServiceType = new JTextField(20);
        tfServiceType.setFont(UIUtils.SET_FONT());
        gbc.gridx = 0; gbc.gridy = 0; form.add(lblServiceTypLabel, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfServiceType, gbc);

        JLabel lblReferenceNumber = new JLabel("Reference Number:"); lblReferenceNumber.setForeground(UIUtils.TEXT_COLOR);
        lblReferenceNumber.setFont(UIUtils.SET_FONT());
        tfReference = new JTextField(20);
        tfReference.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 1; form.add(lblReferenceNumber, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfReference, gbc);

        JLabel lblBillDate = new JLabel("Bill Date (YYYY-MM-DD):"); 
        lblBillDate.setForeground(UIUtils.TEXT_COLOR);
        lblBillDate.setFont(UIUtils.SET_FONT());
        tfBillDate = new JTextField(20);
        tfBillDate.setFont(UIUtils.SET_FONT());
        //dpBillDate.setEditable(false); 
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 2; form.add(lblBillDate, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfBillDate, gbc);

        JLabel lblAmount = new JLabel("Amount"); 
        lblAmount.setForeground(UIUtils.TEXT_COLOR);
        lblAmount.setFont(UIUtils.SET_FONT());
        tfAmount = new JTextField(20);
        tfAmount.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 3; form.add(lblAmount, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfAmount, gbc);

        JLabel lblDueDate = new JLabel("Due Date (YYYY-MM-DD):"); 
        lblDueDate.setForeground(UIUtils.TEXT_COLOR);
        lblDueDate.setFont(UIUtils.SET_FONT());
        tfDueDate = new JTextField(20);
        tfDueDate.setFont(UIUtils.SET_FONT());
        //dbDueDate.setEditable(false); 
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 4; form.add(lblDueDate, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfDueDate, gbc);

        cbIsPaid = new JCheckBox("Is Paid?"); cbIsPaid.setForeground(UIUtils.TEXT_COLOR); 
        cbIsPaid.setFont(UIUtils.SET_FONT());
        cbIsPaid.setBackground(UIUtils.BACKGROUND_COLOR);
        cbIsPaid.setFocusable(false);
        tfPaidDate = new JTextField(20);
        tfPaidDate.setFont(UIUtils.SET_FONT());
        tfPaidDate.setEnabled(false);
        JPanel paidDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        paidDatePanel.setBackground(UIUtils.BACKGROUND_COLOR);
        paidDatePanel.add(cbIsPaid);
        paidDatePanel.add(Box.createHorizontalStrut(10));
        paidDatePanel.add(tfPaidDate);
        JLabel lblPaidDate = new JLabel("Paid On:"); 
        lblPaidDate.setForeground(UIUtils.TEXT_COLOR);
        lblPaidDate.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 5; form.add(lblPaidDate, gbc);
        gbc.gridx = 1; form.add(paidDatePanel, gbc);

        JLabel lblOverdueChargePerDay = new JLabel("Overdue Charge PerDay:"); 
        lblOverdueChargePerDay.setForeground(UIUtils.TEXT_COLOR);
        lblOverdueChargePerDay.setFont(UIUtils.SET_FONT());
        tfOverdueChargePerDay = new JTextField(20);
        tfOverdueChargePerDay.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 6; form.add(lblOverdueChargePerDay, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfOverdueChargePerDay, gbc);

        JLabel lblDescription = new JLabel("Description:"); 
        lblDescription.setForeground(UIUtils.TEXT_COLOR);
        lblDescription.setFont(UIUtils.SET_FONT());
        tfDescription = new JTextArea(5, 20);
        tfDescription.setFont(UIUtils.SET_FONT());
        gbc.weightx = 0.2; gbc.gridx = 0; gbc.gridy = 7; form.add(lblDescription, gbc);
        gbc.weightx = 0.8; gbc.gridx = 1; form.add(tfDescription, gbc);

        // Save button centered
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnSave = UIUtils.createMenuButton("Save bILL"); 
        btnSave.setPreferredSize(new Dimension(220, 45));
        btnSave.setFont(UIUtils.SET_FONT());
        form.add(btnSave, gbc);

        cbIsPaid.addActionListener(e -> {
            if (cbIsPaid.isSelected()) {
                tfPaidDate.setEnabled(true);
            } else {
                tfPaidDate.setEnabled(false);
            }
        });

        // Assemble
        formWrapper.add(form, BorderLayout.CENTER);
        addFormPanel.add(formWrapper, BorderLayout.NORTH);
        addFormPanel.add(Box.createVerticalStrut(20), BorderLayout.CENTER); // Add some vertical space

        btnSave.addActionListener(e -> {
            if (saveBill()) {
                JOptionPane.showMessageDialog(addFormPanel, "Bill saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(addFormPanel, "Failed to save bill.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    private static boolean saveBill() {
        String serviceType = tfServiceType.getText().trim();
        String referenceNumber = tfReference.getText().trim();
        LocalDate billDate = LocalDate.parse(tfBillDate.getText().trim());
        Double amount = Double.parseDouble(tfAmount.getText().trim());
        LocalDate dueDate = LocalDate.parse(tfDueDate.getText().trim());
        LocalDate paidDate = cbIsPaid.isSelected() ? LocalDate.parse(tfPaidDate.getText().trim()) : null;
        boolean isPaid = cbIsPaid.isSelected();
        Double overdueChargePerDay = Double.parseDouble(tfOverdueChargePerDay.getText().trim());
        String description = tfDescription.getText().trim();

        if (serviceType.isEmpty() || referenceNumber.isEmpty() || billDate == null || amount == null || dueDate == null) {
            JOptionPane.showMessageDialog(null, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        BillDAO checkDAO = new BillDAO();
        List<Bill> bills = checkDAO.findAll();
        for (Bill bill : bills) {
            if (bill.getServiceType().equalsIgnoreCase(serviceType) && bill.getReferenceNumber().equalsIgnoreCase(referenceNumber)) {
            JOptionPane.showMessageDialog(null, "A bill with same reference number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
            }
        }
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Amount must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (dueDate.isBefore(billDate)) {
            JOptionPane.showMessageDialog(null, "Due date must be after bill date", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (overdueChargePerDay < 0) {
            JOptionPane.showMessageDialog(null, "Overdue charge must be greater than or equal to 0", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Bill bill = new Bill(serviceType, referenceNumber, billDate, amount, dueDate, paidDate, isPaid, overdueChargePerDay, description);
        //if(isPaid){}
        BillDAO dao = new BillDAO();
        dao.add(bill);
        clearAddFields();
        refreshList();
        contentLayout.show(contentWrapper, "list");
        return true;
    }

    private static void initListPanel() {
        listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        listPanel.setBorder(new EmptyBorder(20,20,20,20));

        // Wrap list in titled border
        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        listWrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIUtils.TEXT_COLOR),
            "Bills List",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            listPanel.getFont().deriveFont(Font.BOLD, 20f),
            UIUtils.TEXT_COLOR
        ));

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        JLabel fltr = new JLabel("Filter:"); fltr.setForeground(UIUtils.TEXT_COLOR);
        fltr.setFont(UIUtils.SET_FONT());
        fltr.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(fltr);
        searchPanel.add(Box.createHorizontalStrut(8));
        String[] opts = {"All","Service","Reference No.","Bill Date","Amount","Paid Date","Is Paid"};
        cbSearchType = new JComboBox<>(opts);
        cbSearchType.setFont(UIUtils.SET_FONT());
        cbSearchType.setBackground(UIUtils.PRIMARY);
        cbSearchType.setForeground(UIUtils.TEXT_COLOR);
        cbSearchType.setFocusable(false);
        cbSearchType.setMaximumSize(new Dimension(150, 28));
        searchPanel.add(cbSearchType);
        // Search 
        searchPanel.add(Box.createHorizontalStrut(10));
        tfSearch = new JTextField(15);
        tfSearch.setFont(UIUtils.SET_FONT());
        tfSearch.setPreferredSize(new Dimension(150,26));
        tfSearch.setMaximumSize(new Dimension(150, 26));
        searchPanel.add(tfSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        JButton btnSearch = UIUtils.createMenuButton("Search");
        btnSearch.setPreferredSize(new Dimension(100, 25));
        btnSearch.setMaximumSize(new Dimension(100, 25));
        btnSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
        btnSearch.addActionListener(e -> doSearch());
        JButton btnReset = UIUtils.createMenuButton("Reset");
        btnReset.setPreferredSize(new Dimension(100, 25));
        btnReset.setMaximumSize(new Dimension(100, 25));
        btnReset.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        btnReset.addActionListener(e -> {
            tfSearch.setText("");
            refreshList();
        });
        searchPanel.add(btnSearch);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(btnReset);

        listWrapper.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID","Service","Reference","Bill Date","Amt","Due","Paid Date","Paid", "Charge", "Desc"};
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
        int[] widths = {50, 100, 120, 60, 70, 80, 60, 90, 90, 150};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        listWrapper.add(scroll, BorderLayout.CENTER);

        // Delete & Auto-Pay toggle panel
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(UIUtils.BACKGROUND_COLOR);


        JButton btnDel = UIUtils.createMenuButton("Delete Selected");
        btnDel.addActionListener(e->{
            int r=table.getSelectedRow();
            if(r<0) return;
            int id=(int)listModel.getValueAt(r,0);
            new BillDAO().deleteById(id);
            refreshList();
        });
        bottom.add(btnDel);

        listWrapper.add(bottom, BorderLayout.SOUTH);

        listPanel.add(listWrapper, BorderLayout.CENTER);
    }

    private static void doSearch() {
        String type = switch (cbSearchType.getSelectedIndex()) {
            case 1 -> "service_type";
            case 2 -> "reference_number";
            case 3 -> "bill_date";
            case 4 -> "amount";
            case 5 -> "paid_date";
            case 6 -> "is_paid";
            default -> throw new IllegalStateException("Unexpected value: " + cbSearchType.getSelectedIndex());
        };
        String q = tfSearch.getText().trim();
        List<Bill> results;
        BillDAO dao = new BillDAO();
        results = dao.findByField(type, q);
        listModel.setRowCount(0);
        for (Bill bill : results) {
            listModel.addRow(new Object[]{
                bill.getBillId(), bill.getServiceType(), bill.getReferenceNumber(), bill.getBillDate(),
                bill.getAmount(), bill.getDueDate(), bill.getPaidDate(), bill.isPaid(), bill.getDescription()
            });
        }
    }

    private static void refreshList() {
        listModel.setRowCount(0);
        List<Bill> bills = new BillDAO().findAll();
        for(Bill b: bills) {
            listModel.addRow(new Object[]{
                b.getBillId(), b.getServiceType(), b.getReferenceNumber(), b.getBillDate(),b.getAmount(), b.getDueDate(), b.getPaidDate(), b.isPaid(), b.getOverdueChargePerDay(), b.getDescription()
            });
        }
    }

    private static void clearAddFields() {
        tfServiceType.setText(""); 
        tfReference.setText("");
        tfOverdueChargePerDay.setText("");
        cbIsPaid.setSelected(false);
        tfBillDate.setText("");
        tfDueDate.setText("");
        tfAmount.setText(""); 
        tfDescription.setText("");
    }
}
