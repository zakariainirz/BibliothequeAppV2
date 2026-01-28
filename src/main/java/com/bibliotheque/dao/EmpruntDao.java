package com.bibliotheque.dao;

import com.bibliotheque.models.Emprunt;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class EmpruntDao {

	public void create(Emprunt emprunt) {
	    String sql = "INSERT INTO emprunts (livre_id, utilisateur_id, date_emprunt, date_retour_prevue, cout_emprunt) VALUES (?, ?, ?, ?, ?)";
	    try (Connection conn = DatabaseManager.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setInt(1, emprunt.getLivreId());
	        pstmt.setInt(2, emprunt.getUtilisateurId());
	        pstmt.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
	        pstmt.setDate(4, Date.valueOf(emprunt.getDateRetourPrevue()));
	        pstmt.setDouble(5, emprunt.getCoutEmprunt()); 
	        pstmt.executeUpdate();

	        // Mettre à jour la disponibilité du livre
	        new LivreDao().decrementerDisponibilite(emprunt.getLivreId());
	        
	        // Mettre à jour la dette totale du lecteur avec le coût initial de l'emprunt
	        new UtilisateurDao().updateAmende(emprunt.getUtilisateurId(), emprunt.getCoutEmprunt());

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

    public void returnBook(int empruntId, int livreId) {
        String sql = "UPDATE emprunts SET date_retour_reelle = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setInt(2, empruntId);
            pstmt.executeUpdate();
            
            new LivreDao().incrementerDisponibilite(livreId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Emprunt> findAllActive() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.id, e.livre_id, e.utilisateur_id, e.date_emprunt, e.date_retour_prevue, l.titre, u.nom " +
                     "FROM emprunts e " +
                     "JOIN livres l ON e.livre_id = l.id " +
                     "JOIN utilisateurs u ON e.utilisateur_id = u.id " +
                     "WHERE e.date_retour_reelle IS NULL " +
                     "ORDER BY e.date_retour_prevue ASC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Emprunt emprunt = new Emprunt();
                emprunt.setId(rs.getInt("id"));
                emprunt.setLivreId(rs.getInt("livre_id"));
                emprunt.setUtilisateurId(rs.getInt("utilisateur_id"));
                emprunt.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
                emprunt.setDateRetourPrevue(rs.getDate("date_retour_prevue").toLocalDate());
                emprunt.setTitreLivre(rs.getString("titre"));
                emprunt.setNomUtilisateur(rs.getString("nom"));
                emprunts.add(emprunt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emprunts;
    }

    public List<Emprunt> findByUserId(int utilisateurId) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.*, l.titre FROM emprunts e JOIN livres l ON e.livre_id = l.id WHERE e.utilisateur_id = ? ORDER BY e.date_emprunt DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, utilisateurId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Emprunt emprunt = new Emprunt();
                emprunt.setId(rs.getInt("id"));
                emprunt.setLivreId(rs.getInt("livre_id"));
                emprunt.setUtilisateurId(rs.getInt("utilisateur_id"));
                emprunt.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
                emprunt.setDateRetourPrevue(rs.getDate("date_retour_prevue").toLocalDate());
                
                Date dateRetourReelle = rs.getDate("date_retour_reelle");
                if (dateRetourReelle != null) {
                    emprunt.setDateRetourReelle(dateRetourReelle.toLocalDate());
                }
                
                emprunt.setTitreLivre(rs.getString("titre"));
                
                emprunt.setCoutEmprunt(rs.getDouble("cout_emprunt"));
                
                emprunts.add(emprunt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emprunts;
    }
    
    public List<Map<String, Object>> getStatistiquesRevenusMensuels() {
        List<Map<String, Object>> stats = new ArrayList<>();
        // Cette requête UNION ALL combine les résultats de deux requêtes différentes
        String sql = 
            "(SELECT 'cout_initial' as type, DATE_FORMAT(date_emprunt, '%Y-%m') as mois, SUM(cout_emprunt) as total " +
            "FROM emprunts " +
            "WHERE date_emprunt >= CURDATE() - INTERVAL 6 MONTH " +
            "GROUP BY mois) " +
            "UNION ALL " +
            "(SELECT 'penalite' as type, DATE_FORMAT(date_retour_reelle, '%Y-%m') as mois, SUM(DATEDIFF(date_retour_reelle, date_retour_prevue)) as total " +
            "FROM emprunts " +
            "WHERE date_retour_reelle IS NOT NULL AND date_retour_reelle > date_retour_prevue " +
            "AND date_retour_reelle >= CURDATE() - INTERVAL 6 MONTH " +
            "GROUP BY mois) " +
            "ORDER BY mois, type";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("type", rs.getString("type"));
                row.put("mois", rs.getString("mois"));
                row.put("total", rs.getDouble("total"));
                stats.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    

    public double getCoutEmpruntsDuMoisEnCours() {
        String sql = "SELECT SUM(cout_emprunt) FROM emprunts WHERE MONTH(date_emprunt) = MONTH(CURDATE()) AND YEAR(date_emprunt) = YEAR(CURDATE())";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    
    public double getPenalitesDuMoisEnCours() {
        // Calcule la somme des jours de retard pour les livres retournés ce mois-ci
        String sql = "SELECT SUM(DATEDIFF(date_retour_reelle, date_retour_prevue)) as total_penalites " +
                     "FROM emprunts " +
                     "WHERE date_retour_reelle IS NOT NULL " +
                     "AND date_retour_reelle > date_retour_prevue " +
                     "AND MONTH(date_retour_reelle) = MONTH(CURDATE()) " +
                     "AND YEAR(date_retour_reelle) = YEAR(CURDATE())";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                // On multiplie par le coût par jour (1 DH)
                return rs.getDouble("total_penalites") * 1.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
