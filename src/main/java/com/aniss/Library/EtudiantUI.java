package com.aniss.Library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class EtudiantUI extends JFrame {

    JTextField txtNom = new JTextField();
    JTextField txtFiliere = new JTextField();
    JTextField txtRecherche = new JTextField();

    JComboBox<String> cbRecherche = new JComboBox<>(
            new String[]{"nom", "filiere"});

    DefaultTableModel modelEtudiant = new DefaultTableModel(
            new String[]{"ID", "Nom", "Filière"}, 0);

    DefaultTableModel modelHistorique = new DefaultTableModel(
            new String[]{"Livre", "Date emprunt", "Date retour", "État"}, 0);

    JTable tableEtudiant = new JTable(modelEtudiant);
    JTable tableHistorique = new JTable(modelHistorique);

    JButton btnAjouter = new JButton("Ajouter");
    JButton btnModifier = new JButton("Modifier");
    JButton btnSupprimer = new JButton("Supprimer");
    JButton btnAfficher = new JButton("Afficher");
    JButton btnHistorique = new JButton("Historique");

    public EtudiantUI() {
        setTitle("Gestion des étudiants");
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));


        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Informations étudiant"));
        form.add(new JLabel("Nom :"));
        form.add(txtNom);
        form.add(new JLabel("Filière :"));
        form.add(txtFiliere);


        JPanel buttons = new JPanel();
        buttons.add(btnAjouter);
        buttons.add(btnModifier);
        buttons.add(btnSupprimer);
        buttons.add(btnAfficher);
        buttons.add(btnHistorique);

        JButton btnPdf = new JButton("Exporter PDF");
        buttons.add(btnPdf);

        btnPdf.addActionListener(e -> {
            ExportPDF.etudiants();
            JOptionPane.showMessageDialog(this, "PDF des étudiants généré !");
        });


        JPanel recherche = new JPanel(new BorderLayout(5, 5));
        recherche.setBorder(BorderFactory.createTitledBorder("Recherche"));
        recherche.add(cbRecherche, BorderLayout.WEST);
        recherche.add(txtRecherche, BorderLayout.CENTER);
        JButton btnRecherche = new JButton("Rechercher");
        recherche.add(btnRecherche, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.CENTER);
        top.add(recherche, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);


        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tableEtudiant),
                new JScrollPane(tableHistorique));
        split.setDividerLocation(220);
        add(split, BorderLayout.CENTER);


        btnAjouter.addActionListener(e -> ajouter());
        btnModifier.addActionListener(e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());
        btnAfficher.addActionListener(e -> charger());
        btnRecherche.addActionListener(e -> rechercher());
        btnHistorique.addActionListener(e -> historique());

        tableEtudiant.getSelectionModel()
                .addListSelectionListener(e -> remplirChamps());

        charger();
        setVisible(true);
    }


    private void ajouter() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO etudiant(nom,filiere) VALUES (?,?)")) {

            ps.setString(1, txtNom.getText());
            ps.setString(2, txtFiliere.getText());
            ps.executeUpdate();
            charger();
            vider();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void modifier() {
        int row = tableEtudiant.getSelectedRow();
        if (row == -1) return;

        int id = (int) modelEtudiant.getValueAt(row, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE etudiant SET nom=?, filiere=? WHERE id=?")) {

            ps.setString(1, txtNom.getText());
            ps.setString(2, txtFiliere.getText());
            ps.setInt(3, id);
            ps.executeUpdate();
            charger();
            vider();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void supprimer() {
        int row = tableEtudiant.getSelectedRow();
        if (row == -1) return;

        int id = (int) modelEtudiant.getValueAt(row, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM etudiant WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
            charger();
            modelHistorique.setRowCount(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void charger() {
        modelEtudiant.setRowCount(0);
        modelHistorique.setRowCount(0);

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM etudiant")) {

            while (rs.next()) {
                modelEtudiant.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("filiere")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void rechercher() {
        modelEtudiant.setRowCount(0);
        String champ = cbRecherche.getSelectedItem().toString();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM etudiant WHERE " + champ + " LIKE ?")) {

            ps.setString(1, "%" + txtRecherche.getText() + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelEtudiant.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("filiere")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void historique() {
        modelHistorique.setRowCount(0);

        int row = tableEtudiant.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un étudiant");
            return;
        }

        int idEtudiant = (int) modelEtudiant.getValueAt(row, 0);

        String sql = """
            SELECT l.titre,
                   e.date_emprunt,
                   e.date_retour,
                   e.rendu
            FROM emprunt e
            JOIN livre l ON e.id_livre = l.id
            WHERE e.id_etudiant = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idEtudiant);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String titre = rs.getString("titre");
                String dateEmp = rs.getString("date_emprunt");
                String dateRet = rs.getString("date_retour");
                int rendu = rs.getInt("rendu");

                String etat;
                if (rendu == 1) {
                    etat = "RENDU";
                } else if (dateRet != null &&
                        LocalDate.parse(dateRet).isBefore(LocalDate.now())) {
                    etat = "EN RETARD";
                } else {
                    etat = "EN COURS";
                }

                modelHistorique.addRow(new Object[]{
                        titre,
                        dateEmp,
                        dateRet != null ? dateRet : "---",
                        etat
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void remplirChamps() {
        int row = tableEtudiant.getSelectedRow();
        if (row == -1) return;

        txtNom.setText(modelEtudiant.getValueAt(row, 1).toString());
        txtFiliere.setText(modelEtudiant.getValueAt(row, 2).toString());
    }

    private void vider() {
        txtNom.setText("");
        txtFiliere.setText("");
    }

    public static void main(String[] args) {
        new EtudiantUI();
    }
}

