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
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

import com.subtrack.DAO.BillDAO;
import com.subtrack.DAO.BillPaymentDAO;
import com.subtrack.models.Bill;
import com.subtrack.models.BillPayment;
import com.subtrack.service.ServiceRenewal;
import com.subtrack.ui.utils.UIUtils;

public class BillPaymentPanel {
    private static JPanel contentWrapper;
    private static CardLayout contentLayout;

    // Add form components
    private static JComboBox<String> cbService, cbReference;
    private static JTextField tfBillId, tfBillAmount, tfTotalOverdueCharge, tfTotalAmount, tfDate;
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
        JLabel headerLabel = new JLabel("Bill Payments");
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

        // Default to list view
        refreshList();
        contentLayout.show(contentWrapper, "list");
        mainPanel.revalidate();
        mainPanel.repaint();
        return mainPanel;
    }

    private static void createAddDropdowns() {
        // Fetch all unpaid bills
        BillDAO billDao = new BillDAO();
        List<Bill> unpaidBills = billDao.findByField("is_paid", "false"); 

        LinkedHashSet<String> services = new LinkedHashSet<>();
        services.add("None");
        for (Bill b : unpaidBills) {
            services.add(b.getServiceType());
        }

        cbService = new JComboBox<>(services.toArray(new String[0]));
        cbService.setSelectedItem("None");
        cbService.setFont(UIUtils.SET_FONT());
        cbService.setBackground(UIUtils.PRIMARY);
        cbService.setForeground(UIUtils.TEXT_COLOR);
        cbService.setFocusable(false);

        cbReference = new JComboBox<>();
        cbReference.setFont(UIUtils.SET_FONT());
        cbReference.setBackground(UIUtils.PRIMARY);
        cbReference.setForeground(UIUtils.TEXT_COLOR);
        cbReference.setFocusable(false);

        // Whenever service changes, refill references
        cbService.addActionListener(e -> {
            String selectedService = (String) cbService.getSelectedItem();
            cbReference.removeAllItems();

            if (!"None".equals(selectedService)) {
                for (Bill b : unpaidBills) {
                    if (b.getServiceType().equals(selectedService)) {
                        cbReference.addItem(b.getReferenceNumber());
                    }
                }
            }
        });

        // Trigger initial fill
        cbService.getActionListeners()[0].actionPerformed(null);

        // auto-fill fields
        cbReference.addActionListener(e -> {
            String selectedRef = (String) cbReference.getSelectedItem();
            if (selectedRef == null) return;

            for (Bill b : unpaidBills) {
                if (b.getReferenceNumber().equals(selectedRef)) {
                    tfBillId.setText(String.valueOf(b.getBillId()));
                    tfBillAmount.setText(String.format("%.2f", b.getAmount()));
                    double overdue = 0.0;
                    double charge = b.getOverdueChargePerDay();
                    if(LocalDate.now().isAfter(b.getDueDate())){
                        for(LocalDate i = b.getDueDate(); i.isBefore(LocalDate.now()); i = i.plusDays(1)){
                            overdue += charge;
                            if (i.isEqual(LocalDate.now())) {
                                break;
                            }
                        }
                    }
                    tfTotalOverdueCharge.setText(String.format("%.2f", overdue));
                    tfTotalAmount.setText(String.format("%.2f", b.getAmount() + overdue));
                    break;
                }
            }
        });
    }


    private static void initAddPanel() {
        addFormPanel = new JPanel(new BorderLayout());
        addFormPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        addFormPanel.setBorder(new EmptyBorder(20,20,20,20));

        createAddDropdowns();

        //Form 
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        formWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.TEXT_COLOR),
                "Add New Bill Payment",
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
        JLabel servicLabel = new JLabel("Service Type:");
        servicLabel.setForeground(UIUtils.TEXT_COLOR);
        servicLabel.setFont(UIUtils.SET_FONT());
        form.add(servicLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(cbService, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        JLabel referenceNoLabel = new JLabel("Reference(Bill) Number:");
        referenceNoLabel.setFont(UIUtils.SET_FONT());
        referenceNoLabel.setForeground(UIUtils.TEXT_COLOR);
        form.add(referenceNoLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(cbReference, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        JLabel billIdLabel = new JLabel("Bill ID:");
        billIdLabel.setFont(UIUtils.SET_FONT());
        billIdLabel.setForeground(UIUtils.TEXT_COLOR);
        form.add(billIdLabel, gbc);
        tfBillId = new JTextField(20);
        tfBillId.setFont(UIUtils.SET_FONT());
        tfBillId.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(tfBillId, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        JLabel amountLabel = new JLabel("Bill Amount:");
        amountLabel.setFont(UIUtils.SET_FONT());
        amountLabel.setForeground(UIUtils.TEXT_COLOR);
        form.add(amountLabel, gbc);
        tfBillAmount = new JTextField(20);
        tfBillAmount.setFont(UIUtils.SET_FONT());
        tfBillAmount.setEditable(false);
        tfBillAmount.setText("0.00");
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(tfBillAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.2;
        JLabel totalOverdueChargeLabel = new JLabel("Total Overdue Charge:");
        totalOverdueChargeLabel.setFont(UIUtils.SET_FONT());
        totalOverdueChargeLabel.setForeground(UIUtils.TEXT_COLOR);
        form.add(totalOverdueChargeLabel, gbc);
        tfTotalOverdueCharge = new JTextField(20);
        tfTotalOverdueCharge.setFont(UIUtils.SET_FONT());
        tfTotalOverdueCharge.setEditable(false);
        tfTotalOverdueCharge.setText("0.00");
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(tfTotalOverdueCharge, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.2;
        JLabel totalAmountLabel = new JLabel("Total Amount:");
        totalAmountLabel.setFont(UIUtils.SET_FONT());
        totalAmountLabel.setForeground(UIUtils.TEXT_COLOR);
        form.add(totalAmountLabel, gbc);
        tfTotalAmount = new JTextField(20);
        tfTotalAmount.setFont(UIUtils.SET_FONT());
        tfTotalAmount.setEditable(false);
        tfTotalAmount.setText("0.00");
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(tfTotalAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.2;
        JLabel paydateLabel = new JLabel("Payment Date (YYYY-MM-DD):");
        paydateLabel.setFont(UIUtils.SET_FONT());
        paydateLabel.setForeground(UIUtils.TEXT_COLOR);
        form.add(paydateLabel, gbc);
        tfDate = new JTextField(20);
        tfDate.setFont(UIUtils.SET_FONT());
        tfDate.setText(LocalDate.now().toString());
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(tfDate, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnSave = UIUtils.createMenuButton("Save Payment");
        btnSave.setFont(UIUtils.SET_FONT());
        btnSave.setPreferredSize(new Dimension(200, 45));
        form.add(btnSave, gbc);

        btnSave.addActionListener(e -> savePayment());

        formWrapper.add(form, BorderLayout.CENTER);
        addFormPanel.add(formWrapper, BorderLayout.NORTH);

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
        JComboBox<String> cbFilterType = new JComboBox<>(new String[]{"All", "Service Type", "Reference Number", "Payment Date"});
        cbFilterType.setFont(UIUtils.SET_FONT());
        controlBar.add(cbFilterType);
        JTextField tfFilter = new JTextField(12);
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
        JComboBox<String> cbSortField = new JComboBox<>(new String[]{"Bill Amount", "Payment Date"});
        cbSortField.setFont(UIUtils.SET_FONT());
        controlBar.add(cbSortField);
        JComboBox<String> cbSortOrder = new JComboBox<>(new String[]{"Asc", "Desc"});
        cbSortOrder.setFont(UIUtils.SET_FONT());
        controlBar.add(cbSortOrder);
        JButton btnSort = UIUtils.createMenuButton("Sort");
        btnSort.setPreferredSize(new Dimension(100, 25));
        btnSort.setMaximumSize(new Dimension(100, 25));
        controlBar.add(btnSort);

        listWrapper.add(controlBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Service Type", "Bill ID", "Reference Number", "Bill Amount", "Overdue Charge", "Total Amount", "Payment Date", "Created At"};
        listModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
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
        btnFilter.addActionListener(e -> {
            String filterType = (String) cbFilterType.getSelectedItem();
            String filterValue = tfFilter.getText().trim();
            applyFilter(filterType, filterValue);
        });
        btnReset.addActionListener(e -> {
            tfFilter.setText("");
            applyFilter("All", "");
        });
        btnSort.addActionListener(e -> {
            String sortField = (String) cbSortField.getSelectedItem();
            String sortOrder = (String) cbSortOrder.getSelectedItem();
            applySort(sortField, sortOrder);
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(listPanel, "Select a row to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) listModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(listPanel, "Delete payment ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new BillPaymentDAO().deleteById(id);
                listModel.removeRow(row);
            }
        });
    }

    private static void applyFilter(String filterType, String filterValue) {
        listModel.setRowCount(0);
        List<BillPayment> payments = new BillPaymentDAO().findAll();
        for (BillPayment payment : payments) {
            boolean match = switch (filterType) {
                case "Service Type" -> payment.getServiceType().toLowerCase().contains(filterValue.toLowerCase());
                case "Reference Number" -> payment.getBillReferenceNumber().toLowerCase().contains(filterValue.toLowerCase());
                case "Payment Date" -> payment.getPaymentDate().toString().equals(filterValue);
                default -> true; // "All" or any other case
            };
            if (match) {
                listModel.addRow(new Object[]{
                    payment.getPaymentId(),
                    payment.getServiceType(),
                    payment.getBillId(),
                    payment.getBillReferenceNumber(),
                    String.format("%.2f", payment.getBillAmount()),
                    String.format("%.2f", payment.getTotalOverdueCharge()),
                    String.format("%.2f", payment.getTotalAmount()),
                    payment.getPaymentDate(),
                    payment.getCreatedAt()
                });
            }
        }
    }

    private static void applySort(String sortField, String sortOrder) {
        List<BillPayment> payments = new BillPaymentDAO().findAll();
        boolean asc = "Asc".equals(sortOrder);
        payments.sort((a, b) -> {
            int cmp = 0;
            if ("Bill Amount".equals(sortField)) {
                cmp = Double.compare(a.getBillAmount(), b.getBillAmount());
            } else if ("Payment Date".equals(sortField)) {
                cmp = a.getPaymentDate().compareTo(b.getPaymentDate());
            }
            return asc ? cmp : -cmp;
        });
        listModel.setRowCount(0);
        for (BillPayment payment : payments) {
            listModel.addRow(new Object[]{
                payment.getPaymentId(),
                payment.getServiceType(),
                payment.getBillId(),
                payment.getBillReferenceNumber(),
                String.format("%.2f", payment.getBillAmount()),
                String.format("%.2f", payment.getTotalOverdueCharge()),
                String.format("%.2f", payment.getTotalAmount()),
                payment.getPaymentDate(),
                payment.getCreatedAt()
            });
        }
    }

    private static void savePayment() {
        try {
            int billId = Integer.parseInt(tfBillId.getText().trim());
            String serviceType = (String) cbService.getSelectedItem();
            String referenceNumber = (String) cbReference.getSelectedItem();
            double billAmount = Double.parseDouble(tfBillAmount.getText().trim());
            double totalOverdueCharge = Double.parseDouble(tfTotalOverdueCharge.getText().trim());
            double totalAmount = Double.parseDouble(tfTotalAmount.getText().trim());
            LocalDate paymentDate = LocalDate.parse(tfDate.getText().trim());

            BillPayment payment = new BillPayment(serviceType, billId, referenceNumber, billAmount, totalOverdueCharge, totalAmount, paymentDate);
            new BillPaymentDAO().add(payment);
            ServiceRenewal.reconcileBillPayments();
            JOptionPane.showMessageDialog(addFormPanel, "Payment recorded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshList();
            contentLayout.show(contentWrapper, "list");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(addFormPanel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void refreshList() {
        listModel.setRowCount(0);
        List<BillPayment> payments = new BillPaymentDAO().findAll();
        for (BillPayment payment : payments) {
            listModel.addRow(new Object[]{
                payment.getPaymentId(),
                payment.getServiceType(),   
                payment.getBillId(),
                payment.getBillReferenceNumber(),
                String.format("%.2f", payment.getBillAmount()),
                String.format("%.2f", payment.getTotalOverdueCharge()),
                String.format("%.2f", payment.getTotalAmount()),
                payment.getPaymentDate(),
                payment.getCreatedAt()
            });
        }
    }
}
