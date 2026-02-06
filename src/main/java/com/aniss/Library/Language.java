package com.aniss.Library;

import java.util.HashMap;
import java.util.Map;

public class Language {

    private static String currentLanguage = "fr";

    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    static {
        Map<String, String> fr = new HashMap<>();
        fr.put("Library Manager", "Gestion de Bibliothèque");
        fr.put("books", "Livres");
        fr.put("students", "Étudiants");
        fr.put("loans", "Emprunts");
        fr.put("statistics", "Statistiques");
        fr.put("title", "Titre");
        fr.put("author", "Auteur");
        fr.put("isbn", "ISBN");
        fr.put("category", "Catégorie");
        fr.put("copies", "Exemplaires");
        fr.put("name", "Nom");
        fr.put("major", "Filière");
        fr.put("add", "Ajouter");
        fr.put("modify", "Modifier");
        fr.put("delete", "Supprimer");
        fr.put("display", "Afficher");
        fr.put("search", "Rechercher");
        fr.put("export_pdf", "Exporter PDF");
        fr.put("book_info", "Informations Livre");
        fr.put("student_info", "Informations Étudiant");
        fr.put("available_book", "Livre disponible");
        fr.put("student", "Étudiant");
        fr.put("borrow", "Emprunter");
        fr.put("return", "Retour du livre");
        fr.put("new_loan", "Nouvel emprunt");
        fr.put("loan_date", "Date emprunt");
        fr.put("return_date", "Date retour");
        fr.put("returned", "Rendu");
        fr.put("penalty", "Pénalité");
        fr.put("yes", "Oui");
        fr.put("no", "Non");
        fr.put("history", "Historique");
        fr.put("status", "État");
        fr.put("returned_status", "RENDU");
        fr.put("late_status", "EN RETARD");
        fr.put("ongoing_status", "EN COURS");
        fr.put("select_student", "Sélectionnez un étudiant");
        fr.put("no_copies", "Aucun exemplaire disponible !");
        fr.put("pdf_generated", "PDF généré !");
        fr.put("computer_science", "Informatique");
        fr.put("mathematics", "Mathématiques");
        fr.put("physics", "Physique");
        fr.put("novel", "Roman");
        fr.put("other", "Autre");
        fr.put("most_borrowed", "Livres les plus empruntés");
        fr.put("book", "Livre");
        fr.put("loans_count", "Emprunts");
        fr.put("library_traffic", "Fréquentation de la bibliothèque");
        fr.put("total_loans", "Nombre total d'emprunts");
        fr.put("loans_by_major", "Emprunts par filière");
        fr.put("book_availability", "Disponibilité des livres");
        fr.put("available_copies", "Exemplaires disponibles");
        fr.put("welcome", "Bienvenue");
        fr.put("select_module", "Sélectionnez un module pour commencer");
        fr.put("total_books", "Total Livres");
        fr.put("total_students", "Total Étudiants");
        fr.put("active_loans", "Emprunts Actifs");
        fr.put("available", "Disponibles");

        Map<String, String> en = new HashMap<>();
        en.put("app_title", "Library Management");
        en.put("books", "Books");
        en.put("students", "Students");
        en.put("loans", "Loans");
        en.put("statistics", "Statistics");
        en.put("title", "Title");
        en.put("author", "Author");
        en.put("isbn", "ISBN");
        en.put("category", "Category");
        en.put("copies", "Copies");
        en.put("name", "Name");
        en.put("major", "Major");
        en.put("add", "Add");
        en.put("modify", "Modify");
        en.put("delete", "Delete");
        en.put("display", "Display");
        en.put("search", "Search");
        en.put("export_pdf", "Export PDF");
        en.put("book_info", "Book Information");
        en.put("student_info", "Student Information");
        en.put("available_book", "Available Book");
        en.put("student", "Student");
        en.put("borrow", "Borrow");
        en.put("return", "Return Book");
        en.put("new_loan", "New Loan");
        en.put("loan_date", "Loan Date");
        en.put("return_date", "Return Date");
        en.put("returned", "Returned");
        en.put("penalty", "Penalty");
        en.put("yes", "Yes");
        en.put("no", "No");
        en.put("history", "History");
        en.put("status", "Status");
        en.put("returned_status", "RETURNED");
        en.put("late_status", "LATE");
        en.put("ongoing_status", "ONGOING");
        en.put("select_student", "Select a student");
        en.put("no_copies", "No copies available!");
        en.put("pdf_generated", "PDF generated!");
        en.put("computer_science", "Computer Science");
        en.put("mathematics", "Mathematics");
        en.put("physics", "Physics");
        en.put("novel", "Novel");
        en.put("other", "Other");
        en.put("most_borrowed", "Most Borrowed Books");
        en.put("book", "Book");
        en.put("loans_count", "Loans");
        en.put("library_traffic", "Library Traffic");
        en.put("total_loans", "Total Number of Loans");
        en.put("loans_by_major", "Loans by Major");
        en.put("book_availability", "Book Availability");
        en.put("available_copies", "Available Copies");
        en.put("welcome", "Welcome");
        en.put("select_module", "Select a module to get started");
        en.put("total_books", "Total Books");
        en.put("total_students", "Total Students");
        en.put("active_loans", "Active Loans");
        en.put("available", "Available");

        translations.put("fr", fr);
        translations.put("en", en);
    }

    public static String get(String key) {
        return translations.get(currentLanguage).getOrDefault(key, key);
    }

    public static void setLanguage(String lang) {
        if (translations.containsKey(lang)) {
            currentLanguage = lang;
        }
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }
}