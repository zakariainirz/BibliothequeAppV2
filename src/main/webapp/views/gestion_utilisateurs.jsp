<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="header.jsp" %>

<h2>Gestion des Utilisateurs</h2>
<p>Cette page vous permet de gérer tous les utilisateurs du système.</p>
<div class="form-group" style="max-width: 500px; margin-bottom: 25px;">
    <input type="text" id="searchInput" placeholder="Rechercher par nom ou email..." style="padding: 10px 12px; border: 1px solid #ced4da; border-radius: 4px; font-size: 1rem; width: 100%; box-sizing: border-box;">
</div>
<table>
    <thead>
        <tr>
            <th>Nom</th>
            <th>Email</th>
            <th>Rôle</th>
            <th>Statut</th>
            <th>Dette Fixe</th>
            <th>Pénalités de Retard</th>
            <th>Total à Payer</th>
            <th style="width: 25%;">Actions</th>
        </tr>
    </thead>
    <tbody>
	    <c:forEach var="user" items="${utilisateurs}">
	        <c:set var="livePenalty" value="${livePenalties[user.id]}" />
	        <c:set var="totalAPayer" value="${user.amendeTotale + livePenalty}" />
	
	        <tr>
	            <td><c:out value="${user.nom}"/></td>
	            <td><c:out value="${user.email}"/></td>
	            <td><c:out value="${user.role}"/></td>
	            <td>
	                <span style="font-weight: bold; padding: 5px 8px; border-radius: 5px; color: white; background-color: ${user.statut == 'ACTIF' ? '#14b8a6' : '#f97316'};">
	                    <c:out value="${user.statut}"/>
	                </span>
	            </td>
	            <td><fmt:formatNumber value="${user.amendeTotale}" type="currency" currencySymbol="DH"/></td>
	            <td style="color: red;"><fmt:formatNumber value="${livePenalty}" type="currency" currencySymbol="DH"/></td>
	            <td style="font-weight: bold;"><fmt:formatNumber value="${totalAPayer}" type="currency" currencySymbol="DH"/></td>
	            <td>
	                <c:if test="${user.id != sessionScope.utilisateur.id}">
	
	                    <c:if test="${user.statut == 'EN ATTENTE'}">
	                        <form action="gestionUtilisateurs" method="post" style="display:inline-block; margin-right: 5px;" class="confirmation-form"
	                              data-title="Accepter l'utilisateur ?"
	                              data-text="L'utilisateur pourra se connecter à l'application."
	                              data-confirm-button-text="Oui, accepter !">
	                            <input type="hidden" name="action" value="accepter">
	                            <input type="hidden" name="userId" value="${user.id}">
	                            <button type="submit" class="btn btn-primary">Accepter</button>
	                        </form>
	                        <form action="gestionUtilisateurs" method="post" style="display:inline-block;" class="confirmation-form"
	                              data-title="Refuser l'utilisateur ?"
	                              data-text="Le compte de cet utilisateur sera supprimé définitivement."
	                              data-confirm-button-text="Oui, refuser !">
	                            <input type="hidden" name="action" value="refuser">
	                            <input type="hidden" name="userId" value="${user.id}">
	                            <button type="submit" class="btn btn-danger">Refuser</button>
	                        </form>
	                    </c:if>
	
	                    <c:if test="${user.statut == 'ACTIF' && user.role == 'LECTEUR'}">
	                         <form action="gestionUtilisateurs" method="post" style="display:inline-block; margin-right:5px;" class="confirmation-form"
	                               data-title="Promouvoir en Admin ?"
	                               data-text="Cet utilisateur aura tous les droits d'un administrateur."
	                               data-confirm-button-text="Oui, promouvoir !">
	                            <input type="hidden" name="action" value="promouvoirAdmin">
	                            <input type="hidden" name="userId" value="${user.id}">
	                            <button type="submit" class="btn btn-secondary">Promouvoir</button>
	                        </form>
	                    </c:if>
	
	                    <c:if test="${user.amendeTotale > 0 || livePenalty > 0}">
	                         <form action="gestionUtilisateurs" method="post" style="display:inline-block;" class="confirmation-form"
	                               data-title="Confirmer le paiement ?"
	                               data-text="La dette fixe de l'utilisateur sera remise à zéro."
	                               data-confirm-button-text="Oui, paiement reçu !">
	                            <input type="hidden" name="action" value="payerAmendes">
	                            <input type="hidden" name="userId" value="${user.id}">
	                            <button type="submit" class="btn" style="background-color: #3b82f6; color: white;">Paiement</button>
	                        </form>
	                    </c:if>
	                </c:if>
	            </td>
	        </tr>
	    </c:forEach>
	</tbody>
</table>
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Sélectionne le champ de recherche et les lignes du tableau
    const searchInput = document.getElementById('searchInput');
    const tableRows = document.querySelectorAll('table tbody tr');

    // Ajoute un écouteur d'événement qui se déclenche à chaque touche pressée
    searchInput.addEventListener('keyup', function(e) {
        const searchTerm = e.target.value.toLowerCase(); // Le texte recherché, en minuscules

        // On parcourt chaque ligne du tableau
        tableRows.forEach(row => {
            // On récupère le contenu du nom (première cellule) et de l'email (deuxième cellule)
            const nameText = row.cells[0].textContent.toLowerCase();
            const emailText = row.cells[1].textContent.toLowerCase();

            // Si le terme de recherche est trouvé dans le nom OU l'email, on affiche la ligne
            if (nameText.includes(searchTerm) || emailText.includes(searchTerm)) {
                row.style.display = ''; // Affiche la ligne (remet le style par défaut)
            } else {
                row.style.display = 'none'; // Cache la ligne
            }
        });
    });
});
</script>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // --- Code existant pour la recherche (ne pas supprimer) ---
    const searchInput = document.getElementById('searchInput');
    // ... etc.

    // --- NOUVEAU CODE POUR LES CONFIRMATIONS ---
    const confirmationForms = document.querySelectorAll('.confirmation-form');

    confirmationForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault(); // On empêche l'envoi du formulaire immédiatement

            // On récupère les messages personnalisés depuis les attributs data-*
            const title = this.dataset.title;
            const text = this.dataset.text;
            const confirmButtonText = this.dataset.confirmButtonText;

            Swal.fire({
                title: title,
                text: text,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: confirmButtonText,
                cancelButtonText: 'Annuler'
            }).then((result) => {
                // Si l'utilisateur clique sur le bouton de confirmation...
                if (result.isConfirmed) {
                    // ... alors on envoie le formulaire pour de vrai.
                    e.target.submit();
                }
            })
        });
    });
});
</script>
<%@ include file="footer.jsp" %>