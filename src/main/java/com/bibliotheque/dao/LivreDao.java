package com.bibliotheque.dao;

import com.bibliotheque.models.Livre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDao {

    public void create(Livre livre) {
        String sql = "INSERT INTO livres (titre, auteur, categorie, description, isbn, quantite_totale, quantite_disponible, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setString(3, livre.getCategorie());
            pstmt.setString(4, livre.getDescription());
            pstmt.setString(5, livre.getIsbn());
            pstmt.setInt(6, livre.getQuantiteTotale());
            pstmt.setInt(7, livre.getQuantiteTotale());
            pstmt.setString(8, livre.getImageUrl());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Livre> findAll() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres ORDER BY titre";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                Livre livre = new Livre();
                livre.setId(rs.getInt("id"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setCategorie(rs.getString("categorie"));
                livre.setDescription(rs.getString("description"));
                livre.setIsbn(rs.getString("isbn"));
                livre.setQuantiteTotale(rs.getInt("quantite_totale"));
                livre.setQuantiteDisponible(rs.getInt("quantite_disponible"));
                livre.setImageUrl(rs.getString("image_url"));
                livres.add(livre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }

    public void delete(int id) {
        String sql = "DELETE FROM livres WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void decrementerDisponibilite(int livreId) {
        String sql = "UPDATE livres SET quantite_disponible = quantite_disponible - 1 WHERE id = ? AND quantite_disponible > 0";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, livreId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementerDisponibilite(int livreId) {
        String sql = "UPDATE livres SET quantite_disponible = quantite_disponible + 1 WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, livreId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long countTotalLivres() {
        String sql = "SELECT SUM(quantite_totale) FROM livres";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public long countLivresEmpruntes() {
        String sql = "SELECT COUNT(*) FROM emprunts WHERE date_retour_reelle IS NULL";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    

    public List<Livre> findRecommendationsForUser(int utilisateurId) {
        System.out.println("\n--- DÉBUT DE LA RECHERCHE DE RECOMMANDATIONS pour l'utilisateur ID: " + utilisateurId + " ---");
        
        List<Livre> recommendations = new ArrayList<>();
        List<String> favoriteCategories = new ArrayList<>();

        // Trouver les 2 catégories préférées de l'utilisateur 
        String topCategoriesSql = "SELECT l2.categorie FROM emprunts e JOIN livres l2 ON e.livre_id = l2.id " +
                                  "WHERE e.utilisateur_id = ? AND l2.categorie IS NOT NULL AND l2.categorie != '' " +
                                  "GROUP BY l2.categorie ORDER BY COUNT(*) DESC LIMIT 2";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(topCategoriesSql)) {
            
            pstmt.setInt(1, utilisateurId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                favoriteCategories.add(rs.getString("categorie"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return recommendations;
        }

        // Affiche les catégories trouvées
        System.out.println("DEBUG: Catégories préférées trouvées -> " + favoriteCategories);

        if (favoriteCategories.isEmpty()) {
            System.out.println("INFO: Aucune catégorie préférée trouvée pour cet utilisateur. Aucune recommandation ne peut être générée.");
            return recommendations;
        }

        // Trouver des livres dans ces catégories que l'utilisateur n'a pas lus
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < favoriteCategories.size(); i++) {
            placeholders.append("?").append(i < favoriteCategories.size() - 1 ? "," : "");
        }

        String recommendationsSql = "SELECT * FROM livres l WHERE l.categorie IN (" + placeholders.toString() + ") " +
                                    "AND l.id NOT IN (SELECT livre_id FROM emprunts WHERE utilisateur_id = ?) " +
                                    "ORDER BY RAND() LIMIT 10";
        
        // Affiche la requête qui sera exécutée
        System.out.println("DEBUG: Requête SQL pour les recommandations -> " + recommendationsSql);
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(recommendationsSql)) {
            
            int paramIndex = 1;
            for (String category : favoriteCategories) {
                pstmt.setString(paramIndex++, category);
            }
            pstmt.setInt(paramIndex, utilisateurId);
            
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()) {
                Livre livre = new Livre();
                livre.setId(rs.getInt("id"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setCategorie(rs.getString("categorie"));
                livre.setDescription(rs.getString("description"));
                livre.setQuantiteTotale(rs.getInt("quantite_totale"));
                livre.setQuantiteDisponible(rs.getInt("quantite_disponible"));
                livre.setImageUrl(rs.getString("image_url"));
                recommendations.add(livre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Affiche le résultat final
        System.out.println("INFO: Nombre de recommandations trouvées -> " + recommendations.size());
        System.out.println("--- FIN DE LA RECHERCHE ---");

        return recommendations;
    }
}