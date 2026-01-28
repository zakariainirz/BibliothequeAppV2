package com.bibliotheque.servlets;

import com.bibliotheque.dao.EmpruntDao;
import com.bibliotheque.dao.LivreDao;
import com.bibliotheque.dao.UtilisateurDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private LivreDao livreDao;
    private UtilisateurDao utilisateurDao;
    private EmpruntDao empruntDao; 

    @Override
    public void init() {
        this.livreDao = new LivreDao();
        this.utilisateurDao = new UtilisateurDao();
        this.empruntDao = new EmpruntDao(); 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //  Statistiques pour les cartes 
        long totalLivres = livreDao.countTotalLivres();
        long livresEmpruntes = livreDao.countLivresEmpruntes();
        long totalLecteurs = utilisateurDao.countTotalLecteurs();
        double coutsEmpruntMois = empruntDao.getCoutEmpruntsDuMoisEnCours();
        double penalitesMois = empruntDao.getPenalitesDuMoisEnCours();

        request.setAttribute("totalLivres", totalLivres);
        request.setAttribute("livresEmpruntes", livresEmpruntes);
        request.setAttribute("totalLecteurs", totalLecteurs);
        request.setAttribute("amendesDuMois", coutsEmpruntMois);
        request.setAttribute("penalitesDuMois", penalitesMois);


        //  génère la liste complète des 6 derniers mois
        List<String> labelsMoisComplets = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            labelsMoisComplets.add(LocalDate.now().minusMonths(i).format(formatter));
        }

        // initialise les maps de données avec 0 pour chaque mois
        Map<String, Double> coutsDataMap = new LinkedHashMap<>();
        Map<String, Double> penalitesDataMap = new LinkedHashMap<>();
        for (String mois : labelsMoisComplets) {
            coutsDataMap.put(mois, 0.0);
            penalitesDataMap.put(mois, 0.0);
        }

        //  récupère les données réelles de la base de données
        List<Map<String, Object>> statsBrutes = empruntDao.getStatistiquesRevenusMensuels();

        // LIGNE DE DÉBOGAGE
        System.out.println("DEBUG: Données brutes reçues du DAO -> " + statsBrutes);

        
        // On remplit les maps avec les données de la BDD, en écrasant les 0 si des données existent
        for (Map<String, Object> stat : statsBrutes) {
            String type = (String) stat.get("type");
            String mois = (String) stat.get("mois");
            double total = (double) stat.get("total");

            if ("cout_initial".equals(type) && coutsDataMap.containsKey(mois)) {
                coutsDataMap.put(mois, total);
            } else if ("penalite".equals(type) && penalitesDataMap.containsKey(mois)) {
                penalitesDataMap.put(mois, total);
            }
        }

        // On transforme les données finales en listes JSON pour JavaScript
        List<String> chartLabels = labelsMoisComplets.stream().map(m -> "\"" + m + "\"").collect(Collectors.toList());
        List<Double> chartCoutsData = new ArrayList<>(coutsDataMap.values());
        List<Double> chartPenalitesData = new ArrayList<>(penalitesDataMap.values());

        request.setAttribute("chartLabels", chartLabels.toString());
        request.setAttribute("chartCoutsData", chartCoutsData.toString());
        request.setAttribute("chartPenalitesData", chartPenalitesData.toString());

        request.getRequestDispatcher("/views/admin_dashboard.jsp").forward(request, response);
    }
}