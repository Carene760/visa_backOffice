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
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            max-width: 500px;
            width: 100%;
            text-align: center;
        }
        .success-icon {
            width: 80px;
            height: 80px;
            background: #28a745;
            border-radius: 50%;
            margin: 0 auto 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 50px;
        }
        h1 {
            color: #28a745;
            margin-bottom: 15px;
            font-size: 28px;
        }
        .message {
            color: #666;
            margin-bottom: 30px;
            font-size: 16px;
            line-height: 1.6;
        }
        .info-box {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 30px;
            border-left: 4px solid #28a745;
        }
        .info-box p {
            color: #333;
            margin-bottom: 10px;
            font-size: 14px;
        }
        .info-box .label {
            font-weight: bold;
            color: #28a745;
        }
        .info-box .value {
            color: #333;
            font-size: 18px;
            margin-top: 5px;
        }
        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: center;
        }
        a, button {
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s;
        }
        .btn-primary {
            background: #667eea;
            color: white;
        }
        .btn-primary:hover {
            background: #5568d3;
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .btn-secondary {
            background: #ecf0f1;
            color: #333;
        }
        .btn-secondary:hover {
            background: #bdc3c7;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="success-icon">✓</div>
        <h1>Demande Créée avec Succès</h1>

        <div class="message">
            <p>Votre demande de transformation de visa a été enregistrée avec succès.</p>
            <p>Veuillez conserver votre numéro de dossier pour suivre votre demande.</p>
        </div>

        <div class="info-box">
            <p><span class="label">Numéro de Dossier:</span></p>
            <p class="value">${demandeId}</p>
        </div>

        <div class="message">
            <p style="color: #999; font-size: 14px;">
                Un email de confirmation a été envoyé à votre adresse email.
            </p>
        </div>

        <div class="form-actions">
            <a href="/demande/nouveau" class="btn-secondary">Nouvelle Demande</a>
            <a href="/" class="btn-primary">Accueil</a>
        </div>
    </div>
</body>
</html>
