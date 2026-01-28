<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Inscription - Biblio-Tech</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f8f9fa;}
        .auth-container { max-width: 400px; width: 100%;}
    </style>
</head>
<body>
    <div class="container auth-container">
        <h2 style="text-align: center;">Inscription Lecteur</h2>
        <form action="${pageContext.request.contextPath}/auth" method="post">
            <input type="hidden" name="action" value="register">
            <div class="form-group">
                <label for="nom">Nom complet :</label>
                <input type="text" id="nom" name="nom" required>
            </div>
            <div class="form-group">
                <label for="email">Email :</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="motDePasse">Mot de passe :</label>
                <input type="password" id="motDePasse" name="motDePasse" required>
            </div>
            <div class="form-actions" style="margin-top: 20px;">
                <button type="submit" class="btn btn-primary" style="width: 100%;">S'inscrire</button>
            </div>
        </form>
        <p style="text-align: center; margin-top: 20px;">Déjà un compte ? <a href="login.jsp">Connectez-vous</a></p>
    </div>
</body>
</html>