<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate, java.time.LocalDateTime, java.time.temporal.ChronoUnit" %>

<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.temporal.ChronoUnit" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>

<h2>Mon Profil - ${sessionScope.utilisateur.nom}</h2>
<p>Email : ${sessionScope.utilisateur.email}</p>
<p id="totalAmountDisplay" style="font-weight:bold; color: #ae2012;">Montant total des amendes à payer : ${sessionScope.utilisateur.amendeTotale} MAD</p>

<h3>Historique de mes emprunts</h3>
<table>
    <thead>
        <tr>
            <th>Titre du Livre</th>
            <th>Coût Initial</th>
            <th>Pénalité Actuelle</th>
            <th>Statut du Retour</th>
        </tr>
    </thead>
    <tbody>
	    <c:forEach var="emprunt" items="${emprunts}">
	        <%-- Bloc de calcul en Java pour chaque livre --%>
	        <%
	            com.bibliotheque.models.Emprunt e = (com.bibliotheque.models.Emprunt) pageContext.getAttribute("emprunt");
	            
	            String statutRetour = "";
	            double penaliteActuelle = 0.0;
	            
	            // Si le livre n'est pas encore rendu, on calcule le statut et la pénalité
	            if (e.getDateRetourReelle() == null) {
	                LocalDate aujourdhui = LocalDate.now();
	                LocalDate dateRetourPrevue = e.getDateRetourPrevue();
	
	                // On vérifie si le livre est en retard
	                if (aujourdhui.isAfter(dateRetourPrevue)) {
	                    long joursDeRetard = ChronoUnit.DAYS.between(dateRetourPrevue, aujourdhui);
	                    statutRetour = "<span style='color:red; font-weight:bold;'>" + joursDeRetard + " jour(s) de retard</span>";
	                    
	                    // LOGIQUE DE PÉNALITÉ PAR JOUR RESTAURÉE (1 DH par jour)
	                    penaliteActuelle = joursDeRetard * 1.0; 
	
	                } else { // Si le livre n'est pas en retard
	                    long joursRestants = ChronoUnit.DAYS.between(aujourdhui, dateRetourPrevue);
	                    statutRetour = "<span style='color:green;'>" + joursRestants + " jour(s) restant(s)</span>";
	                }
	
	            } else { // Si le livre a déjà été retourné
	                statutRetour = "<span style='color:blue;'>Retourné le " + e.getDateRetourReelle() + "</span>";
	            }
	
	            // On formate la pénalité pour n'avoir que 2 chiffres après la virgule
	            String penaliteFormattee = String.format("%.2f", penaliteActuelle);
	        %>
	
	        <tr>
	            <td>${emprunt.titreLivre}</td>
	            <td>${emprunt.coutEmprunt} DH</td>
	            <td class="current-penalty" style="color: red; font-weight: bold;">
	                <%= penaliteFormattee %> DH
	            </td>
	            <td>
	                <%= statutRetour %>
	            </td>
	        </tr>
	    </c:forEach>
	</tbody>
</table>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // 1. On récupère le montant de base depuis la base de données
    let totalAmount = ${sessionScope.utilisateur.amendeTotale};

    // 2. On sélectionne toutes les cellules qui affichent une pénalité actuelle
    const penaltyCells = document.querySelectorAll('.current-penalty');

    let currentPenaltiesSum = 0.0;

    // 3. On additionne chaque pénalité "live"
    penaltyCells.forEach(cell => {
        // On extrait le nombre du texte (ex: "4,00 DH" -> 4.00)
        const penaltyText = cell.textContent.trim().replace(',', '.');
        const penaltyValue = parseFloat(penaltyText);

        if (!isNaN(penaltyValue)) {
            currentPenaltiesSum += penaltyValue;
        }
    });

    // 4. Le total final est le montant de base + la somme des pénalités live
    const finalTotal = totalAmount + currentPenaltiesSum;

    // 5. On met à jour l'affichage du montant total
    const totalDisplayElement = document.getElementById('totalAmountDisplay');
    if (totalDisplayElement) {
        totalDisplayElement.textContent = 'Montant total des amendes à payer : ' + finalTotal.toFixed(2).replace('.', ',') + ' MAD';
    }
});
</script>

<%@ include file="footer.jsp" %>