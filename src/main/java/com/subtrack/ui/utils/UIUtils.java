package com.subtrack.ui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

// import com.mysql.cj.x.protobuf.MysqlxNotice.Frame;

public class UIUtils {
    // Updated to a deeper indigo for primary and a softer lavender for secondary
    private static final String PRIMARY_COLOR = "#4B0082";
    private static final String SECONDARY_COLOR = "#B39DDB";
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    public static final Color PRIMARY = Color.decode(PRIMARY_COLOR);
    public static final Color PRIMARY_LIGHT = PRIMARY.brighter();
    private static final Color PRIMARY_DARK = PRIMARY.darker();

    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 17);

    public static Font SET_FONT() {
        Font font = new Font("Arial", Font.BOLD, 15);
        return font;

    }

    public static JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(null);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setPreferredSize(new Dimension(150, 60));
        // button.setMinimumSize(new Dimension(150, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_LIGHT.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY);
            }
        });
        return button;
    }

    public static JButton createPanelButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(PRIMARY_LIGHT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        Border roundedBorder = BorderFactory.createLineBorder(PRIMARY_DARK, 1, true);
        button.setBorder(roundedBorder);
        button.setBorderPainted(true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_LIGHT.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_LIGHT);
            }
        });
        return button;
    }

    public static JPanel createWindowControl(JFrame frame) {
        JButton btnClose = createMenuButton("X");
        JButton btnMinimize = createMenuButton("--");
        JButton btnMaximize = createMenuButton("[]");

        Stream.of(btnClose, btnMinimize, btnMaximize).forEach(b -> {
            b.setFont(BUTTON_FONT);
            b.setForeground(TEXT_COLOR);
            b.setBackground(PRIMARY);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setOpaque(true);
            b.setBorder(null);
            b.setPreferredSize(new Dimension(40, 30));
            b.setMinimumSize(new Dimension(50, 50));
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    b.setBackground(PRIMARY_LIGHT.brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    b.setBackground(PRIMARY);
                }
            });
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controls.setOpaque(false);
        controls.add(btnMinimize);
        controls.add(btnMaximize);
        controls.add(btnClose);

        btnClose.addActionListener(e -> {
            AbandonedConnectionCleanupThread.checkedShutdown(); // no try-catch
            frame.dispose();
        });

        // MINIMIZE
        btnMinimize.addActionListener(e -> frame.setState(Frame.ICONIFIED));

        // MAXIMIZE / RESTORE toggle
        btnMaximize.addActionListener(e -> {
            int state = frame.getExtendedState();
            if ((state & Frame.MAXIMIZED_BOTH) != 0) {
                // already maximized — restore
                frame.setExtendedState(Frame.NORMAL);
            } else {
                // maximize
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        });
        return controls;
    }
}
