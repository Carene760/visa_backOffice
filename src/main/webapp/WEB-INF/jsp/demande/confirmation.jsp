<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirmation de Demande</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Times New Roman', serif;
            background: radial-gradient(circle at 20% 0%, #fafaf7 0%, #f3f3ef 45%, #ecece8 100%);
            min-height: 100vh;
            padding: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .container {
            max-width: 650px;
            background: #ffffff;
            padding: 40px;
            border: 1px solid #9a9a9a;
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
            text-align: center;
        }
        .success-icon {
            font-size: 60px;
            margin-bottom: 20px;
        }
        h1 {
            color: #2d5016;
            margin-bottom: 30px;
            font-size: 26px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .message {
            color: #666;
            margin-bottom: 25px;
            font-size: 15px;
            line-height: 1.6;
        }
        .info-box {
            background: #f0f8f0;
            border: 1px solid #c0e0c0;
            border-radius: 4px;
            padding: 25px;
            margin-bottom: 30px;
            text-align: left;
        }
        .info-box p {
            margin: 15px 0;
            font-size: 14px;
            color: #333;
            display: flex;
            justify-content: space-between;
        }
        .info-box .label {
            font-weight: 600;
            color: #173f70;
        }
        .info-box .value {
            color: #2d5016;
            font-weight: 600;
            font-size: 16px;
        }
        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
        }
        a, button {
            padding: 12px 30px;
            border: none;
            border-radius: 2px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            transition: background-color 0.2s;
        }
        .btn-primary {
            background: #d4ce84;
            color: #fff;
        }
        .btn-primary:hover {
            background: #173f70;
        }
        .btn-secondary {
            background: #e0e0e0;
            color: #333;
        }
        .btn-secondary:hover {
            background: #d0d0d0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="success-icon"></div>
        <h1>Demande Enregistrée avec Succès</h1>

        <div class="message">
            <p>Votre demande de transformation de visa a été enregistrée avec succès dans le système.</p>
            <p>Veuillez conserver votre numéro de dossier ci-dessous pour suivre votre demande.</p>
        </div>

        <div class="info-box">
            <p>
                <span class="label">Numéro de Dossier:</span>
                <span class="value">${demandeId}</span>
            </p>
            <p>
                <span class="label">Statut:</span>
                <span class="value">DOSSIER_CREE</span>
            </p>
            <p>
                <span class="label">Date d'Enregistrement:</span>
                <span class="value"><c:out value="${createdDate}"/></span>
            </p>
        </div>

        <div class="message">
            <p style="color: #999; font-size: 13px;">
                Un email de confirmation a été envoyé à votre adresse email. 
                Vérifiez votre dossier spam si vous ne le recevez pas.
            </p>
        </div>

        <div class="form-actions">
            <a href="/demande/nouveau" class="btn-secondary">Créer une Autre Demande</a>
            <a href="/" class="btn-primary">Retour à l'Accueil</a>
        </div>
    </div>
</body>
</html>
