<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modification Non Autorisée</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            color: #333;
            line-height: 1.6;
        }
        .container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .error-box {
            background-color: white;
            border: 2px solid #e74c3c;
            border-radius: 5px;
            padding: 30px;
            text-align: center;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        .error-icon {
            font-size: 48px;
            color: #e74c3c;
            margin-bottom: 20px;
        }
        h1 {
            color: #2c3e50;
            margin-bottom: 15px;
            font-size: 28px;
        }
        .error-message {
            color: #721c24;
            background-color: #f8d7da;
            border-left: 4px solid #f5c6cb;
            padding: 15px;
            margin: 20px 0;
            border-radius: 3px;
            text-align: left;
        }
        .error-details {
            color: #666;
            margin: 20px 0;
            text-align: left;
        }
        .error-details ul {
            margin-left: 20px;
            margin-top: 10px;
        }
        .error-details li {
            margin: 8px 0;
        }
        .info-box {
            background-color: #d1ecf1;
            border-left: 4px solid #bee5eb;
            color: #0c5460;
            padding: 15px;
            margin: 20px 0;
            border-radius: 3px;
            text-align: left;
        }
        .actions {
            margin-top: 30px;
        }
        a {
            display: inline-block;
            padding: 12px 30px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-weight: 600;
            transition: background-color 0.3s;
            margin: 0 10px;
        }
        a:hover {
            background-color: #2980b9;
        }
        .btn-secondary {
            background-color: #95a5a6;
        }
        .btn-secondary:hover {
            background-color: #7f8c8d;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="error-box">
            <div class="error-icon">⛔</div>
            <h1>Modification Non Autorisée</h1>
            
            <c:if test="${not empty erreur}">
                <div class="error-message">
                    <strong>${erreur}</strong>
                </div>
            </c:if>

            <div class="info-box">
                <strong>Raisons possibles:</strong>
                <c:if test="${not empty erreurs}">
                    <ul>
                        <c:forEach var="err" items="${erreurs}">
                            <li>${err}</li>
                        </c:forEach>
                    </ul>
                </c:if>
                <c:if test="${empty erreurs}">
                    <ul>
                        <li>Le dossier a déjà été traité (statut supérieur à DOSSIER_CREE)</li>
                        <li>Seules les demandes en statut <strong>DOSSIER_CREE</strong> peuvent être modifiées</li>
                        <li>Une fois le statut "SCAN_TERMINÉ" atteint, aucune modification n'est possible</li>
                    </ul>
                </c:if>
            </div>

            <p style="color: #666; margin: 20px 0;">
                Pour plus d'informations ou pour signaler un problème, contactez l'administrateur.
            </p>

            <div class="actions">
                <a href="/demande/nouveau?type=NOUVEAU_TITRE" class="btn-secondary">Nouvelle Demande</a>
                <a href="/" class="btn-secondary">Accueil</a>
            </div>
        </div>
    </div>
</body>
</html>
