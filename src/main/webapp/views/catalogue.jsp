<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>

<h2>Catalogue des Livres</h2>

<%-- NOUVELLE SECTION POUR LES RECOMMANDATIONS --%>
<c:if test="${not empty recommendations}">
    <div class="recommendation-section">
        <h3>Spécialement pour vous</h3>
        <div class="carousel-container">
            <button id="reco-scroll-left" class="carousel-arrow">&lt;</button>
            <div id="reco-card-container" class="card-container">
                <c:forEach var="reco" items="${recommendations}">
                    <div class="book-card-container">
                        <div class="book-card-inner">
                            <div class="book-card-front">
                                <div class="book-card-image-wrapper">
                                    <c:choose>
                                        <c:when test="${reco.quantiteDisponible >= 3}">
                                            <c:set var="statusClass" value="disponible"/>
                                        </c:when>
                                        <c:when test="${reco.quantiteDisponible > 0}">
                                            <c:set var="statusClass" value="warning"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="statusClass" value="emprunte"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="status ${statusClass}">${reco.quantiteDisponible}</div>
                                    
                                    <c:if test="${not empty reco.imageUrl}"><img src="${reco.imageUrl.replace('http://', 'https://')}" alt="Couverture"></c:if>
                                    <c:if test="${empty reco.imageUrl}"><div class="no-image-placeholder">Image non disponible</div></c:if>
                                </div>
                                <div class="book-card-content">
                                    <h3><c:out value="${reco.titre}"/></h3>
                                    <p class="author">par <c:out value="${reco.auteur}"/></p>
                                </div>
                            </div>
                            <div class="book-card-back">
                                <h4>Description</h4>
                                <p class="description"><c:out value="${reco.description}"/></p>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <button id="reco-scroll-right" class="carousel-arrow">&gt;</button>
        </div>
    </div>
    <hr style="margin: 40px 0; border: none; border-top: 1px solid #eee;">
</c:if>

<div class="filter-controls">
    <div class="form-group search-bar">
        <input type="text" id="searchInput" placeholder="Rechercher par titre ou auteur...">
    </div>
    <div class="form-group category-filter">
        <select id="categoryFilter">
            <option value="all">Toutes les catégories</option>
            </select>
    </div>
</div>

<div class="catalogue-container">
    <c:forEach var="livre" items="${livres}">
        <div class="book-card-container" data-title="${livre.titre}" data-author="${livre.auteur}" data-category="${livre.categorie}">
            <div class="book-card-inner">

                <div class="book-card-front">
                    <div class="book-card-image-wrapper">
                        <div class="status ${livre.quantiteDisponible >= 3 ? 'disponible' : (livre.quantiteDisponible > 0 ? 'warning' : 'emprunte')}">
                            ${livre.quantiteDisponible}
                        </div>

                        <c:if test="${not empty livre.imageUrl}">
                            <img src="${livre.imageUrl.replace('http://', 'https://')}" alt="Couverture de ${livre.titre}">
                        </c:if>
                        <c:if test="${empty livre.imageUrl}">
                            <div class="no-image-placeholder">Image non disponible</div>
                        </c:if>
                    </div>

                    <div class="book-card-content">
                        <h3><c:out value="${livre.titre}"/></h3>
                        <p class="author">par <c:out value="${livre.auteur}"/></p>
                    </div>
                </div>

                <div class="book-card-back">
                    <h4>Description</h4>
                    <p class="description"><c:out value="${livre.description}"/></p>
                </div>
                
            </div>
        </div>
    </c:forEach>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    const bookCards = document.querySelectorAll('.book-card-container');

    // --- 1. Peupler le filtre des catégories dynamiquement ---
    const categories = new Set(); // Un 'Set' ne stocke que les valeurs uniques
    bookCards.forEach(card => {
        const category = card.dataset.category;
        if (category && category.trim() !== '') {
            categories.add(category);
        }
    });

    // Triez les catégories par ordre alphabétique
    const sortedCategories = Array.from(categories).sort();

    // Créez les options et ajoutez-les au select
    sortedCategories.forEach(category => {
        const option = document.createElement('option');
        option.value = category;
        option.textContent = category;
        categoryFilter.appendChild(option);
    });

    // --- 2. Fonction de filtrage principale ---
    function filterBooks() {
        const searchTerm = searchInput.value.toLowerCase();
        const selectedCategory = categoryFilter.value;

        bookCards.forEach(card => {
            const title = card.dataset.title.toLowerCase();
            const author = card.dataset.author.toLowerCase();
            const category = card.dataset.category;

            // Condition de recherche : le texte doit être dans le titre OU l'auteur
            const matchesSearch = title.includes(searchTerm) || author.includes(searchTerm);

            // Condition de catégorie : "all" ou la catégorie correspond
            const matchesCategory = (selectedCategory === 'all') || (category === selectedCategory);

            // La carte est affichée uniquement si les deux conditions sont vraies
            if (matchesSearch && matchesCategory) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }

    // --- 3. Ajouter les écouteurs d'événements ---
    searchInput.addEventListener('keyup', filterBooks);
    categoryFilter.addEventListener('change', filterBooks);
    
    const recoContainer = document.getElementById('reco-card-container');
    if (recoContainer) {
        const recoScrollLeftBtn = document.getElementById('reco-scroll-left');
        const recoScrollRightBtn = document.getElementById('reco-scroll-right');
        
        recoScrollLeftBtn.addEventListener('click', () => {
            recoContainer.scrollBy({ left: -300, behavior: 'smooth' });
        });

        recoScrollRightBtn.addEventListener('click', () => {
            recoContainer.scrollBy({ left: 300, behavior: 'smooth' });
        });
    }
});
</script>

<%@ include file="footer.jsp" %>