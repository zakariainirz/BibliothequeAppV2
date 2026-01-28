<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>

<h2>Gestion des Livres</h2>

<fieldset>
    <legend>Aide à l'ajout via Google Books</legend>
    <form action="${pageContext.request.contextPath}/livres" method="get">
        <input type="hidden" name="action" value="searchAPI">
        <div class="form-group" style="flex-direction: row; align-items: center; gap: 10px; display:flex;">
            <label for="query" style="margin-bottom:0; flex-shrink: 0;">Titre du livre :</label>
            <input type="text" id="query" name="query" required style="flex-grow: 1;">
            <button type="submit" class="btn btn-secondary">Rechercher</button>
        </div>
        <c:if test="${not empty apiError}"><p class="error-message">${apiError}</p></c:if>
    </form>
</fieldset>

<c:if test="${not empty propositions}">
    <h3>Résultats de la recherche - Choisissez un livre</h3>

    <div class="carousel-container">
    
        <button id="scroll-left" class="carousel-arrow">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"></polyline></svg>
        </button>

        <div class="card-container" style="display: flex; overflow-x: auto; gap: 25px; padding: 10px 0;">
            <c:forEach var="prop" items="${propositions}">
			    <div class="card book-card" style="height: 380px; display: flex; flex-direction: column; text-align: center; padding: 15px; flex: 0 0 220px;">
			        <c:if test="${not empty prop.imageUrl}">
			            <img src="${prop.imageUrl.replace('http://', 'https://')}" alt="Couverture">
			        </c:if>
			
			        <div class="card-content-wrapper">
			            <strong><c:out value="${prop.titre}"/></strong>
			            <p class="author"><c:out value="${prop.auteur}"/></p>
			        </div>
			
			        <%-- Ce nouveau lien appelle le servlet pour obtenir la catégorie via l'IA --%>
					<c:url var="categorizeUrl" value="livres">
					    <c:param name="action" value="categorize"/>
					    <c:param name="titre" value="${prop.titre}"/>
					    <c:param name="auteur" value="${prop.auteur}"/>
					    <c:param name="isbn" value="${prop.isbn}"/>
					    <c:param name="description" value="${prop.description}"/>
					    <c:param name="imageUrl" value="${prop.imageUrl}"/>
					</c:url>
					<a href="${categorizeUrl}" class="btn btn-secondary" style="margin-top:auto;">Choisir</a>
			    </div>
			</c:forEach>
        </div>

        <button id="scroll-right" class="carousel-arrow">
             <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"></polyline></svg>
        </button>
    </div>
</c:if>

<h3>Ajouter un nouveau livre</h3>
<form action="${pageContext.request.contextPath}/livres" method="post">
    <input type="hidden" id="imageUrl" name="imageUrl" value="${livreApi.imageUrl}">

    <div class="form-container">
    
        <div class="form-column">
            <div class="form-group">
                <label for="titre">Titre</label>
                <input type="text" id="titre" name="titre" value="${livreApi.titre}" required>
            </div>
            <div class="form-group">
                <label for="auteur">Auteur(s)</label>
                <input type="text" id="auteur" name="auteur" value="${livreApi.auteur}" required>
            </div>
            <div class="form-group">
                <label for="isbn">ISBN</label>
                <input type="text" id="isbn" name="isbn" value="${livreApi.isbn}">
            </div>
             <div class="form-group">
                <label for="quantite">Quantité</label>
                <input type="number" id="quantite" name="quantite" value="1" min="1" required>
            </div>
             <div class="form-group">
                <label for="categorie">Catégorie</label>
                <input type="text" id="categorie" name="categorie" value="${livreApi.categorie}">
            </div>
        </div>

        <div class="form-column">
            <div class="form-group" style="flex-grow: 1;">
                <label for="description">Description</label>
                <textarea id="description" name="description" style="height: 100%;">${livreApi.description}</textarea>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary" style="width: 100%;">Ajouter le Livre</button>
            </div>
        </div>

    </div>
</form>

</br>

<div class="form-group" style="max-width: 500px; margin-top: 40px; margin-bottom: 20px;">
    <label for="searchExistingBooksInput" style="font-weight: bold;">Rechercher dans la liste</label>
    <input type="text" id="searchExistingBooksInput" placeholder="Rechercher par titre, auteur, ISBN..." style="padding: 10px 12px; border: 1px solid #ced4da; border-radius: 4px; font-size: 1rem; width: 100%; box-sizing: border-box;">
</div>
<h3>Liste des Livres Existants</h3>
<table id="existingBooksTable">
    <thead>
        <tr>
            <th>Image</th>
            <th>Titre</th>
            <th>Auteur</th>
            <th>ISBN</th>
            <th>Statut (Disponible / Total)</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="livre" items="${livres}">
            <tr>
                <td>
                    <c:if test="${not empty livre.imageUrl}">
                        <img src="${livre.imageUrl.replace('http://', 'https://')}" alt="Couverture" style="width: 50px; height: auto;"/>
                    </c:if>
                </td>
                <td><c:out value="${livre.titre}"/></td>
                <td><c:out value="${livre.auteur}"/></td>
                <td><c:out value="${livre.isbn}"/></td>
                <td>
				    <c:choose>
				        <c:when test="${livre.quantiteDisponible > 0}">
				            <span class="status ${livre.quantiteDisponible > 0 ? 'disponible' : 'emprunte'}">
							    ${livre.quantiteDisponible} / ${livre.quantiteTotale}
							</span>
				        </c:when>
				        <c:otherwise>
				            <span class="status emprunte" style="padding: 5px 10px; border-radius: 5px; ">
				                ${livre.quantiteDisponible} / ${livre.quantiteTotale}
				            </span>
				        </c:otherwise>
				    </c:choose>
				</td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/livres?action=delete&id=${livre.id}" class="btn btn-danger" onclick="return confirm('Êtes-vous sûr ?')">Supprimer</a>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<script>
document.addEventListener('DOMContentLoaded', () => {

    // --- Logique pour le carrousel (ne change pas) ---
    const carouselContainer = document.querySelector('.card-container');
    const scrollLeftBtn = document.getElementById('scroll-left');
    const scrollRightBtn = document.getElementById('scroll-right');

    if (carouselContainer && scrollLeftBtn && scrollRightBtn) {
        const firstCard = carouselContainer.querySelector('.card');
        const scrollAmount = firstCard ? firstCard.offsetWidth + 15 : 235;

        scrollLeftBtn.addEventListener('click', () => { carouselContainer.scrollLeft -= scrollAmount; });
        scrollRightBtn.addEventListener('click', () => { carouselContainer.scrollLeft += scrollAmount; });
    }
    
    // --- NOUVELLE LOGIQUE POUR LA RECHERCHE DANS LE TABLEAU ---
    const searchInput = document.getElementById('searchExistingBooksInput');
    const booksTable = document.getElementById('existingBooksTable');
    // On s'assure que le tableau existe avant de continuer
    if (searchInput && booksTable) {
        const tableRows = booksTable.querySelectorAll('tbody tr');

        searchInput.addEventListener('keyup', function(e) {
            const searchTerm = e.target.value.toLowerCase();

            tableRows.forEach(row => {
                // On récupère le texte du titre(col 1), auteur(col 2), et ISBN(col 3)
                const title = row.cells[1].textContent.toLowerCase();
                const author = row.cells[2].textContent.toLowerCase();
                const isbn = row.cells[3].textContent.toLowerCase();

                // Si le terme de recherche est trouvé dans l'un des champs, on affiche la ligne
                if (title.includes(searchTerm) || author.includes(searchTerm) || isbn.includes(searchTerm)) {
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