package com.bibliotheque.servlets;

import com.bibliotheque.dao.UtilisateurDao;
import com.bibliotheque.models.Utilisateur;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {
    private UtilisateurDao utilisateurDao;

    @Override
    public void init() throws ServletException {
        this.utilisateurDao = new UtilisateurDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null && action.equals("logout")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("login".equals(action)) {
            loginUser(request, response);
        } else if ("register".equals(action)) {
            registerUser(request, response);
        }
    }

    private void loginUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        Utilisateur user = utilisateurDao.findByEmailAndPassword(email, motDePasse);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("utilisateur", user);

            switch (user.getRole()) {
                case "ADMIN":
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    break;
                case "BIBLIOTHECAIRE":
                    response.sendRedirect(request.getContextPath() + "/emprunts");
                    break;
                case "LECTEUR":
                    response.sendRedirect(request.getContextPath() + "/livres?action=catalogue");
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/views/login.jsp");
                    break;
            }
        } else {
            request.setAttribute("errorMessage", "Email ou mot de passe incorrect, ou compte en attente de validation.");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        Utilisateur newUser = new Utilisateur();
        newUser.setNom(nom);
        newUser.setEmail(email);
        newUser.setMotDePasse(motDePasse);
        newUser.setRole("LECTEUR");

        utilisateurDao.create(newUser);
        
        request.setAttribute("successMessage", "Inscription réussie ! Votre compte est en attente de validation par un administrateur.");
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }
}