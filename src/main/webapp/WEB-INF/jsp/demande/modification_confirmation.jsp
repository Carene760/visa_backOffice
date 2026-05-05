<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirmation de Modification</title>
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
        .confirmation-box {
            background-color: white;
            border: 2px solid #28a745;
            border-radius: 5px;
            padding: 30px;
            text-align: center;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        .success-icon {
            font-size: 48px;
            color: #28a745;
            margin-bottom: 20px;
        }
        h1 {
            color: #2c3e50;
            margin-bottom: 15px;
            font-size: 28px;
        }
        .success-message {
            color: #155724;
            background-color: #d4edda;
            border-left: 4px solid #c3e6cb;
            padding: 15px;
            margin: 20px 0;
            border-radius: 3px;
        }
        .confirmation-details {
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            padding: 20px;
            margin: 20px 0;
            border-radius: 3px;
            text-align: left;
        }
        .detail-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        .detail-row:last-child {
            border-bottom: none;
        }
        .detail-label {
            font-weight: 600;
            color: #2c3e50;
        }
        .detail-value {
            color: #666;
        }
        .info-box {
            background-color: #d1ecf1;
            border-left: 4px solid #bee5eb;
            color: #0c5460;
            padding: 15px;
            margin: 20px 0;
            border-radius: 3px;
        }
        .actions {
            margin-top: 30px;
        }
        a, button {
            display: inline-block;
            padding: 12px 30px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border: none;
            border-radius: 4px;
            font-weight: 600;
            transition: background-color 0.3s;
            margin: 0 10px;
            cursor: pointer;
            font-size: 16px;
        }
        a:hover, button:hover {
            background-color: #2980b9;
        }
        .btn-secondary {
            background-color: #95a5a6;
        }
        .btn-secondary:hover {
            background-color: #7f8c8d;
        }
        .btn-primary {
            background-color: #28a745;
        }
        .btn-primary:hover {
            background-color: #218838;
        }
        .next-steps {
            text-align: left;
            background-color: #f0f7ff;
            border-left: 4px solid #0066cc;
            padding: 15px;
            margin: 20px 0;
            border-radius: 3px;
        }
        .next-steps h3 {
            color: #0066cc;
            margin-bottom: 10px;
        }
        .next-steps ul {
            margin-left: 20px;
        }
        .next-steps li {
            margin: 8px 0;
            color: #333;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="confirmation-box">
            <div class="success-icon">✓</div>
            <h1>Modification Enregistrée Avec Succès</h1>
            
            <div class="success-message">
                <strong>${message}</strong>
            </div>

            <div class="confirmation-details">
                <div class="detail-row">
                    <span class="detail-label">Numéro de Dossier:</span>
                    <span class="detail-value">#${demandeId}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Date de Modification:</span>
                    <span class="detail-value">${modifiedDate}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Status Actuel:</span>
                    <span class="detail-value">DOSSIER_CREE</span>
                </div>
            </div>

            <div class="info-box">
                <strong>Important:</strong> Votre modification a été enregistrée. Le dossier reste en statut DOSSIER_CREE et peut être modifié à nouveau si nécessaire.
            </div>

            <div class="next-steps">
                <h3>Prochaines étapes:</h3>
                <ul>
                    <li>Consultez le détail de votre dossier</li>
                    <li>Téléchargez le récépissé (PDF)</li>
                    <li>Soumettez votre dossier pour traitement</li>
                    <li>Suivez l'évolution de votre demande</li>
                </ul>
            </div>

            <div class="actions">
                <a href="/demande/${demandeId}/generer-recepisse" class="btn-primary" download="recepisse_${demandeId}.pdf">
                    Télécharger Récépissé
                </a>
                <a href="/demande/nouveau?type=NOUVEAU_TITRE" class="btn-secondary">Nouvelle Demande</a>
                <a href="/" class="btn-secondary">Accueil</a>
            </div>
        </div>
    </div>
</body>
</html>
