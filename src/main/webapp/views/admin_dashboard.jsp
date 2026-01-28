<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="header.jsp" %>

<h2>Dashboard Administrateur</h2>

<div class="dashboard-cards">
    
    <div class="card">
        <div class="card-icon icon-books">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path></svg>
        </div>
        <div class="card-content">
            <h3>Total des Livres</h3>
            <p>${totalLivres}</p>
        </div>
    </div>

    <div class="card">
        <div class="card-icon icon-loans">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path></svg>
        </div>
        <div class="card-content">
            <h3>Livres Empruntés</h3>
            <p>${livresEmpruntes}</p>
        </div>
    </div>

    <div class="card">
        <div class="card-icon icon-users">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
        </div>
        <div class="card-content">
            <h3>Total des Lecteurs</h3>
            <p>${totalLecteurs}</p>
        </div>
    </div>

    <div class="card">
        <div class="card-icon icon-amendes">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"></line><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>
        </div>
        <div class="card-content">
            <h3>Amendes (Mois)</h3>
            <p><fmt:formatNumber value="${amendesDuMois}" type="number" minFractionDigits="2" maxFractionDigits="2"/> DH</p>
        </div>
    </div>

    <div class="card">
        <div class="card-icon icon-penalites">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
        </div>
        <div class="card-content">
            <h3>Pénalités (Mois)</h3>
            <p><fmt:formatNumber value="${penalitesDuMois}" type="number" minFractionDigits="2" maxFractionDigits="2"/> DH</p>
        </div>
    </div>
</div>

<div class="chart-container">
    <h2>Statistiques</h2>
    <canvas id="myChart"></canvas>
</div>

<script>
document.addEventListener('DOMContentLoaded', () => {
    const ctx = document.getElementById('myChart');
    if(ctx) {
        // On récupère les 3 listes de données envoyées par le servlet
        const labels = ${chartLabels};
        const coutsData = ${chartCoutsData};
        const penalitesData = ${chartPenalitesData};

        const data = {
          labels: labels,
          datasets: [
            {
              label: 'Coûts d\'emprunt (DH)',
              backgroundColor: 'rgba(59, 130, 246, 0.7)', // Bleu
              borderColor: 'rgba(59, 130, 246, 1)',
              data: coutsData, // Données des coûts
            },
            {
              label: 'Pénalités de retard (DH)',
              backgroundColor: 'rgba(239, 68, 68, 0.7)', // Rouge
              borderColor: 'rgba(239, 68, 68, 1)',
              data: penalitesData, // Données des pénalités
            }
          ]
        };

        const config = {
          type: 'bar',
          data: data,
          options: {
            scales: { 
                y: { 
                    beginAtZero: true,
                    stacked: false // false = barres groupées, true = barres empilées
                },
                x: {
                    stacked: false
                } 
            },
            plugins: { legend: { display: true } }
          }
        };
        new Chart(ctx, config);
    }
});
</script>

<%@ include file="footer.jsp" %>