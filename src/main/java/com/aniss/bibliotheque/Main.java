package com.aniss.bibliotheque;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        setTitle("Gestion de Bibliothèque");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JLabel titre = new JLabel("Système de Gestion de Bibliothèque", JLabel.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 18));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titre, BorderLayout.NORTH);


        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnLivres = new JButton("Gestion des Livres");
        JButton btnEtudiants = new JButton("Gestion des Étudiants");
        JButton btnEmprunts = new JButton("Gestion des Emprunts");
        JButton btnStats = new JButton("Statistiques");
        JButton btnQuitter = new JButton("Quitter");

        panel.add(btnLivres);
        panel.add(btnEtudiants);
        panel.add(btnEmprunts);
        panel.add(btnStats);
        panel.add(btnQuitter);
        getContentPane().setBackground(UITheme.BACKGROUND);
        titre.setForeground(UITheme.PRIMARY);
        btnLivres.setBackground(UITheme.PRIMARY);
        btnLivres.setForeground(Color.WHITE);

        btnEtudiants.setBackground(UITheme.PRIMARY);
        btnEtudiants.setForeground(Color.WHITE);

        btnEmprunts.setBackground(UITheme.PRIMARY);
        btnEmprunts.setForeground(Color.WHITE);

        btnStats.setBackground(UITheme.SUCCESS);
        btnStats.setForeground(Color.WHITE);

        btnQuitter.setBackground(UITheme.DANGER);
        btnQuitter.setForeground(Color.WHITE);




        add(panel, BorderLayout.CENTER);


        btnLivres.addActionListener(e -> new LivreUI());
        btnEtudiants.addActionListener(e -> new EtudiantUI());
        btnEmprunts.addActionListener(e -> new EmpruntUI());
        btnStats.addActionListener(e -> new StatistiquesUI());

        btnQuitter.addActionListener(e -> {
            int choix = JOptionPane.showConfirmDialog(
                    this,
                    "Voulez-vous quitter l'application ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (choix == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {

        InitDB.init();
        new Main();
    }
}
