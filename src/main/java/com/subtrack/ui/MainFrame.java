package com.subtrack.ui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.subtrack.ui.panels.EntryPanel;

public class MainFrame {
    public MainFrame() {
        JFrame frame = new JFrame("Entry Frame");
        JFrame mainFrame = new JFrame("Main Frame");
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/play-circle-fill.png"));
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); 
        EntryPanel.displayEntryPanel(frame);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
