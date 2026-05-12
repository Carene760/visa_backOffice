<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload scanner - Dossier #<c:out value="${demandeId}"/></title>
    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(180deg, #f7f7f2 0%, #eceae1 100%);
            color: #20252f;
        }
        .page {
            max-width: 1100px;
            margin: 0 auto;
            padding: 28px 18px 40px;
        }
        .card {
            background: #fff;
            border: 1px solid #d6d6cd;
            box-shadow: 0 8px 24px rgba(20, 27, 45, 0.08);
            padding: 24px;
        }
        h1 {
            margin: 0 0 10px;
            color: #173f70;
        }
        p {
            line-height: 1.6;
            color: #4a4a4a;
        }
        .actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            margin-top: 18px;
        }
        a.button, button {
            display: inline-block;
            padding: 11px 16px;
            border-radius: 2px;
            border: 0;
            text-decoration: none;
            font-weight: 700;
            cursor: pointer;
        }
        .primary { background: #173f70; color: #fff; }
        .secondary { background: #ecede8; color: #252a36; }
        .notice {
            margin-top: 16px;
            padding: 12px 14px;
            background: #f9f6e8;
            border: 1px solid #d8ce93;
            color: #5b4f14;
        }
    </style>
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>Étape upload scanner</h1>
            <p>
                Dossier n° <strong><c:out value="${demandeId}"/></strong> : la photo et la signature ont été enregistrées.
                Cette étape permet maintenant de poursuivre le dossier dans le flux Spring Boot sans sortir de l'application.
            </p>

            <div class="notice">
                La partie d'upload des fichiers sera branchée ensuite. Pour l'instant, cette page sert de point de passage
                dans l'application afin d'éviter le 404 après « Soumettre ».
            </div>

            <div class="actions">
                <a class="button primary" href="/demande/<c:out value='${demandeId}'/>/detail">Voir le dossier</a>
                <a class="button secondary" href="/demande/<c:out value='${demandeId}'/>/photo-signature-capture">Revenir à la capture photo/signature</a>
            </div>
        </div>
    </div>
</body>
</html>