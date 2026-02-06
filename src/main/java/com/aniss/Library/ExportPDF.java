package com.aniss.Library;

import java.io.FileOutputStream;
import java.sql.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ExportPDF {


    public static void livres() {
        exporter(
                "Liste des Livres",
                "SELECT id, titre, auteur, isbn FROM livre",
                new String[]{"ID", "Titre", "Auteur", "ISBN"},
                "livres.pdf"
        );
    }


    public static void etudiants() {
        exporter(
                "Liste des Étudiants",
                "SELECT id, nom, filiere FROM etudiant",
                new String[]{"ID", "Nom", "Filière"},
                "etudiants.pdf"
        );
    }


    public static void emprunts() {
        exporter(
                "Historique des Emprunts",
                """
                SELECT e.id, et.nom, l.titre, e.date_emprunt, e.date_retour
                FROM emprunt e
                JOIN etudiant et ON e.id_etudiant = et.id
                JOIN livre l ON e.id_livre = l.id
                """,
                new String[]{"ID", "Étudiant", "Livre", "Date emprunt", "Date retour"},
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

            for (String col : colonnes)
                table.addCell(col);

            Connection c = DBConnection.getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                for (int i = 1; i <= colonnes.length; i++) {
                    table.addCell(rs.getString(i));
                }
            }

            doc.add(table);
            doc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
