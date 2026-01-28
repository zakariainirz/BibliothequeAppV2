<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - Biblio-Tech</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f8f9fa;}
        .auth-container { max-width: 400px; width: 100%;}
    </style>
</head>
<body>
    <div class="container auth-container">
        <h2 style="text-align: center;">Biblio-Tech - Connexion</h2>

        <c:if test="${not empty errorMessage}">
            <p class="error-message" style="color: #721c24; background-color: #f8d7da; border-color: #f5c6cb; padding: .75rem 1.25rem; margin-bottom: 1rem; border: 1px solid transparent; border-radius: .25rem;">${errorMessage}</p>
        </c:if>
        <c:if test="${not empty successMessage}">
            <p class="success-message" style="color: #155724; background-color: #d4edda; border-color: #c3e6cb; padding: .75rem 1.25rem; margin-bottom: 1rem; border: 1px solid transparent; border-radius: .25rem;">${successMessage}</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth" method="post">
            <input type="hidden" name="action" value="login">
            <div class="form-group">
                <label for="email">Email :</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="motDePasse">Mot de passe :</label>
                <input type="password" id="motDePasse" name="motDePasse" required>
            </div>
            <div class="form-actions" style="margin-top:20px;">
                <button type="submit" class="btn btn-primary" style="width:100%;">Se connecter</button>
            </div>
        </form>
        <p style="text-align: center; margin-top: 20px;">Pas encore de compte ? <a href="register.jsp">Inscrivez-vous ici</a></p>
    </div>
</body>
</html>