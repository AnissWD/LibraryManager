package com.aniss.Library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoansPanel extends JPanel {

    private JComboBox<String> cbLivres;
    private JComboBox<String> cbEtudiants;
    private DefaultTableModel model;
    private JTable table;

    public LoansPanel() {
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

        JPanel formGrid = new JPanel(new GridLayout(1, 4, 15, 15));
        formGrid.setBackground(Color.WHITE);

        cbLivres = new JComboBox<>();
        cbLivres.setFont(UITheme.NORMAL_FONT);

        cbEtudiants = new JComboBox<>();
        cbEtudiants.setFont(UITheme.NORMAL_FONT);

        formGrid.add(createLabel(Language.get("available_book") + ":"));
        formGrid.add(cbLivres);
        formGrid.add(createLabel(Language.get("student") + ":"));
        formGrid.add(cbEtudiants);

        container.add(formGrid, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnBorrow = createButton(Language.get("borrow"), UITheme.PRIMARY);
        JButton btnReturn = createButton(Language.get("return"), UITheme.SUCCESS);
        JButton btnDisplay = createButton(Language.get("display"), UITheme.TEXT_SECONDARY);
        JButton btnPdf = createButton(Language.get("export_pdf"), UITheme.TEXT_SECONDARY);

        btnBorrow.addActionListener(e -> emprunter());
        btnReturn.addActionListener(e -> retour());
        btnDisplay.addActionListener(e -> refresh());
        btnPdf.addActionListener(e -> {
            ExportPDF.emprunts();
            JOptionPane.showMessageDialog(this, Language.get("pdf_generated"));
        });

        buttonPanel.add(btnBorrow);
        buttonPanel.add(btnReturn);
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

        String[] columns = {"ID", Language.get("book"), Language.get("student"),
                Language.get("loan_date"), Language.get("return_date"),
                Language.get("returned"), Language.get("penalty")};
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
                                " (" + rs.getInt("nb_exemplaires") + " available)"
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
        if (cbLivres.getSelectedItem() == null || cbEtudiants.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a book and a student");
            return;
        }

        int idLivre = Integer.parseInt(cbLivres.getSelectedItem().toString().split(" - ")[0]);
        int idEtudiant = Integer.parseInt(cbEtudiants.getSelectedItem().toString().split(" - ")[0]);

        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement check = c.prepareStatement(
                    "SELECT nb_exemplaires FROM livre WHERE id = ?");
            check.setInt(1, idLivre);
            ResultSet rs = check.executeQuery();

            if (!rs.next() || rs.getInt("nb_exemplaires") <= 0) {
                JOptionPane.showMessageDialog(this, Language.get("no_copies"));
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
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void retour() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a loan");
            return;
        }

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
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

                double penalite = (retard > 0 && rs.getInt("rendu") == 0) ? retard * 2 : 0;

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("nom"),
                        rs.getString("date_emprunt"),
                        rs.getString("date_retour"),
                        rs.getInt("rendu") == 1 ? Language.get("yes") : Language.get("no"),
                        penalite + " DH"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        chargerLivresDisponibles();
        chargerEtudiants();
        chargerEmprunts();
    }
}