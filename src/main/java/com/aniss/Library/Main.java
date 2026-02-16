package com.aniss.Library;

import com.aniss.Library.panels.*;
import com.aniss.Library.util.InitDB;
import com.aniss.Library.util.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {

    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JLabel titleLabel;

    private BooksPanel booksPanel;
    private StudentsPanel studentsPanel;
    private LoansPanel loansPanel;
    private StatisticsPanel statisticsPanel;
    private DashboardPanel dashboardPanel;

    public Main() {
        setTitle(Language.get("app_title"));
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createSidebar();
        createMainContent();

        setVisible(true);
    }

    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(UITheme.SIDEBAR_BG);
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UITheme.SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel appTitle = new JLabel(Language.get("app_title"));
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(UITheme.SIDEBAR_TEXT);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(appTitle);

        header.add(Box.createVerticalStrut(10));

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        langPanel.setBackground(UITheme.SIDEBAR_BG);
        langPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnFr = createLanguageButton("FR", "fr");
        JButton btnEn = createLanguageButton("EN", "en");

        langPanel.add(btnFr);
        langPanel.add(btnEn);
        header.add(langPanel);

        sidebarPanel.add(header);
        sidebarPanel.add(Box.createVerticalStrut(10));

        sidebarPanel.add(createMenuItem("🏠 Dashboard", "dashboard"));
        sidebarPanel.add(createMenuItem("📚 " + Language.get("books"), "books"));
        sidebarPanel.add(createMenuItem("👥 " + Language.get("students"), "students"));
        sidebarPanel.add(createMenuItem("📖 " + Language.get("loans"), "loans"));
        sidebarPanel.add(createMenuItem("📊 " + Language.get("statistics"), "statistics"));

        sidebarPanel.add(Box.createVerticalGlue());

        add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton createLanguageButton(String text, String lang) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        boolean isActive = Language.getCurrentLanguage().equals(lang);
        btn.setForeground(isActive ? Color.WHITE : UITheme.TEXT_SECONDARY);
        btn.setBackground(isActive ? UITheme.SIDEBAR_ACTIVE : new Color(52, 63, 82));
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createLineBorder(isActive ? UITheme.SIDEBAR_ACTIVE : new Color(70, 80, 95), 1));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(40, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!isActive) {
                    btn.setBackground(new Color(70, 80, 95));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isActive) {
                    btn.setBackground(new Color(52, 63, 82));
                }
            }
        });

        btn.addActionListener(e -> {
            Language.setLanguage(lang);
            dispose();
            SwingUtilities.invokeLater(() -> new Main());
        });

        return btn;
    }

    private JPanel createMenuItem(String text, String panelName) {
        JPanel menuItem = new JPanel(new BorderLayout());
        menuItem.setBackground(UITheme.SIDEBAR_BG);
        menuItem.setMaximumSize(new Dimension(250, 50));
        menuItem.setPreferredSize(new Dimension(250, 50));
        menuItem.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel label = new JLabel(text);
        label.setFont(UITheme.NORMAL_FONT);
        label.setForeground(UITheme.SIDEBAR_TEXT);
        menuItem.add(label, BorderLayout.WEST);

        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(UITheme.SIDEBAR_HOVER);
                menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(UITheme.SIDEBAR_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                switchPanel(panelName, text);
            }
        });

        return menuItem;
    }

    private void createMainContent() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UITheme.MAIN_BG);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(getWidth(), 70));
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));

        titleLabel = new JLabel(Language.get("welcome"));
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        topBar.add(titleLabel, BorderLayout.WEST);

        container.add(topBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UITheme.MAIN_BG);

        dashboardPanel = new DashboardPanel();
        booksPanel = new BooksPanel();
        studentsPanel = new StudentsPanel();
        loansPanel = new LoansPanel();
        statisticsPanel = new StatisticsPanel();

        mainContentPanel.add(dashboardPanel, "dashboard");
        mainContentPanel.add(booksPanel, "books");
        mainContentPanel.add(studentsPanel, "students");
        mainContentPanel.add(loansPanel, "loans");
        mainContentPanel.add(statisticsPanel, "statistics");

        container.add(mainContentPanel, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }

    private void switchPanel(String panelName, String title) {
        cardLayout.show(mainContentPanel, panelName);

        String displayTitle = title;
        if (title.contains(" ")) {
            displayTitle = title.substring(title.indexOf(" ") + 1);
        }
        titleLabel.setText(displayTitle);

        switch (panelName) {
            case "books" -> booksPanel.refresh();
            case "students" -> studentsPanel.refresh();
            case "loans" -> loansPanel.refresh();
            case "statistics" -> statisticsPanel.refresh();
            case "dashboard" -> dashboardPanel.refresh();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        InitDB.init();
        SwingUtilities.invokeLater(() -> new Main());
    }
}