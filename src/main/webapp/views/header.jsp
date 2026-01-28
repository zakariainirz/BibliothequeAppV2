<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Biblio-Tech</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
    <link href="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tom-select.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/js/tom-select.complete.min.js"></script>
    
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <header>
        <h1><a href="${pageContext.request.contextPath}/">Biblio-Tech</a></h1>
        <c:if test="${not empty sessionScope.utilisateur}">
            <nav>
                <c:if test="${sessionScope.utilisateur.role == 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                    <a href="${pageContext.request.contextPath}/livres">Gérer les Livres</a>
                    <a href="${pageContext.request.contextPath}/emprunts">Gérer les Emprunts</a>
                    <a href="${pageContext.request.contextPath}/gestionUtilisateurs">Gérer les Utilisateurs</a>
                </c:if>
                 <c:if test="${sessionScope.utilisateur.role == 'BIBLIOTHECAIRE'}">
                    <a href="${pageContext.request.contextPath}/emprunts">Gérer les Emprunts</a>
                    <a href="${pageContext.request.contextPath}/livres">Gérer les Livres</a>
                </c:if>
                 <c:if test="${sessionScope.utilisateur.role == 'LECTEUR'}">
                    <a href="${pageContext.request.contextPath}/livres?action=catalogue">Catalogue</a>
                    <a href="${pageContext.request.contextPath}/profil">Mon Profil</a>
                </c:if>
            </nav>
            <div class="user-info">
                <span>Bonjour, ${sessionScope.utilisateur.nom}</span>
                <a href="${pageContext.request.contextPath}/auth?action=logout" class="btn btn-danger">Déconnexion</a>
            </div>
        </c:if>
    </header>
    <main class="container">