package com.aniss.bibliotheque;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LivreUI extends JFrame {

    JTextField txtTitre = new JTextField();
    JTextField txtAuteur = new JTextField();
    JTextField txtISBN = new JTextField();
    JTextField txtExemplaires = new JTextField();
    JTextField txtRecherche = new JTextField();

    JComboBox<String> cbCategorie = new JComboBox<>(
            new String[]{"Informatique", "Mathématiques", "Physique", "Roman", "Autre"});

    DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Titre", "Auteur", "ISBN", "Catégorie", "Exemplaires"}, 0);

    JTable table = new JTable(model);

    JButton btnAjouter = new JButton("Ajouter");
    JButton btnModifier = new JButton("Modifier");
    JButton btnSupprimer = new JButton("Supprimer");
    JButton btnAfficher = new JButton("Afficher");

    JComboBox<String> cbRecherche = new JComboBox<>(
            new String[]{"titre", "auteur", "isbn"});

    public LivreUI() {
        setTitle("Gestion des livres");
        setSize(800, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));


        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("Titre :"));
        form.add(txtTitre);
        form.add(new JLabel("Auteur :"));
        form.add(txtAuteur);
        form.add(new JLabel("ISBN :"));
        form.add(txtISBN);
        form.add(new JLabel("Catégorie :"));
        form.add(cbCategorie);
        form.add(new JLabel("Exemplaires :"));
        form.add(txtExemplaires);


        JPanel buttons = new JPanel();
        buttons.add(btnAjouter);
        buttons.add(btnModifier);
        buttons.add(btnSupprimer);
        buttons.add(btnAfficher);

        JButton btnPdf = new JButton("Exporter PDF");
        buttons.add(btnPdf);

        btnPdf.addActionListener(e -> {
            ExportPDF.livres();
            JOptionPane.showMessageDialog(this, "PDF des livres généré !");
        });


        JPanel recherche = new JPanel(new BorderLayout(5, 5));
        recherche.add(cbRecherche, BorderLayout.WEST);
        recherche.add(txtRecherche, BorderLayout.CENTER);
        JButton btnRecherche = new JButton("Rechercher");
        recherche.add(btnRecherche, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.CENTER);
        top.add(recherche, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);


        btnAjouter.addActionListener(e -> ajouter());
        btnAfficher.addActionListener(e -> charger());
        btnSupprimer.addActionListener(e -> supprimer());
        btnModifier.addActionListener(e -> modifier());
        btnRecherche.addActionListener(e -> rechercher());

        table.getSelectionModel().addListSelectionListener(e -> remplirChamps());

        charger();
        setVisible(true);
    }


    private void ajouter() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO livre(titre,auteur,isbn,categorie,nb_exemplaires) VALUES (?,?,?,?,?)")) {

            ps.setString(1, txtTitre.getText());
            ps.setString(2, txtAuteur.getText());
            ps.setString(3, txtISBN.getText());
            ps.setString(4, cbCategorie.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(txtExemplaires.getText()));

            ps.executeUpdate();
            charger();
            vider();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void modifier() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE livre SET titre=?, auteur=?, isbn=?, categorie=?, nb_exemplaires=? WHERE id=?")) {

            ps.setString(1, txtTitre.getText());
            ps.setString(2, txtAuteur.getText());
            ps.setString(3, txtISBN.getText());
            ps.setString(4, cbCategorie.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(txtExemplaires.getText()));
            ps.setInt(6, id);

            ps.executeUpdate();
            charger();
            vider();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void supprimer() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM livre WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
            charger();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void charger() {
        model.setRowCount(0);

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM livre")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("isbn"),
                        rs.getString("categorie"),
                        rs.getInt("nb_exemplaires")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void rechercher() {
        model.setRowCount(0);
        String champ = cbRecherche.getSelectedItem().toString();
        String sql = "SELECT * FROM livre WHERE " + champ + " LIKE ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + txtRecherche.getText() + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("isbn"),
                        rs.getString("categorie"),
                        rs.getInt("nb_exemplaires")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void remplirChamps() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        txtTitre.setText(model.getValueAt(row, 1).toString());
        txtAuteur.setText(model.getValueAt(row, 2).toString());
        txtISBN.setText(model.getValueAt(row, 3).toString());
        cbCategorie.setSelectedItem(model.getValueAt(row, 4).toString());
        txtExemplaires.setText(model.getValueAt(row, 5).toString());
    }

    private void vider() {
        txtTitre.setText("");
        txtAuteur.setText("");
        txtISBN.setText("");
        txtExemplaires.setText("");
    }
}
