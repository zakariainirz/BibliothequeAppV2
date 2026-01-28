package com.bibliotheque.models;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private int livreId;
    private int utilisateurId;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourReelle;
    private String titreLivre;
    private String nomUtilisateur;
    private double coutEmprunt; 

    public Emprunt() {}
    
    // Getters and Setters 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLivreId() { return livreId; }
    public void setLivreId(int livreId) { this.livreId = livreId; }
    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }
    public LocalDate getDateRetourReelle() { return dateRetourReelle; }
    public void setDateRetourReelle(LocalDate dateRetourReelle) { this.dateRetourReelle = dateRetourReelle; }
    public String getTitreLivre() { return titreLivre; }
    public void setTitreLivre(String titreLivre) { this.titreLivre = titreLivre; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }
    public double getCoutEmprunt() { return coutEmprunt; }
    public void setCoutEmprunt(double coutEmprunt) { this.coutEmprunt = coutEmprunt; }
}