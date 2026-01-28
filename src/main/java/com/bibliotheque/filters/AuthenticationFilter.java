package com.bibliotheque.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard", "/livres", "/emprunts", "/profil", "/gestionUtilisateurs"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); 

        boolean isLoggedIn = (session != null && session.getAttribute("utilisateur") != null);

        if (isLoggedIn) {
            chain.doFilter(request, response);
        } else {
            System.out.println("Accès non autorisé détecté. Redirection vers la page de connexion.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/views/login.jsp");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}