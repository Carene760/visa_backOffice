<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détail de la Demande</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
        }

        .container {
            max-width: 900px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #333;
            margin-bottom: 10px;
        }

        .status-badge {
            display: inline-block;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: bold;
            margin-bottom: 20px;
        }

        .status-badge.pending {
            background-color: #fff3cd;
            color: #856404;
        }

        .status-badge.warning {
            background-color: #f8d7da;
            color: #721c24;
        }

        .section {
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 1px solid #ddd;
        }

        .section:last-child {
            border-bottom: none;
        }

        h2 {
            color: #0066cc;
            font-size: 16px;
            margin-bottom: 15px;
        }

        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .info-item {
            padding: 10px 0;
        }

        .info-label {
            color: #666;
            font-size: 12px;
            text-transform: uppercase;
            margin-bottom: 3px;
        }

        .info-value {
            color: #333;
            font-weight: bold;
            font-size: 14px;
        }

        .alert {
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }

        .alert-warning {
            background-color: #fff3cd;
            border-left: 4px solid #ffc107;
            color: #856404;
        }

        .alert-success {
            background-color: #d4edda;
            border-left: 4px solid #28a745;
            color: #155724;
        }

        .documents-list {
            background-color: #f9f9f9;
            padding: 15px;
            border-radius: 4px;
        }

        .document-item {
            padding: 10px;
            margin-bottom: 5px;
            background-color: white;
            border-radius: 3px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .document-status {
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: bold;
        }

        .document-status.present {
            background-color: #d4edda;
            color: #155724;
        }

        .document-status.missing {
            background-color: #f8d7da;
            color: #721c24;
        }

        .action-buttons {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }

        button {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            font-size: 14px;
        }

        .btn-primary {
            background-color: #0066cc;
            color: white;
        }

        .btn-primary:hover {
            background-color: #0052a3;
        }

        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #5a6268;
        }

        .btn-danger {
            background-color: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background-color: #c82333;
        }

        .missing-documents {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            padding: 15px;
            border-radius: 4px;
            margin-top: 15px;
        }

        .missing-documents h3 {
            color: #721c24;
            margin-bottom: 10px;
        }

        .missing-documents ul {
            margin-left: 20px;
            color: #721c24;
        }

        .missing-documents li {
            margin-bottom: 5px;
        }

        .loading {
            text-align: center;
            padding: 40px;
            color: #666;
        }

        .error {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            padding: 15px;
            border-radius: 4px;
            color: #721c24;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Détail de la Demande de Visa</h1>
        
        <div id="loadingSpinner" class="loading">
            Chargement des données...
        </div>

        <div id="demandeContent" style="display: none;">
            <!-- Statut -->
            <div id="statusSection"></div>

            <!-- Alertes -->
            <div id="alertsSection"></div>

            <!-- Informations Demandeur -->
            <div class="section">
                <h2>Informations du Demandeur</h2>
                <div class="info-grid" id="infosPdemandeur"></div>
            </div>

            <!-- Informations de la Demande -->
            <div class="section">
                <h2>Informations de la Demande</h2>
                <div class="info-grid" id="infosDemande"></div>
            </div>

            <!-- Statut et Progression -->
            <div class="section">
                <h2>Statut et Progression</h2>
                <div class="info-grid" id="infosStatut"></div>
                <div id="missingDocumentsAlert"></div>
            </div>

            <!-- Documents -->
            <div class="section">
                <h2>Documents Fournis</h2>
                <div id="documentsList" class="documents-list"></div>
            </div>

            <!-- Actions -->
            <div class="action-buttons">
                <button class="btn-primary" onclick="modifierDemande()">Modifier la Demande</button>
                <button class="btn-secondary" onclick="telechargerRecapitulatif()">Télécharger le Récapitulatif</button>
                <button class="btn-secondary" onclick="retourner()">Retour</button>
            </div>
        </div>

        <div id="errorContent" style="display: none;" class="error">
            <p id="errorMessage"></p>
        </div>
    </div>

    <script>
        let demandeId = null;

        // Récupérer l'ID de la demande depuis l'URL
        function getDemandeIdFromUrl() {
            const pathParts = window.location.pathname.split('/');
            return pathParts[pathParts.length - 1];
        }

        // Charger les données de la demande
        function chargerDemande() {
            demandeId = getDemandeIdFromUrl();
            
            Promise.all([
                fetch(`/api/demandes/${demandeId}`).then(r => r.json()),
                fetch(`/api/demandes/${demandeId}/statut`).then(r => r.json()),
                fetch(`/api/demandes/${demandeId}/documents-manquants`).then(r => r.json())
            ])
            .then(([demande, statut, documentsManquants]) => {
                afficherDemande(demande, statut, documentsManquants);
                document.getElementById('loadingSpinner').style.display = 'none';
                document.getElementById('demandeContent').style.display = 'block';
            })
            .catch(error => {
                console.error('Erreur:', error);
                afficherErreur('Impossible de charger les données de la demande.');
            });
        }

        // Afficher les données de la demande
        function afficherDemande(demande, statut, documentsManquants) {
            // Statut
            const statusSection = document.getElementById('statusSection');
            const statusBadgeClass = demande.completDossier ? 'status-badge' : 'status-badge warning';
            statusSection.innerHTML = `
                <div class="status-badge ${statusBadgeClass}">
                    Statut: ${statut?.libelle || 'Dossier créé'}
                </div>
            `;

            // Alertes
            const alertsSection = document.getElementById('alertsSection');
            let alertsHtml = '';
            
            if (documentsManquants && documentsManquants.length > 0) {
                alertsHtml = `
                    <div class="alert alert-warning">
                        <strong>Attention :</strong> Vous avez ${documentsManquants.length} document(s) manquant(s) pour compléter votre dossier.
                    </div>
                `;
            } else {
                alertsHtml = `
                    <div class="alert alert-success">
                        <strong>Félicitations :</strong> Tous les documents obligatoires sont présents.
                    </div>
                `;
            }
            alertsSection.innerHTML = alertsHtml;

            // Informations Demandeur
            const demandeur = demande.demandeur;
            const infosDemandeur = document.getElementById('infosPdemandeur');
            infosDemandeur.innerHTML = `
                <div class="info-item">
                    <div class="info-label">Nom</div>
                    <div class="info-value">${demandeur?.nom || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Prénom</div>
                    <div class="info-value">${demandeur?.prenom || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Email</div>
                    <div class="info-value">${demandeur?.email || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Téléphone</div>
                    <div class="info-value">${demandeur?.telephone || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Date de Naissance</div>
                    <div class="info-value">${demandeur?.dateNaissance || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Adresse à Madagascar</div>
                    <div class="info-value">${demandeur?.adresseMadagascar || 'N/A'}</div>
                </div>
            `;

            // Informations Demande
            const infosDemande = document.getElementById('infosDemande');
            infosDemande.innerHTML = `
                <div class="info-item">
                    <div class="info-label">ID Demande</div>
                    <div class="info-value">${demande.id || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Date de Création</div>
                    <div class="info-value">${new Date(demande.dateDemande).toLocaleDateString('fr-FR') || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Type de Demande</div>
                    <div class="info-value">${demande.typeDemande?.libelle || 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Motif</div>
                    <div class="info-value">${demande.typeMotif?.libelle || 'N/A'}</div>
                </div>
            `;

            // Infos Statut
            const infosStatut = document.getElementById('infosStatut');
            const documentsHTML = documentsManquants && documentsManquants.length > 0 ? `
                <div class="missing-documents">
                    <h3>Documents manquants :</h3>
                    <ul>
                        ${documentsManquants.map(d => `<li>${d}</li>`).join('')}
                    </ul>
                </div>
            ` : '<p style="color: #28a745; font-weight: bold;">✓ Tous les documents obligatoires sont présents</p>';
            
            infosStatut.innerHTML = documentsHTML;

            // Documents
            const documentsList = document.getElementById('documentsList');
            documentsList.innerHTML = '<p style="color: #666;">Les documents seront affichés lors de l\'intégration de la gestion des pièces jointes.</p>';
        }

        // Afficher une erreur
        function afficherErreur(message) {
            document.getElementById('loadingSpinner').style.display = 'none';
            document.getElementById('demandeContent').style.display = 'none';
            document.getElementById('errorContent').style.display = 'block';
            document.getElementById('errorMessage').textContent = message;
        }

        // Actions
        function modifierDemande() {
            window.location.href = `/modifier-demande/${demandeId}`;
        }

        function telechargerRecapitulatif() {
            alert('Fonctionnalité à implémenter');
        }

        function retourner() {
            window.location.href = '/';
        }

        // Charger les données au chargement de la page
        document.addEventListener('DOMContentLoaded', chargerDemande);
    </script>
</body>
</html>
