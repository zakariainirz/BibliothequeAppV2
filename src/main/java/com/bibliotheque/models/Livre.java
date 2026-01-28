package com.bibliotheque.models;

public class Livre {
    private int id;
    private String titre;
    private String auteur;
    private String categorie;
    private String description;
    private String isbn;
    private int quantiteTotale;
    private int quantiteDisponible;
    private String imageUrl;

    public Livre() {}
    
    // Getters and Setters 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getQuantiteTotale() { return quantiteTotale; }
    public void setQuantiteTotale(int quantiteTotale) { this.quantiteTotale = quantiteTotale; }
    public int getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(int quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}