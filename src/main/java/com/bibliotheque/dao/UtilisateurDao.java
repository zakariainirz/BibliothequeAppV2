package com.bibliotheque.dao;

import com.bibliotheque.models.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDao {

    public Utilisateur findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ? AND mot_de_passe = ? AND statut = 'ACTIF'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setAmendeTotale(rs.getDouble("amende_totale"));
                user.setStatut(rs.getString("statut"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void create(Utilisateur user) {
        String sql = "INSERT INTO utilisateurs (nom, email, mot_de_passe, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getMotDePasse());
            pstmt.setString(4, user.getRole());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     public List<Utilisateur> findAllLecteurs() {
        List<Utilisateur> lecteurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE role = 'LECTEUR' AND statut = 'ACTIF'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setAmendeTotale(rs.getDouble("amende_totale"));
                user.setStatut(rs.getString("statut"));
                lecteurs.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lecteurs;
    }
    
    public void updateAmende(int utilisateurId, double amende) {
        String sql = "UPDATE utilisateurs SET amende_totale = amende_totale + ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amende);
            pstmt.setInt(2, utilisateurId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long countTotalLecteurs() {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE role = 'LECTEUR' AND statut = 'ACTIF'";
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

    public List<Utilisateur> findAll() {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY date_creation DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while(rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setStatut(rs.getString("statut"));
                
                // LIGNE MANQUANTE QUI CORRIGE LE BUG :
                user.setAmendeTotale(rs.getDouble("amende_totale"));
                
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUserStatus(int userId, String newStatus) {
        String sql = "UPDATE utilisateurs SET statut = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserRole(int userId, String newRole) {
        String sql = "UPDATE utilisateurs SET role = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
         try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void resetAmende(int userId) {
        String sql = "UPDATE utilisateurs SET amende_totale = 0.00 WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}