package com.bibliotheque.servlets;

import com.bibliotheque.dao.EmpruntDao;
import com.bibliotheque.models.Emprunt;
import com.bibliotheque.models.Utilisateur;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProfilServlet", urlPatterns = {"/profil"})
public class ProfilServlet extends HttpServlet {
    private EmpruntDao empruntDao;

    @Override
    public void init() {
        this.empruntDao = new EmpruntDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("utilisateur") != null) {
            Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
            List<Emprunt> emprunts = empruntDao.findByUserId(user.getId());
            request.setAttribute("emprunts", emprunts);
            request.getRequestDispatcher("/views/profil_lecteur.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        }
    }
}