package bibliotheque;

import java.sql.Connection;
import java.sql.Statement;

public class InitDB {

    public static void init() {

        try {
            Connection c = DBConnection.getConnection();
            Statement st = c.createStatement();


            st.execute("""
                CREATE TABLE IF NOT EXISTS livre (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titre TEXT NOT NULL,
                    auteur TEXT NOT NULL,
                    isbn TEXT NOT NULL,
                    categorie TEXT NOT NULL,
                    nb_exemplaires INTEGER
                )
            """);


            st.execute("""
                CREATE TABLE IF NOT EXISTS etudiant (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    filiere TEXT NOT NULL
                )
            """);


            st.execute("""
                CREATE TABLE IF NOT EXISTS emprunt (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_etudiant INTEGER NOT NULL,
                    id_livre INTEGER NOT NULL,
                    date_emprunt TEXT NOT NULL,
                    date_retour TEXT,
                    rendu INTEGER DEFAULT 0,
                    FOREIGN KEY (id_etudiant) REFERENCES etudiant(id),
                    FOREIGN KEY (id_livre) REFERENCES livre(id)
                )
            """);

            System.out.println("Base de données initialisée avec succès");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
