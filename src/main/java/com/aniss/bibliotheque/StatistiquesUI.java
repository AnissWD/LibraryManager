package com.aniss.bibliotheque;

import javax.swing.*;
import java.sql.*;

import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class StatistiquesUI extends JFrame {

    public StatistiquesUI() {
        setTitle("Statistiques de la bibliothèque");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Livres populaires", panelLivresPopulaires());
        tabs.add("Fréquentation", panelFrequentation());
        tabs.add("Emprunts par filière", panelParFiliere());
        tabs.add("Disponibilité des livres", panelDisponibilite());

        add(tabs);
        setVisible(true);
    }


    private JPanel panelLivresPopulaires() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = """
            SELECT l.titre, COUNT(e.id) AS nb
            FROM emprunt e
            JOIN livre l ON e.id_livre = l.id
            GROUP BY l.titre
        """;

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                dataset.addValue(
                        rs.getInt("nb"),
                        "Nombre d'emprunts",
                        rs.getString("titre")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Livres les plus empruntés",
                "Livre",
                "Emprunts",
                dataset
        );

        return new ChartPanel(chart);
    }


    private JPanel panelFrequentation() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = "SELECT COUNT(*) AS total FROM emprunt";

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                dataset.addValue(
                        rs.getInt("total"),
                        "Emprunts",
                        "Total"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Fréquentation de la bibliothèque",
                "",
                "Nombre total d'emprunts",
                dataset
        );

        return new ChartPanel(chart);
    }


    private JPanel panelParFiliere() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        String sql = """
            SELECT et.filiere, COUNT(e.id) AS nb
            FROM emprunt e
            JOIN etudiant et ON e.id_etudiant = et.id
            GROUP BY et.filiere
        """;

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                dataset.setValue(
                        rs.getString("filiere"),
                        rs.getInt("nb")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Emprunts par filière",
                dataset,
                true,
                true,
                false
        );

        return new ChartPanel(chart);
    }


    private JPanel panelDisponibilite() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = """
            SELECT 
                l.titre,
                l.nb_exemplaires -
                COUNT(CASE WHEN e.rendu = 0 THEN 1 END) AS disponibles
            FROM livre l
            LEFT JOIN emprunt e ON l.id = e.id_livre
            GROUP BY l.id
        """;

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                dataset.addValue(
                        rs.getInt("disponibles"),
                        "Exemplaires disponibles",
                        rs.getString("titre")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Disponibilité des livres",
                "Livre",
                "Exemplaires disponibles",
                dataset
        );

        return new ChartPanel(chart);
    }

    public static void main(String[] args) {
        new StatistiquesUI();
    }
}
