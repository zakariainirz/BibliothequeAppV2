<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>

<h2>Gestion des Emprunts</h2>

<h3>Enregistrer un Emprunt</h3>
<form action="${pageContext.request.contextPath}/emprunts" method="post">
    <input type="hidden" name="action" value="emprunter">
    <div class="form-container" style="align-items: flex-end;">
        <div class="form-column">
            <div class="form-group">
                <label for="livreId">Choisir un livre disponible :</label>
                <select name="livreId" id="livreId" required>
                	<option value="">-- Choisir un livre disponible --</option> <%-- LIGNE À AJOUTER --%>
                    <c:forEach var="livre" items="${livresDisponibles}">
                        <c:if test="${livre.quantiteDisponible > 0}">
                            <option value="${livre.id}">${livre.titre} - (${livre.quantiteDisponible} disponible(s))</option>
                        </c:if>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="form-column">
            <div class="form-group">
                <label for="utilisateurId">Choisir un lecteur :</label>
                <select name="utilisateurId" id="utilisateurId" required>
                	<option value="">-- Choisir un livre disponible --</option> <%-- LIGNE À AJOUTER --%>
                    <c:forEach var="lecteur" items="${lecteurs}">
                        <option value="${lecteur.id}">${lecteur.nom} (${lecteur.email})</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Enregistrer l'emprunt</button>
        </div>
    </div>
</form>

</br>

<div class="form-group" style="max-width: 500px; margin-top: 40px; margin-bottom: 20px;">
    <label for="searchEmpruntsInput" style="font-weight: bold;">Rechercher un emprunt</label>
    <input type="text" id="searchEmpruntsInput" placeholder="Rechercher par nom du livre ou du lecteur..." style="padding: 10px 12px; border: 1px solid #ced4da; border-radius: 4px; font-size: 1rem; width: 100%; box-sizing: border-box;">
</div>
<h3>Emprunts Actifs</h3>
<table id="activeLoansTable">
    <thead>
        <tr>
            <th>Livre</th>
            <th>Emprunté par</th>
            <th>Date d'emprunt</th>
            <th>Date de retour prévue</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="emprunt" items="${empruntsActifs}">
            <tr>
                <td>${emprunt.titreLivre}</td>
                <td>${emprunt.nomUtilisateur}</td>
                <td>${emprunt.dateEmprunt}</td>
                <td>${emprunt.dateRetourPrevue}</td>
                <td>
                    <form action="${pageContext.request.contextPath}/emprunts" method="post" style="margin:0;">
                        <input type="hidden" name="action" value="retourner">
                        <input type="hidden" name="empruntId" value="${emprunt.id}">
                        <input type="hidden" name="livreId" value="${emprunt.livreId}">
                        <input type="hidden" name="utilisateurId" value="${emprunt.utilisateurId}">
                        <input type="hidden" name="dateRetourPrevue" value="${emprunt.dateRetourPrevue}">
                        <button type="submit" class="btn btn-secondary">Enregistrer le Retour</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<script>
document.addEventListener('DOMContentLoaded', () => {
    // --- Code pour Tom Select (ne change pas) ---
    new TomSelect('#livreId', {
        placeholder: "Rechercher un livre...", // LIGNE AJOUTÉE
        create: false,
        sortField: { field: "text", direction: "asc" }
    });
    new TomSelect('#utilisateurId', {
    	placeholder: "Rechercher un lecteur...", // LIGNE AJOUTÉE
        create: false,
        sortField: { field: "text", direction: "asc" }
    });

    // --- NOUVELLE LOGIQUE POUR LA RECHERCHE DANS LE TABLEAU DES EMPRUNTS ---
    const searchInput = document.getElementById('searchEmpruntsInput');
    const loansTable = document.getElementById('activeLoansTable');

    if (searchInput && loansTable) {
        const tableRows = loansTable.querySelectorAll('tbody tr');

        searchInput.addEventListener('keyup', function(e) {
            const searchTerm = e.target.value.toLowerCase();

            tableRows.forEach(row => {
                // On récupère le texte du titre du livre (colonne 0) et du nom du lecteur (colonne 1)
                const bookTitle = row.cells[0].textContent.toLowerCase();
                const readerName = row.cells[1].textContent.toLowerCase();

                if (bookTitle.includes(searchTerm) || readerName.includes(searchTerm)) {
                    row.style.display = ''; // Affiche la ligne
                } else {
                    row.style.display = 'none'; // Cache la ligne
                }
            });
        });
    }
});
</script>
<%@ include file="footer.jsp" %>