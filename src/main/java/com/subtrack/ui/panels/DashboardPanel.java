package com.subtrack.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import com.subtrack.ui.utils.UIUtils;
import com.subtrack.DAO.PaymentDAO;
import com.subtrack.DAO.SubscriptionDAO;
import com.subtrack.models.Subscription;
import java.time.LocalDate;
import java.util.List;
import com.subtrack.DAO.BillDAO;
import com.subtrack.models.Bill;
import com.subtrack.DAO.BillPaymentDAO;

public class DashboardPanel {
    private boolean isSidebarExpanded = true;
    private JLabel brandLabel;
    private JButton btnSubscriptions;
    private JButton btnSubPayments;
    private JButton btnBills;
    private JButton btnBillPayment;
    private JFrame frame;
    private JPanel sidePanel;
    private JButton toggleButton;
    private ImageIcon toggleIconExpand = new ImageIcon("src/main/resources/images/toggle-icon-expand.png");
    private ImageIcon toggleIconFold = new ImageIcon("src/main/resources/images/toggle-icon-fold.png");
    private ImageIcon settingsIcon = new ImageIcon("src/main/resources/images/settings-icon.png"); 

    public DashboardPanel() {
        frame = new JFrame("Dashboard");
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/images/play-circle-fill.png"));
        frame.setIconImage(appIcon.getImage());
        frame.setSize(1300, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setUndecorated(true);

        // side panel
        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(UIUtils.PRIMARY);
        sidePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        sidePanel.setPreferredSize(new Dimension(200, 0));
        sidePanel.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));

        // Toggle button
        toggleButton = new JButton(toggleIconFold);
        toggleButton.setBackground(UIUtils.PRIMARY);
        toggleButton.setFocusPainted(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> animateToggle());

        // Icon
        ImageIcon icon = new ImageIcon("src/main/resources/images/play-circle-fill.png");
        JButton iconLabel = new JButton(icon);
        iconLabel.setContentAreaFilled(false);
        iconLabel.setOpaque(false);
        iconLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        iconLabel.setForeground(UIUtils.TEXT_COLOR);
        iconLabel.setBackground(UIUtils.PRIMARY);
        iconLabel.setFocusPainted(false);
        iconLabel.setBorderPainted(false);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.addActionListener(e -> {
            frame.remove(mainPanel);
            frame.add(createMainPanel(), BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });
        sidePanel.add(Box.createVerticalStrut(30));
        sidePanel.add(iconLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Brand label
        brandLabel = new JLabel("SubTrack");
        brandLabel.setFont(brandLabel.getFont().deriveFont(Font.BOLD, 30f));
        brandLabel.setForeground(UIUtils.TEXT_COLOR);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(brandLabel);
        sidePanel.add(Box.createVerticalStrut(30));

        // Manage subscriptions button
        btnSubscriptions = UIUtils.createMenuButton("Subscriptions");
        btnSubscriptions.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSubscriptions.addActionListener(e -> {
            SubscriptionPanel.build(mainPanel, frame);
        });

        // Manage Payments button
        btnSubPayments = UIUtils.createMenuButton("Subscription \n Payments");
        btnSubPayments.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSubPayments.addActionListener(e -> {
            PaymentPanel.build(mainPanel, frame);
        });

        //Manage Bills button
        btnBills = UIUtils.createMenuButton("Bills");
        btnBills.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBills.addActionListener(e -> {
            BillPanel.build(mainPanel, frame);
        });

        //Manage Bill Payments button
        btnBillPayment = UIUtils.createMenuButton("Bill Payments");
        btnBillPayment.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBillPayment.addActionListener(e -> {
            BillPaymentPanel.build(mainPanel, frame);
        });
        
        sidePanel.add(btnSubscriptions);
        sidePanel.add(btnSubPayments);
        sidePanel.add(btnBills);
        sidePanel.add(btnBillPayment);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(toggleButton, BorderLayout.SOUTH);
        sidePanel.add(Box.createVerticalStrut(20));

        frame.add(sidePanel, BorderLayout.WEST);
        frame.add(createMainPanel(), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel mainPanel;
    private JPanel createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        // Top wrapper
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        topWrapper.add(UIUtils.createWindowControl(frame));

        // Header bar
        JPanel headerBar = new JPanel();
        headerBar.setBackground(UIUtils.PRIMARY_LIGHT);
        headerBar.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerBar.setLayout(new BoxLayout(headerBar, BoxLayout.X_AXIS));
        headerBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel headerLabel = new JLabel("Dashboard");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 26f));
        headerLabel.setForeground(UIUtils.TEXT_COLOR);
        headerBar.add(headerLabel);
        headerBar.add(Box.createHorizontalGlue());
        JButton settingsBtn = new JButton(settingsIcon);
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
        JButton btnSubscriptions = UIUtils.createMenuButton("Subscription");
        btnSubscriptions.setPreferredSize(new Dimension(150, 40));
        JButton btnBills = UIUtils.createMenuButton("Bills");
        btnBills.setPreferredSize(new Dimension(150, 40));
        menuBar.add(btnSubscriptions);
        menuBar.add(Box.createHorizontalStrut(10));
        menuBar.add(btnBills);
        topWrapper.add(menuBar);

        mainPanel.add(topWrapper, BorderLayout.NORTH);
        btnSubscriptions.addActionListener(e -> {
            createSubscriptionDashboard(mainPanel);
        });
        btnBills.addActionListener(e -> {
            createBillsDashboard(mainPanel);
        });
        // Default to Subscription dashboard
        createSubscriptionDashboard(mainPanel);
        return mainPanel;
    }

