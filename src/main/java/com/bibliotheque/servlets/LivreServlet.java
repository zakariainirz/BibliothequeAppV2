package com.bibliotheque.servlets;

import com.bibliotheque.dao.LivreDao;
import com.bibliotheque.models.Livre;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import com.bibliotheque.models.Utilisateur;

import com.bibliotheque.services.GeminiService;

@WebServlet(name = "LivreServlet", urlPatterns = {"/livres"})
public class LivreServlet extends HttpServlet {
    private LivreDao livreDao;

    @Override
    public void init() {
        this.livreDao = new LivreDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "delete":
                deleteLivre(request, response);
                break;
            case "searchAPI":
                searchGoogleBooks(request, response);
                break;
            case "catalogue":
                // On récupère d'abord tous les livres pour le catalogue principal
                request.setAttribute("livres", livreDao.findAll());

                // Eon cherche des recommandations personnalisées pour l'utilisateur connecté
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute("utilisateur") != null) {
                    Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
                    if (user.getRole().equals("LECTEUR")) {
                        List<Livre> recommendations = livreDao.findRecommendationsForUser(user.getId());
                        request.setAttribute("recommendations", recommendations);
                    }
                }

                request.getRequestDispatcher("/views/catalogue.jsp").forward(request, response);
                break;
            case "categorize":
                categorizeBook(request, response);
                break;
            default: // "list"
                request.setAttribute("livres", livreDao.findAll());
                request.getRequestDispatcher("/views/gestion_livres.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        addLivre(request, response);
    }

    private void addLivre(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Livre livre = new Livre();
        livre.setTitre(request.getParameter("titre"));
        livre.setAuteur(request.getParameter("auteur"));
        livre.setIsbn(request.getParameter("isbn"));
        livre.setCategorie(request.getParameter("categorie"));
        livre.setDescription(request.getParameter("description"));
        livre.setQuantiteTotale(Integer.parseInt(request.getParameter("quantite")));
        livre.setImageUrl(request.getParameter("imageUrl"));

        livreDao.create(livre);
        response.sendRedirect(request.getContextPath() + "/livres");
    }

    private void deleteLivre(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        livreDao.delete(id);
        response.sendRedirect(request.getContextPath() + "/livres");
    }

    private void searchGoogleBooks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8.toString()) + "&maxResults=10";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(sb.toString());
            List<Livre> propositions = new ArrayList<>();
            if (jsonResponse.has("items")) {
                JSONArray items = jsonResponse.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject volumeInfo = items.getJSONObject(i).getJSONObject("volumeInfo");
                    Livre livreApi = new Livre();
                    livreApi.setTitre(volumeInfo.optString("title", ""));
                    livreApi.setAuteur(volumeInfo.has("authors") ? volumeInfo.getJSONArray("authors").join(", ").replace("\"", "") : "N/A");
                    livreApi.setDescription(volumeInfo.optString("description", ""));

                    if (volumeInfo.has("industryIdentifiers")) {
                        JSONArray identifiers = volumeInfo.getJSONArray("industryIdentifiers");
                        if (identifiers.length() > 0) {
                            livreApi.setIsbn(identifiers.getJSONObject(0).optString("identifier", ""));
                        }
                    }
                    
                    if (volumeInfo.has("imageLinks")) {
                        livreApi.setImageUrl(volumeInfo.getJSONObject("imageLinks").optString("thumbnail", ""));
                    }
                    
                    propositions.add(livreApi);
                }
            }
            request.setAttribute("propositions", propositions);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("apiError", "Erreur lors de la communication avec l'API Google Books.");
        }
        
        request.setAttribute("livres", livreDao.findAll());
        request.getRequestDispatcher("/views/gestion_livres.jsp").forward(request, response);
    }
    
    private void categorizeBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer les infos du livre choisi depuis l'URL
        String titre = request.getParameter("titre");
        String auteur = request.getParameter("auteur");
        String isbn = request.getParameter("isbn");
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");

        // Appeler le service Gemini pour obtenir la catégorie
        GeminiService geminiService = new GeminiService();
        String categorie = geminiService.getCategoryForBook(titre, description);

        // On prépare un objet Livre avec TOUTES les informations
        Livre livreApi = new Livre();
        livreApi.setTitre(titre);
        livreApi.setAuteur(auteur);
        livreApi.setIsbn(isbn);
        livreApi.setDescription(description);
        livreApi.setImageUrl(imageUrl);
        livreApi.setCategorie(categorie); // On s'assure que la catégorie de l'IA est bien ajoutée à l'objet

        // On place cet objet dans la requête pour que la page JSP puisse le lire
        request.setAttribute("livreApi", livreApi);
        
        // On récupère aussi la liste des livres pour l'afficher en bas de page
        request.setAttribute("livres", livreDao.findAll());
        
        // On renvoie vers la page JSP qui va utiliser ces informations pour remplir le formulaire
        request.getRequestDispatcher("/views/gestion_livres.jsp").forward(request, response);
    }
}