package com.aniss.Library;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DashboardPanel extends JPanel {

    private JLabel totalBooksLabel;
    private JLabel totalStudentsLabel;
    private JLabel activeLoansLabel;
    private JLabel availableBooksLabel;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel welcome = new JLabel(Language.get("welcome") + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(Language.get("select_module"));
        subtitle.setFont(UITheme.NORMAL_FONT);
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(welcome);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(subtitle);

        welcomePanel.add(textPanel, BorderLayout.WEST);

        add(welcomePanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(UITheme.MAIN_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        totalBooksLabel = new JLabel("0");
        totalStudentsLabel = new JLabel("0");
        activeLoansLabel = new JLabel("0");
        availableBooksLabel = new JLabel("0");

        statsPanel.add(createStatCard(Language.get("total_books"), totalBooksLabel, UITheme.PRIMARY));
        statsPanel.add(createStatCard(Language.get("total_students"), totalStudentsLabel, UITheme.SUCCESS));
        statsPanel.add(createStatCard(Language.get("active_loans"), activeLoansLabel, UITheme.WARNING));
        statsPanel.add(createStatCard(Language.get("available"), availableBooksLabel, new Color(52, 168, 83)));

        add(statsPanel, BorderLayout.CENTER);

        refresh();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.NORMAL_FONT);
        titleLabel.setForeground(UITheme.TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(color);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(valueLabel);

        card.add(content, BorderLayout.CENTER);

        return card;
    }

    public void refresh() {
        try (Connection c = DBConnection.getConnection()) {
            Statement st = c.createStatement();

            ResultSet rs = st.executeQuery("SELECT COUNT(*) as count FROM livre");
            if (rs.next()) {
                totalBooksLabel.setText(String.valueOf(rs.getInt("count")));
            }

            rs = st.executeQuery("SELECT COUNT(*) as count FROM etudiant");
            if (rs.next()) {
                totalStudentsLabel.setText(String.valueOf(rs.getInt("count")));
            }

            rs = st.executeQuery("SELECT COUNT(*) as count FROM emprunt WHERE rendu = 0");
            if (rs.next()) {
                activeLoansLabel.setText(String.valueOf(rs.getInt("count")));
            }

            rs = st.executeQuery("SELECT SUM(nb_exemplaires) as count FROM livre");
            if (rs.next()) {
                availableBooksLabel.setText(String.valueOf(rs.getInt("count")));
            }

            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}