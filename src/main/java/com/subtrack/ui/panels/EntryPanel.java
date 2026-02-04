package com.subtrack.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.subtrack.service.SubscriptionRenewalService;
import com.subtrack.ui.utils.UIUtils;

public class EntryPanel {
    public static void displayEntryPanel(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Side panel 
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(UIUtils.PRIMARY);
        sidePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        sidePanel.setPreferredSize(new Dimension(160, 0));

        // Icon
        ImageIcon icon = null;
        java.net.URL imgURL = EntryPanel.class.getResource("/images/play-circle-fill.png");

        if (imgURL != null) {
            icon = new ImageIcon(imgURL);
        } else {
            System.err.println("Resource not found: /images/play-circle-fill.png");
        }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Brand Label
        JLabel brandLabel = new JLabel("SubTrack");
        brandLabel.setForeground(UIUtils.TEXT_COLOR);
        brandLabel.setFont(brandLabel.getFont().deriveFont(Font.BOLD, 26f));
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(iconLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        sidePanel.add(brandLabel);
        sidePanel.add(Box.createVerticalGlue());

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        mainPanel.add(Box.createVerticalGlue());

        JLabel titleLabel = new JLabel("Welcome to SubTrack!", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 20f));
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton enterBtn = UIUtils.createMenuButton("Enter");
        enterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        enterBtn.setPreferredSize(new Dimension(170, 40));
        enterBtn.setMaximumSize(new Dimension(170, 40));
        mainPanel.add(enterBtn);

        mainPanel.add(Box.createVerticalGlue());

        frame.add(sidePanel, BorderLayout.WEST);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();

        enterBtn.addActionListener(e -> {
            SubscriptionRenewalService.processAutoPayments();
            SubscriptionRenewalService.processSubscriptionsRenewal();
            
            frame.dispose(); // Close the entry frame
            DashboardPanel dashboardPanel = new DashboardPanel();
        });
    }
}
