package com.aniss.Library.panels;

import com.aniss.Library.util.DBConnection;
import com.aniss.Library.util.ExportPDF;
import com.aniss.Library.util.Language;
import com.aniss.Library.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class BooksPanel extends JPanel {

    private JTextField txtTitre;
    private JTextField txtAuteur;
    private JTextField txtISBN;
    private JTextField txtExemplaires;
    private JTextField txtRecherche;
    private JComboBox<String> cbCategorie;
    private JComboBox<String> cbRecherche;
    private DefaultTableModel model;
    private JTable table;

    public BooksPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        refresh();
    }

    private JPanel createFormPanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel formGrid = new JPanel(new GridLayout(3, 4, 15, 15));
        formGrid.setBackground(Color.WHITE);

        txtTitre = createTextField();
        txtAuteur = createTextField();
        txtISBN = createTextField();
        txtExemplaires = createTextField();

        String[] categories = {
                Language.get("computer_science"),
                Language.get("mathematics"),
                Language.get("physics"),
                Language.get("novel"),
                Language.get("other")
        };
        cbCategorie = new JComboBox<>(categories);
        cbCategorie.setFont(UITheme.NORMAL_FONT);

        formGrid.add(createLabel(Language.get("title") + ":"));
        formGrid.add(txtTitre);
        formGrid.add(createLabel(Language.get("author") + ":"));
        formGrid.add(txtAuteur);
        formGrid.add(createLabel(Language.get("isbn") + ":"));
        formGrid.add(txtISBN);
        formGrid.add(createLabel(Language.get("category") + ":"));
        formGrid.add(cbCategorie);
        formGrid.add(createLabel(Language.get("copies") + ":"));
        formGrid.add(txtExemplaires);

        container.add(formGrid, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton(Language.get("add"), UITheme.PRIMARY);
        JButton btnModify = createButton(Language.get("modify"), UITheme.SUCCESS);
        JButton btnDelete = createButton(Language.get("delete"), UITheme.DANGER);
        JButton btnDisplay = createButton(Language.get("display"), UITheme.TEXT_SECONDARY);
        JButton btnPdf = createButton(Language.get("export_pdf"), UITheme.TEXT_SECONDARY);

        btnAdd.addActionListener(e -> ajouter());
        btnModify.addActionListener(e -> modifier());
        btnDelete.addActionListener(e -> supprimer());
        btnDisplay.addActionListener(e -> refresh());
        btnPdf.addActionListener(e -> {
            ExportPDF.livres();
            JOptionPane.showMessageDialog(this, Language.get("pdf_generated"));
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnModify);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnDisplay);
        buttonPanel.add(btnPdf);

        container.add(buttonPanel, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createTablePanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        String[] searchFields = {Language.get("title"), Language.get("author"), "isbn"};
        cbRecherche = new JComboBox<>(searchFields);
        cbRecherche.setFont(UITheme.NORMAL_FONT);
        cbRecherche.setPreferredSize(new Dimension(150, 35));

        txtRecherche = createTextField();
        txtRecherche.setPreferredSize(new Dimension(300, 35));

        JButton btnSearch = createButton(Language.get("search"), UITheme.PRIMARY);
        btnSearch.addActionListener(e -> rechercher());

        searchPanel.add(cbRecherche, BorderLayout.WEST);
        searchPanel.add(txtRecherche, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        container.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"ID", Language.get("title"), Language.get("author"), Language.get("isbn"), Language.get("category"), Language.get("copies")};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(UITheme.NORMAL_FONT);
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(UITheme.TEXT_PRIMARY);
        table.setGridColor(UITheme.BORDER);

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.SUBTITLE_FONT);
        header.setBackground(UITheme.MAIN_BG);
        header.setForeground(UITheme.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        table.getSelectionModel().addListSelectionListener(e -> remplirChamps());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.NORMAL_FONT);
        label.setForeground(UITheme.TEXT_PRIMARY);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(UITheme.NORMAL_FONT);
        field.setPreferredSize(new Dimension(200, 35));
        return field;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(UITheme.NORMAL_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void ajouter() {
        if (!validateInputs()) return;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO livre(titre,auteur,isbn,categorie,nb_exemplaires) VALUES (?,?,?,?,?)")) {

            ps.setString(1, txtTitre.getText().trim());
            ps.setString(2, txtAuteur.getText().trim());
            ps.setString(3, txtISBN.getText().trim());
            ps.setString(4, cbCategorie.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(txtExemplaires.getText().trim()));

            ps.executeUpdate();
            refresh();
            vider();
            JOptionPane.showMessageDialog(this, "Book added successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifier() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book");
            return;
        }

        if (!validateInputs()) return;

        int id = (int) model.getValueAt(row, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE livre SET titre=?, auteur=?, isbn=?, categorie=?, nb_exemplaires=? WHERE id=?")) {

            ps.setString(1, txtTitre.getText().trim());
            ps.setString(2, txtAuteur.getText().trim());
            ps.setString(3, txtISBN.getText().trim());
            ps.setString(4, cbCategorie.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(txtExemplaires.getText().trim()));
            ps.setInt(6, id);

            ps.executeUpdate();
            refresh();
            vider();
            JOptionPane.showMessageDialog(this, "Book updated successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?");
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) model.getValueAt(row, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM livre WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
            refresh();
            vider();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
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
        String searchField = cbRecherche.getSelectedIndex() == 0 ? "titre" :
                cbRecherche.getSelectedIndex() == 1 ? "auteur" : "isbn";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM livre WHERE " + searchField + " LIKE ?")) {

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

    private boolean validateInputs() {
        if (txtTitre.getText().trim().isEmpty() ||
                txtAuteur.getText().trim().isEmpty() ||
                txtISBN.getText().trim().isEmpty() ||
                txtExemplaires.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return false;
        }

        try {
            Integer.parseInt(txtExemplaires.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Copies must be a number");
            return false;
        }

        return true;
    }
}