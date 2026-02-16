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
import java.time.LocalDate;

public class StudentsPanel extends JPanel {

    private JTextField txtNom;
    private JTextField txtFiliere;
    private JTextField txtRecherche;
    private JComboBox<String> cbRecherche;
    private DefaultTableModel modelEtudiant;
    private DefaultTableModel modelHistorique;
    private JTable tableEtudiant;
    private JTable tableHistorique;

    public StudentsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablesPanel(), BorderLayout.CENTER);

        refresh();
    }

    private JPanel createFormPanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel formGrid = new JPanel(new GridLayout(1, 4, 15, 15));
        formGrid.setBackground(Color.WHITE);

        txtNom = createTextField();
        txtFiliere = createTextField();

        formGrid.add(createLabel(Language.get("name") + ":"));
        formGrid.add(txtNom);
        formGrid.add(createLabel(Language.get("major") + ":"));
        formGrid.add(txtFiliere);

        container.add(formGrid, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton(Language.get("add"), UITheme.PRIMARY);
        JButton btnModify = createButton(Language.get("modify"), UITheme.SUCCESS);
        JButton btnDelete = createButton(Language.get("delete"), UITheme.DANGER);
        JButton btnDisplay = createButton(Language.get("display"), UITheme.TEXT_SECONDARY);
        JButton btnHistory = createButton(Language.get("history"), UITheme.TEXT_SECONDARY);
        JButton btnPdf = createButton(Language.get("export_pdf"), UITheme.TEXT_SECONDARY);

        btnAdd.addActionListener(e -> ajouter());
        btnModify.addActionListener(e -> modifier());
        btnDelete.addActionListener(e -> supprimer());
        btnDisplay.addActionListener(e -> refresh());
        btnHistory.addActionListener(e -> historique());
        btnPdf.addActionListener(e -> {
            ExportPDF.etudiants();
            JOptionPane.showMessageDialog(this, Language.get("pdf_generated"));
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnModify);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnDisplay);
        buttonPanel.add(btnHistory);
        buttonPanel.add(btnPdf);

        container.add(buttonPanel, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createTablesPanel() {
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setBackground(UITheme.MAIN_BG);

        container.add(createStudentTablePanel());
        container.add(createHistoryTablePanel());

        return container;
    }

    private JPanel createStudentTablePanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        String[] searchFields = {Language.get("name"), Language.get("major")};
        cbRecherche = new JComboBox<>(searchFields);
        cbRecherche.setFont(UITheme.NORMAL_FONT);
        cbRecherche.setPreferredSize(new Dimension(120, 35));

        txtRecherche = createTextField();

        JButton btnSearch = createButton(Language.get("search"), UITheme.PRIMARY);
        btnSearch.addActionListener(e -> rechercher());

        searchPanel.add(cbRecherche, BorderLayout.WEST);
        searchPanel.add(txtRecherche, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        container.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"ID", Language.get("name"), Language.get("major")};
        modelEtudiant = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableEtudiant = new JTable(modelEtudiant);
        tableEtudiant.setFont(UITheme.NORMAL_FONT);
        tableEtudiant.setRowHeight(35);
        tableEtudiant.setSelectionBackground(new Color(232, 240, 254));
        tableEtudiant.setSelectionForeground(UITheme.TEXT_PRIMARY);
        tableEtudiant.setGridColor(UITheme.BORDER);

        JTableHeader header = tableEtudiant.getTableHeader();
        header.setFont(UITheme.SUBTITLE_FONT);
        header.setBackground(UITheme.MAIN_BG);
        header.setForeground(UITheme.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        tableEtudiant.getSelectionModel().addListSelectionListener(e -> remplirChamps());

        JScrollPane scrollPane = new JScrollPane(tableEtudiant);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createHistoryTablePanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(Language.get("history"));
        titleLabel.setFont(UITheme.SUBTITLE_FONT);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        container.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {Language.get("book"), Language.get("loan_date"), Language.get("return_date"), Language.get("status")};
        modelHistorique = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHistorique = new JTable(modelHistorique);
        tableHistorique.setFont(UITheme.NORMAL_FONT);
        tableHistorique.setRowHeight(35);
        tableHistorique.setSelectionBackground(new Color(232, 240, 254));
        tableHistorique.setSelectionForeground(UITheme.TEXT_PRIMARY);
        tableHistorique.setGridColor(UITheme.BORDER);

        JTableHeader header = tableHistorique.getTableHeader();
        header.setFont(UITheme.SUBTITLE_FONT);
        header.setBackground(UITheme.MAIN_BG);
        header.setForeground(UITheme.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        JScrollPane scrollPane = new JScrollPane(tableHistorique);
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
                     "INSERT INTO etudiant(nom,filiere) VALUES (?,?)")) {

            ps.setString(1, txtNom.getText().trim());
            ps.setString(2, txtFiliere.getText().trim());
            ps.executeUpdate();
            refresh();
            vider();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifier() {
        int row = tableEtudiant.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student");
            return;
        }

        if (!validateInputs()) return;

        int id = (int) modelEtudiant.getValueAt(row, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE etudiant SET nom=?, filiere=? WHERE id=?")) {

            ps.setString(1, txtNom.getText().trim());
            ps.setString(2, txtFiliere.getText().trim());
            ps.setInt(3, id);
            ps.executeUpdate();
            refresh();
            vider();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimer() {
        int row = tableEtudiant.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?");
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) modelEtudiant.getValueAt(row, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM etudiant WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
            refresh();
            modelHistorique.setRowCount(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
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
        String searchField = cbRecherche.getSelectedIndex() == 0 ? "nom" : "filiere";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM etudiant WHERE " + searchField + " LIKE ?")) {

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
            JOptionPane.showMessageDialog(this, Language.get("select_student"));
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
                    etat = Language.get("returned_status");
                } else if (dateRet != null && LocalDate.parse(dateRet).isBefore(LocalDate.now())) {
                    etat = Language.get("late_status");
                } else {
                    etat = Language.get("ongoing_status");
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

    private boolean validateInputs() {
        if (txtNom.getText().trim().isEmpty() || txtFiliere.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return false;
        }
        return true;
    }
}