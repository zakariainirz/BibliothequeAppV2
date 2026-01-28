package com.bibliotheque.servlets;

import com.bibliotheque.dao.EmpruntDao;
import com.bibliotheque.dao.LivreDao;
import com.bibliotheque.dao.UtilisateurDao;
import com.bibliotheque.models.Emprunt;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@WebServlet(name = "EmpruntServlet", urlPatterns = {"/emprunts"})
public class EmpruntServlet extends HttpServlet {
    private EmpruntDao empruntDao;
    private LivreDao livreDao;
    private UtilisateurDao utilisateurDao;

    @Override
    public void init() {
        this.empruntDao = new EmpruntDao();
        this.livreDao = new LivreDao();
        this.utilisateurDao = new UtilisateurDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("empruntsActifs", empruntDao.findAllActive());
        request.setAttribute("livresDisponibles", livreDao.findAll());
        request.setAttribute("lecteurs", utilisateurDao.findAllLecteurs());
        request.getRequestDispatcher("/views/gestion_emprunts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("emprunter".equals(action)) {
            addEmprunt(request, response);
        } else if ("retourner".equals(action)) {
            returnEmprunt(request, response);
        }
    }

    private void addEmprunt(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int livreId = Integer.parseInt(request.getParameter("livreId"));
        int utilisateurId = Integer.parseInt(request.getParameter("utilisateurId"));

        Emprunt emprunt = new Emprunt();
        emprunt.setLivreId(livreId);
        emprunt.setUtilisateurId(utilisateurId);
        emprunt.setDateEmprunt(LocalDate.now());
        emprunt.setDateRetourPrevue(LocalDate.now().plusDays(14));
        emprunt.setCoutEmprunt(20.00); // On définit le coût fixe de 20 DH

        empruntDao.create(emprunt);
        response.sendRedirect(request.getContextPath() + "/emprunts");
    }

    private void returnEmprunt(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int empruntId = Integer.parseInt(request.getParameter("empruntId"));
        int livreId = Integer.parseInt(request.getParameter("livreId"));
        int utilisateurId = Integer.parseInt(request.getParameter("utilisateurId"));
        LocalDate dateRetourPrevue = LocalDate.parse(request.getParameter("dateRetourPrevue"));
        
        if (LocalDate.now().isAfter(dateRetourPrevue)) {
            long joursDeRetard = ChronoUnit.DAYS.between(dateRetourPrevue, LocalDate.now());
            double amende = joursDeRetard * 1.0;
            if (amende > 0) {
                utilisateurDao.updateAmende(utilisateurId, amende);
            }
        }
        
        empruntDao.returnBook(empruntId, livreId);
        response.sendRedirect(request.getContextPath() + "/emprunts");
    }
}