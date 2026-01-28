package com.bibliotheque.servlets;

import com.bibliotheque.dao.UtilisateurDao;
import com.bibliotheque.models.Utilisateur;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.bibliotheque.dao.EmpruntDao;
import com.bibliotheque.models.Emprunt;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "GestionUtilisateurServlet", urlPatterns = {"/gestionUtilisateurs"})
public class GestionUtilisateurServlet extends HttpServlet {
    private UtilisateurDao utilisateurDao;

    @Override
    public void init() {
        this.utilisateurDao = new UtilisateurDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer tous les utilisateurs
        List<Utilisateur> utilisateurs = utilisateurDao.findAll();

        // Créer une Map pour stocker les pénalités de retard "live" pour chaque utilisateur
        Map<Integer, Double> livePenalties = new HashMap<>();
        EmpruntDao empruntDao = new EmpruntDao();

        // Pour chaque utilisateur, calculer ses pénalités de retard actuelles
        for (Utilisateur user : utilisateurs) {
            double totalPenaliteActuelle = 0.0;
            List<Emprunt> emprunts = empruntDao.findByUserId(user.getId());

            for (Emprunt emprunt : emprunts) {
                // On calcule la pénalité uniquement si le livre n'est pas rendu et est en retard
                if (emprunt.getDateRetourReelle() == null && LocalDate.now().isAfter(emprunt.getDateRetourPrevue())) {
                    long joursDeRetard = ChronoUnit.DAYS.between(emprunt.getDateRetourPrevue(), LocalDate.now());
                    totalPenaliteActuelle += joursDeRetard * 1.0; // 1 DH par jour
                }
            }
            livePenalties.put(user.getId(), totalPenaliteActuelle);
        }

        // Envoyer la liste des utilisateurs ET la map des pénalités à la page JSP
        request.setAttribute("utilisateurs", utilisateurs);
        request.setAttribute("livePenalties", livePenalties);

        request.getRequestDispatcher("/views/gestion_utilisateurs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int userId = Integer.parseInt(request.getParameter("userId"));

        switch (action) {
            case "accepter":
                utilisateurDao.updateUserStatus(userId, "ACTIF");
                break;
            case "refuser":
                utilisateurDao.deleteUser(userId);
                break;
            case "promouvoirAdmin":
                utilisateurDao.updateUserRole(userId, "ADMIN");
                break;
            case "payerAmendes":
                utilisateurDao.resetAmende(userId);
                break;
        }

        response.sendRedirect(request.getContextPath() + "/gestionUtilisateurs");
    }
}