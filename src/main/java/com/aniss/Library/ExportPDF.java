package com.aniss.Library;

import java.io.FileOutputStream;
import java.sql.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ExportPDF {

    public static void livres() {
        exporter(
                Language.get("books"),
                "SELECT id, titre, auteur, isbn FROM livre",
                new String[]{"ID", Language.get("title"), Language.get("author"), Language.get("isbn")},
                "livres.pdf"
        );
    }

    public static void etudiants() {
        exporter(
                Language.get("students"),
                "SELECT id, nom, filiere FROM etudiant",
                new String[]{"ID", Language.get("name"), Language.get("major")},
                "etudiants.pdf"
        );
    }

    public static void emprunts() {
        exporter(
                Language.get("loans"),
                """
                SELECT e.id, et.nom, l.titre, e.date_emprunt, e.date_retour
                FROM emprunt e
                JOIN etudiant et ON e.id_etudiant = et.id
                JOIN livre l ON e.id_livre = l.id
                """,
                new String[]{"ID", Language.get("student"), Language.get("book"), Language.get("loan_date"), Language.get("return_date")},
                "emprunts.pdf"
        );
    }

    private static void exporter(String titreDoc, String sql, String[] colonnes, String fichier) {
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(fichier));
            doc.open();

            Font titreFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titre = new Paragraph(titreDoc, titreFont);
            titre.setAlignment(Element.ALIGN_CENTER);
            doc.add(titre);
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(colonnes.length);
            table.setWidthPercentage(100);

            for (String col : colonnes) {
                PdfPCell cell = new PdfPCell(new Phrase(col));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            Connection c = DBConnection.getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                for (int i = 1; i <= colonnes.length; i++) {
                    String value = rs.getString(i);
                    PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : ""));
                    cell.setPadding(5);
                    table.addCell(cell);
                }
            }

            doc.add(table);
            doc.close();
            rs.close();
            st.close();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}