    private void createSubscriptionDashboard(JPanel mainPanel){
        // Content area
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UIUtils.BACKGROUND_COLOR);
        JPanel stats = new JPanel(new GridLayout(1,3,20,20));
        stats.setBackground(UIUtils.BACKGROUND_COLOR);
        stats.setBorder(new EmptyBorder(20,20,20,20));

        long activeCount = new SubscriptionDAO().findAll().stream().filter(Subscription::isActive).count();
        stats.add(createStatCard("Active Subscriptions", String.valueOf(activeCount)));

        double totalSpent = new PaymentDAO().findAll().stream().mapToDouble(p -> p.getAmount()).sum();
        stats.add(createStatCard("Total Spent", String.format("₹ %.2f", totalSpent)));

        LocalDate today = LocalDate.now();
        List<Subscription> dueSoon = new SubscriptionDAO().findAll().stream()
            .filter(Subscription::isActive)
            .filter(s -> !s.getNextDueDate().isBefore(today) && s.getNextDueDate().isBefore(today.plusDays(10)) && !s.isAutoPay())
            .toList();
        stats.add(createStatCard("Due Within 10 Days", String.valueOf(dueSoon.size())));

        content.add(stats, BorderLayout.CENTER);

        // Due Soon List
        JPanel dueWrapper = new JPanel();
        dueWrapper.setLayout(new BorderLayout());
        dueWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        dueWrapper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UIUtils.TEXT_COLOR),
            "Subscriptions Due Soon", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 25),
            UIUtils.TEXT_COLOR));
        JPanel duePanel = new JPanel();
        duePanel.setBackground(UIUtils.BACKGROUND_COLOR);
        duePanel.setMinimumSize(new Dimension(0, 200));
        duePanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20)); 
        duePanel.setLayout(new BoxLayout(duePanel, BoxLayout.Y_AXIS));

        for (Subscription s : dueSoon) {
            JPanel item = new JPanel(new GridLayout(1,3));
            item.setBackground(UIUtils.BACKGROUND_COLOR);
            item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.PRIMARY,1), new EmptyBorder(10,20,10,20)
            ));
            // fixing height of each item
            int itemHeight = 60; 
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, itemHeight));
            item.setMinimumSize(new Dimension(0, itemHeight));
            item.setPreferredSize(new Dimension(duePanel.getWidth(), itemHeight));
            JLabel pLbl = new JLabel(s.getPlatform()); pLbl.setFont(UIUtils.SET_FONT()); pLbl.setForeground(UIUtils.TEXT_COLOR);
            JLabel nLbl = new JLabel(s.getName()); nLbl.setFont(UIUtils.SET_FONT()); nLbl.setForeground(UIUtils.TEXT_COLOR);
            JLabel dLbl = new JLabel(s.getNextDueDate().toString()); dLbl.setFont(UIUtils.SET_FONT()); dLbl.setForeground(UIUtils.TEXT_COLOR);
            item.add(pLbl); item.add(nLbl); item.add(dLbl);
            duePanel.add(item);
            duePanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(duePanel);
        scroll.setBackground(UIUtils.BACKGROUND_COLOR);
        scroll.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(0, 220));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dueWrapper.add(scroll);

        content.add(dueWrapper, BorderLayout.SOUTH);
        mainPanel.add(content, BorderLayout.CENTER);
        mainPanel.revalidate();
    }

    private void createBillsDashboard(JPanel mainPanel){
        // Content area
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UIUtils.BACKGROUND_COLOR);
        JPanel stats = new JPanel(new GridLayout(1,3,20,20));
        stats.setBackground(UIUtils.BACKGROUND_COLOR);
        stats.setBorder(new EmptyBorder(20,20,20,20));

        long activeCount = new BillDAO().findAll().stream().filter(Bill::isPaid).count();
        stats.add(createStatCard("Total Bills Paid", String.valueOf(activeCount)));

        double totalSpent = new BillPaymentDAO().findAll().stream().mapToDouble(p -> p.getTotalAmount()).sum();
        stats.add(createStatCard("Total Spent", String.format("₹ %.2f", totalSpent)));

        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(10);

        List<Bill> dueSoon = new BillDAO().findAll().stream()
            .filter(b -> !b.isPaid())
            .filter(b -> !b.getDueDate().isAfter(cutoff))
            .toList();
        stats.add(createStatCard("Bills Due Within 10 Days", String.valueOf(dueSoon.size())));

        content.add(stats, BorderLayout.CENTER);

        // Due Soon List
        JPanel dueWrapper = new JPanel();
        dueWrapper.setLayout(new BorderLayout());
        dueWrapper.setBackground(UIUtils.BACKGROUND_COLOR);
        dueWrapper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UIUtils.TEXT_COLOR),
            "Bills Due Soon", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 25),
            UIUtils.TEXT_COLOR));
        JPanel duePanel = new JPanel();
        duePanel.setBackground(UIUtils.BACKGROUND_COLOR);
        duePanel.setMinimumSize(new Dimension(0, 200));
        duePanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        duePanel.setLayout(new BoxLayout(duePanel, BoxLayout.Y_AXIS));

        for (Bill b : dueSoon) {
            JPanel item = new JPanel(new GridLayout(1, 4));
            item.setBackground(UIUtils.BACKGROUND_COLOR);
            item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.PRIMARY, 1), new EmptyBorder(10, 20, 10, 20)
            ));
            // fixing height of each item
            int itemHeight = 60;
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, itemHeight));
            item.setMinimumSize(new Dimension(0, itemHeight));
            item.setPreferredSize(new Dimension(duePanel.getWidth(), itemHeight));
            JLabel refLbl = new JLabel(b.getReferenceNumber()); refLbl.setFont(UIUtils.SET_FONT()); refLbl.setForeground(UIUtils.TEXT_COLOR);
            JLabel sLbl = new JLabel(b.getServiceType()); sLbl.setFont(UIUtils.SET_FONT()); sLbl.setForeground(UIUtils.TEXT_COLOR);
            JLabel aLbl = new JLabel(String.format("₹ %.2f", b.getAmount())); aLbl.setFont(UIUtils.SET_FONT()); aLbl.setForeground(UIUtils.TEXT_COLOR);
            JLabel dLbl = new JLabel(b.getDueDate().toString()); dLbl.setFont(UIUtils.SET_FONT()); dLbl.setForeground(UIUtils.TEXT_COLOR);
            item.add(refLbl); item.add(sLbl); item.add(aLbl); item.add(dLbl);
            duePanel.add(item);
            duePanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(duePanel);
        scroll.setBackground(UIUtils.BACKGROUND_COLOR);
        scroll.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(0, 220));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dueWrapper.add(scroll);

        content.add(dueWrapper, BorderLayout.SOUTH);
        mainPanel.add(content, BorderLayout.CENTER);
        mainPanel.revalidate();
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel();
        card.setBackground(UIUtils.BACKGROUND_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.PRIMARY,2), new EmptyBorder(15,15,15,15)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title); t.setFont(UIUtils.SET_FONT()); t.setForeground(UIUtils.TEXT_COLOR);
        JLabel v = new JLabel(value); v.setFont(v.getFont().deriveFont(Font.BOLD,24f)); v.setForeground(UIUtils.TEXT_COLOR);
        card.add(t); card.add(Box.createVerticalStrut(10)); card.add(v);
        return card;
    }

    private void animateToggle() {
        int targetWidth = isSidebarExpanded ? 60 : 200;
        int step = isSidebarExpanded ? -5 : 5;

        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int newWidth = sidePanel.getPreferredSize().width + step;
                boolean done = isSidebarExpanded ? newWidth <= targetWidth : newWidth >= targetWidth;
                sidePanel.setPreferredSize(new Dimension(done ? targetWidth : newWidth, 0));
                sidePanel.revalidate();
                frame.repaint();
                if (done) {
                    ((Timer) e.getSource()).stop();
                    updateSidebarText();
                }
            }
        });
        timer.start();
    }

    private void updateSidebarText() {
        Timer fadeTimer = new Timer(30, null);
        final float[] opacity = { isSidebarExpanded ? 1.0f : 0.0f };
        final boolean fadingOut = isSidebarExpanded;
    
        if (!fadingOut) {
            brandLabel.setVisible(true);
            btnSubscriptions.setVisible(true);
            btnSubscriptions.setEnabled(true);  // Enable early
            btnSubPayments.setVisible(true);
            btnSubPayments.setEnabled(true);
            btnBills.setVisible(true);
            btnBills.setEnabled(true); // Enable early
            btnBillPayment.setVisible(true);
            btnBillPayment.setEnabled(true); // Enable early
        }
    
        fadeTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fadingOut) {
                    opacity[0] -= 0.1f;
                    if (opacity[0] <= 0f) {
                        opacity[0] = 0f;
                        brandLabel.setVisible(false);
                        btnSubscriptions.setVisible(false);
                        btnSubscriptions.setEnabled(false); 
                        btnSubPayments.setVisible(false);
                        btnSubPayments.setEnabled(false);
                        btnBills.setVisible(false);
                        btnBills.setEnabled(false);
                        btnBillPayment.setVisible(false);
                        btnBillPayment.setEnabled(false);
                        fadeTimer.stop();
                    }
                } else {
                    opacity[0] += 0.1f;
                    if (opacity[0] >= 1f) {
                        opacity[0] = 1f;
                        fadeTimer.stop();
                    }
                }
                java.awt.Color fadedColor = new java.awt.Color(1f, 1f, 1f, opacity[0]);
                brandLabel.setForeground(fadedColor);
                btnSubscriptions.setForeground(fadedColor);
                btnSubPayments.setForeground(fadedColor);
                btnBills.setForeground(fadedColor);
                btnBillPayment.setForeground(fadedColor);
    
                brandLabel.repaint();
                btnSubscriptions.repaint();
                btnSubPayments.repaint();
                btnBills.repaint();
                btnBills.setForeground(fadedColor);
                btnBillPayment.repaint();
                btnBillPayment.setForeground(fadedColor);
                btnSubPayments.setForeground(fadedColor);
            }
        });
        fadeTimer.start();
        isSidebarExpanded = !isSidebarExpanded;
        toggleButton.setIcon(isSidebarExpanded ? toggleIconFold : toggleIconExpand);

    }
}

