package com.aniss.Library.panels;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import com.aniss.Library.util.DBConnection;
import com.aniss.Library.util.Language;
import com.aniss.Library.UITheme;
import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PlotOrientation;

public class StatisticsPanel extends JPanel {

    private JTabbedPane tabs;

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        tabs = new JTabbedPane();
        tabs.setFont(UITheme.NORMAL_FONT);
        tabs.setBackground(Color.WHITE);

        refresh();

        add(tabs, BorderLayout.CENTER);
    }

    public void refresh() {
        tabs.removeAll();

        tabs.add(Language.get("most_borrowed"), createPanelWrapper(panelLivresPopulaires()));
        tabs.add(Language.get("library_traffic"), createPanelWrapper(panelFrequentation()));
        tabs.add(Language.get("loans_by_major"), createPanelWrapper(panelParFiliere()));
        tabs.add(Language.get("book_availability"), createPanelWrapper(panelDisponibilite()));
    }

    private JPanel createPanelWrapper(JPanel chartPanel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        wrapper.add(chartPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel panelLivresPopulaires() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = """
            SELECT l.titre, COUNT(e.id) AS nb
            FROM emprunt e
            JOIN livre l ON e.id_livre = l.id
            GROUP BY l.titre
            ORDER BY nb DESC
            LIMIT 10
        """;

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                dataset.addValue(
                        rs.getInt("nb"),
                        Language.get("loans_count"),
                        rs.getString("titre")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                Language.get("most_borrowed"),
                Language.get("book"),
                Language.get("loans_count"),
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(new Color(248, 249, 250));

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
                        Language.get("loans_count"),
                        "Total"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                Language.get("library_traffic"),
                "",
                Language.get("total_loans"),
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(new Color(248, 249, 250));

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
                Language.get("loans_by_major"),
                dataset,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);

        return new ChartPanel(chart);
    }

    private JPanel panelDisponibilite() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = """
            SELECT 
                l.titre,
                l.nb_exemplaires - 
                COALESCE((SELECT COUNT(*) FROM emprunt e WHERE e.id_livre = l.id AND e.rendu = 0), 0) AS disponibles
            FROM livre l
        """;

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                dataset.addValue(
                        rs.getInt("disponibles"),
                        Language.get("available_copies"),
                        rs.getString("titre")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                Language.get("book_availability"),
                Language.get("book"),
                Language.get("available_copies"),
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(new Color(248, 249, 250));

        return new ChartPanel(chart);
    }
}