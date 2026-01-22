package com.aniss.bibliotheque;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EmpruntUI extends JFrame {

    JComboBox<String> cbLivres = new JComboBox<>();
    JComboBox<String> cbEtudiants = new JComboBox<>();

    DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Livre", "Étudiant", "Emprunt", "Retour", "Rendu", "Pénalité"}, 0);

    JTable table = new JTable(model);

    JButton btnEmprunter = new JButton("Emprunter");
    JButton btnRetour = new JButton("Retour du livre");
    JButton btnAfficher = new JButton("Afficher");

    public EmpruntUI() {
        setTitle("Gestion des emprunts");
        setSize(900, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));


        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Nouvel emprunt"));
        form.add(new JLabel("Livre disponible :"));
        form.add(cbLivres);
        form.add(new JLabel("Étudiant :"));
        form.add(cbEtudiants);

        JPanel buttons = new JPanel();
        buttons.add(btnEmprunter);
        buttons.add(btnRetour);
        buttons.add(btnAfficher);

        JButton btnPdf = new JButton("Exporter PDF");
        buttons.add(btnPdf);

        btnPdf.addActionListener(e -> {
            ExportPDF.emprunts();
            JOptionPane.showMessageDialog(this, "PDF des emprunts généré !");
        });

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        chargerLivresDisponibles();
        chargerEtudiants();
        chargerEmprunts();

        btnEmprunter.addActionListener(e -> emprunter());
        btnRetour.addActionListener(e -> retour());
        btnAfficher.addActionListener(e -> chargerEmprunts());

        setVisible(true);
    }


    private void chargerLivresDisponibles() {
        cbLivres.removeAllItems();

        String sql = "SELECT id, titre, nb_exemplaires FROM livre WHERE nb_exemplaires > 0";

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                cbLivres.addItem(
                        rs.getInt("id") + " - " +
                                rs.getString("titre") +
                                " (" + rs.getInt("nb_exemplaires") + " dispo)"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void chargerEtudiants() {
        cbEtudiants.removeAllItems();

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nom FROM etudiant")) {

            while (rs.next()) {
                cbEtudiants.addItem(rs.getInt("id") + " - " + rs.getString("nom"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void emprunter() {
        if (cbLivres.getSelectedItem() == null || cbEtudiants.getSelectedItem() == null)
            return;

        int idLivre = Integer.parseInt(cbLivres.getSelectedItem().toString().split(" - ")[0]);
        int idEtudiant = Integer.parseInt(cbEtudiants.getSelectedItem().toString().split(" - ")[0]);

        try (Connection c = DBConnection.getConnection()) {


            PreparedStatement check = c.prepareStatement(
                    "SELECT nb_exemplaires FROM livre WHERE id = ?");
            check.setInt(1, idLivre);
            ResultSet rs = check.executeQuery();

            if (!rs.next() || rs.getInt("nb_exemplaires") <= 0) {
                JOptionPane.showMessageDialog(this, "Aucun exemplaire disponible !");
                return;
            }


            PreparedStatement insert = c.prepareStatement("""
                INSERT INTO emprunt(id_livre,id_etudiant,date_emprunt,date_retour,rendu)
                VALUES(?,?,?,?,0)
            """);

            insert.setInt(1, idLivre);
            insert.setInt(2, idEtudiant);
            insert.setString(3, LocalDate.now().toString());
            insert.setString(4, LocalDate.now().plusDays(7).toString());
            insert.executeUpdate();


            PreparedStatement updateLivre = c.prepareStatement(
                    "UPDATE livre SET nb_exemplaires = nb_exemplaires - 1 WHERE id = ?");
            updateLivre.setInt(1, idLivre);
            updateLivre.executeUpdate();

            chargerLivresDisponibles();
            chargerEmprunts();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void retour() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int idEmprunt = (int) model.getValueAt(row, 0);

        try (Connection c = DBConnection.getConnection()) {


            PreparedStatement ps1 = c.prepareStatement(
                    "SELECT id_livre FROM emprunt WHERE id = ?");
            ps1.setInt(1, idEmprunt);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) return;
            int idLivre = rs.getInt("id_livre");


            PreparedStatement ps2 = c.prepareStatement(
                    "UPDATE emprunt SET rendu = 1 WHERE id = ?");
            ps2.setInt(1, idEmprunt);
            ps2.executeUpdate();


            PreparedStatement ps3 = c.prepareStatement(
                    "UPDATE livre SET nb_exemplaires = nb_exemplaires + 1 WHERE id = ?");
            ps3.setInt(1, idLivre);
            ps3.executeUpdate();

            chargerLivresDisponibles();
            chargerEmprunts();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void chargerEmprunts() {
        model.setRowCount(0);

        String sql = """
            SELECT e.id, l.titre, et.nom,
                   e.date_emprunt, e.date_retour, e.rendu
            FROM emprunt e
            JOIN livre l ON e.id_livre = l.id
            JOIN etudiant et ON e.id_etudiant = et.id
        """;

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                LocalDate dateRetour = LocalDate.parse(rs.getString("date_retour"));
                long retard = ChronoUnit.DAYS.between(dateRetour, LocalDate.now());

                double penalite =
                        (retard > 0 && rs.getInt("rendu") == 0)
                                ? retard * 2
                                : 0;

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("nom"),
                        rs.getString("date_emprunt"),
                        rs.getString("date_retour"),
                        rs.getInt("rendu") == 1 ? "Oui" : "Non",
                        penalite + " DH"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EmpruntUI();
    }
}
