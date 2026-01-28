package com.bibliotheque.services;

import okhttp3.*;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;

public class GeminiService {

    private static final String API_KEY = "AIzaSyDQH0GLhFtFay0i2Y9eCau-cjnElZxOgiM";

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + API_KEY;
    private final OkHttpClient client = new OkHttpClient();

    public String getCategoryForBook(String title, String description) throws IOException {
        if (API_KEY.equals("VOTRE_CLÉ_API_GEMINI_ICI")) {
            System.out.println("ERREUR: La clé API Gemini n'a pas été configurée.");
            return "Clé API non configurée";
        }

        // Création du prompt pour l'IA
        String prompt = "Génère une seule catégorie pertinente (genre littéraire) pour un livre ayant le titre : \"" + title + "\" et la description : \"" + description + "\". Ta réponse doit contenir uniquement le nom de la catégorie et rien d'autre (par exemple : 'Science-Fiction', 'Roman Historique', 'Thriller').";
        System.out.println("DEBUG (GeminiService): Prompt envoyé à l'IA -> " + prompt);

        // Création du corps de la requête JSON
        JSONObject jsonBody = new JSONObject();
        JSONObject content = new JSONObject();
        JSONObject part = new JSONObject();
        part.put("text", prompt);
        content.put("parts", new JSONArray().put(part));
        jsonBody.put("contents", new JSONArray().put(content));

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            System.out.println("DEBUG (GeminiService): Réponse brute de l'API -> " + responseBody);

            if (!response.isSuccessful()) {
                System.err.println("ERREUR: Appel API échoué avec le code " + response.code() + " et le message : " + response.message());
                System.err.println("Réponse d'erreur : " + responseBody);
                return "Erreur API " + response.code();
            }

            JSONObject jsonResponse = new JSONObject(responseBody);
            
            // Vérification plus sûre de la structure de la réponse
            if (jsonResponse.has("candidates") && jsonResponse.getJSONArray("candidates").length() > 0) {
                String category = jsonResponse.getJSONArray("candidates")
                                              .getJSONObject(0)
                                              .getJSONObject("content")
                                              .getJSONArray("parts")
                                              .getJSONObject(0)
                                              .getString("text");

                System.out.println("DEBUG (GeminiService): Catégorie extraite -> " + category.trim());
                return category.trim();
            } else {
                 System.err.println("ERREUR (GeminiService): La réponse JSON de l'API ne contient pas de 'candidates' valides.");
                 return "Réponse API invalide";
            }

        } catch (Exception e) {
            System.err.println("ERREUR (GeminiService): Exception lors de l'appel ou du parsing de la réponse.");
            e.printStackTrace();
            return "Catégorie non déterminée";
        }
    }
}