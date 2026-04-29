<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Demande Enregistrée</title>
</head>
<body>
    <h1>Votre demande a été enregistrée avec succès!</h1>
    
    <div class="success-message">
        <p><strong>Numéro de dossier:</strong> ${demandeId}</p>
        <p><strong>Statut:</strong> ENREGISTREE</p>
    </div>
    
    <button onclick="window.location.href='/'">Retour</button>
</body>
</html>