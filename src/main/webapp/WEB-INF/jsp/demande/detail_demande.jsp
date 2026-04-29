<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détail du Dossier #${demande.id}</title>
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
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        header {
            background-color: white;
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #2c3e50;
            margin-bottom: 10px;
        }

        .dossier-reference {
            font-size: 14px;
            color: #666;
        }

        .cards-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }

        .card {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .card h2 {
            font-size: 16px;
            color: #2c3e50;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #3498db;
        }

        .alert {
            padding: 15px;
            margin: 15px 0;
            border-radius: 5px;
            border-left: 4px solid;
        }

        .alert-info {
            background-color: #d1ecf1;
            border-left-color: #0c5460;
            color: #0c5460;
        }

        .alert-warning {
            background-color: #fff3cd;
            border-left-color: #856404;
            color: #856404;
        }

        .alert-danger {
            background-color: #f8d7da;
            border-left-color: #721c24;
            color: #721c24;
        }

        .alert-success {
            background-color: #d4edda;
            border-left-color: #155724;
            color: #155724;
        }

        .status-badge {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: 600;
        }

        .status-dossier-cree {
            background-color: #e3f2fd;
            color: #1976d2;
        }

        .status-scan-termine {
            background-color: #f3e5f5;
            color: #6a1b9a;
        }

        .status-visa-approuvee {
            background-color: #e8f5e9;
            color: #2e7d32;
        }

        .status-refuse {
            background-color: #ffebee;
            color: #c62828;
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
            width: 40%;
        }

        .detail-value {
            color: #666;
            width: 60%;
            text-align: right;
        }

        .timeline {
            position: relative;
            padding: 20px 0;
        }

        .timeline-item {
            display: flex;
            margin-bottom: 20px;
            position: relative;
        }

        .timeline-marker {
            width: 40px;
            height: 40px;
            background-color: #3498db;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 600;
            margin-right: 20px;
            flex-shrink: 0;
        }

        .timeline-marker.completed {
            background-color: #27ae60;
        }

        .timeline-content {
            flex: 1;
        }

        .timeline-date {
            font-size: 12px;
            color: #95a5a6;
        }

        .timeline-status {
            font-weight: 600;
            color: #2c3e50;
            margin: 5px 0;
        }

        .completion-bar {
            background-color: #ecf0f1;
            border-radius: 5px;
            height: 10px;
            margin: 10px 0;
            overflow: hidden;
        }

        .completion-fill {
            height: 100%;
            background-color: #27ae60;
            transition: width 0.3s;
        }

        .source-demand {
            background-color: #f9f9f9;
            padding: 15px;
            border-left: 4px solid #3498db;
            margin: 10px 0;
            border-radius: 3px;
        }

        .source-demand a {
            color: #3498db;
            text-decoration: none;
            font-weight: 600;
        }

        .source-demand a:hover {
            text-decoration: underline;
        }

        .mode-indicator {
            display: inline-flex;
            align-items: center;
            padding: 8px 12px;
            background-color: #f0f0f0;
            border-radius: 3px;
            margin: 5px 0;
            font-size: 13px;
        }

        .checkbox-indicator {
            display: inline-block;
            width: 16px;
            height: 16px;
            border: 2px solid #3498db;
            border-radius: 3px;
            margin-right: 8px;
            text-align: center;
            line-height: 14px;
            color: white;
            background-color: #3498db;
            font-size: 12px;
        }

        .checkbox-indicator.unchecked {
            background-color: white;
            color: #bdc3c7;
        }

        .pieceslist {
            margin: 10px 0;
        }

        .pieceslist ul {
            list-style: none;
            padding-left: 0;
        }

        .pieceslist li {
            padding: 8px 0;
            border-bottom: 1px solid #eee;
        }

        .pieceslist li:last-child {
            border-bottom: none;
        }

        .piece-icon {
            display: inline-block;
            width: 20px;
            text-align: center;
            margin-right: 8px;
        }

        .obligatoire {
            color: #e74c3c;
        }

        .optionnelle {
            color: #f39c12;
        }

        .present {
            color: #27ae60;
        }

        .actions {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }

        a.btn, button.btn {
            display: inline-block;
            padding: 10px 20px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
            transition: background-color 0.3s;
        }

        a.btn:hover, button.btn:hover {
            background-color: #2980b9;
        }

        a.btn.secondary {
            background-color: #95a5a6;
        }

        a.btn.secondary:hover {
            background-color: #7f8c8d;
        }

        a.btn.danger {
            background-color: #e74c3c;
        }

        a.btn.danger:hover {
            background-color: #c0392b;
        }

        .full-width {
            grid-column: 1 / -1;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Détail du Dossier</h1>
            <div class="dossier-reference">
                <strong>Dossier #${demande.id}</strong>
                <span> | Créé le ${demande.dateDemande}</span>
                <span class="status-badge ${fn:toLowerCase(demande.statutDemande.libelle) == 'dossier_cree' ? 'status-dossier-cree' : fn:toLowerCase(demande.statutDemande.libelle) == 'scan_termine' ? 'status-scan-termine' : fn:toLowerCase(demande.statutDemande.libelle) == 'visa_approuvee' ? 'status-visa-approuvee' : 'status-refuse'}">
                    ${demande.statutDemande.libelle}
                </span>
            </div>
        </header>

        <!-- Alert: Modification Lock (if not DOSSIER_CREE) -->
        <c:if test="${demande.statutDemande.libelle != 'DOSSIER_CREE'}">
            <div class="alert alert-danger">
                <strong>⛔ Modification non autorisée</strong>
                <p>Ce dossier a atteint le statut "${demande.statutDemande.libelle}". Aucune modification n'est possible après DOSSIER_CREE.</p>
            </div>
        </c:if>

        <!-- Grid Cards -->
        <div class="cards-grid">

            <!-- Card 1: Information Demandeur -->
            <div class="card">
                <h2>Information du Demandeur</h2>
                <div class="detail-row">
                    <span class="detail-label">Nom Complet:</span>
                    <span class="detail-value">${demande.demandeur.nom} ${demande.demandeur.prenom}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Email:</span>
                    <span class="detail-value">${demande.demandeur.email}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Téléphone:</span>
                    <span class="detail-value">${demande.demandeur.telephone}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Nationalité:</span>
                    <span class="detail-value">${demande.demandeur.nationalite.libelle}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Adresse:</span>
                    <span class="detail-value">${demande.demandeur.adresseMadagascar}</span>
                </div>
            </div>

            <!-- Card 2: Source de Demande -->
            <div class="card">
                <h2>Source de Demande</h2>
                <div class="detail-row">
                    <span class="detail-label">Mode:</span>
                    <span class="detail-value">
                        ${demande.avecDonneesAnterieures ? '✓ Avec données antérieures' : '✗ Sans données antérieures'}
                    </span>
                </div>

                <c:if test="${demande.demandeSource != null}">
                    <div class="source-demand">
                        <strong>Basée sur la demande source:</strong>
                        <br>
                        <a href="/demande/${demande.demandeSource.id}/detail">Dossier source #${demande.demandeSource.id}</a>
                        <br>
                        <small>Type: ${demande.demandeSource.typeDemande.libelle}</small>
                    </div>
                </c:if>

                <c:if test="${demande.demandeSource == null && demande.typeDemande.libelle == 'DUPLICATA'}">
                    <div class="alert alert-warning">
                        <strong>⚠ Aucune source identifiée</strong>
                        <p>Ce dossier est en mode "sans données antérieures" et n'a pas de source liée. Une demande base sera créée automatiquement lors du traitement.</p>
                    </div>
                </c:if>

                <c:if test="${demande.estBaseGeneree}">
                    <div class="alert alert-info">
                        <strong>ℹ Demande Base Générée</strong>
                        <p>Ceci est une demande base générée automatiquement pour les duplicata/transfert sans antécédents.</p>
                    </div>
                </c:if>

                <div class="mode-indicator">
                    <span class="checkbox-indicator ${demande.avecDonneesAnterieures ? '' : 'unchecked'}">
                        ${demande.avecDonneesAnterieures ? '✓' : ''}
                    </span>
                    Avec données antérieures
                </div>
            </div>

            <!-- Card 3: Type et Motif -->
            <div class="card">
                <h2>Type et Motif</h2>
                <div class="detail-row">
                    <span class="detail-label">Type de Demande:</span>
                    <span class="detail-value">${demande.typeDemande.libelle}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Motif:</span>
                    <span class="detail-value">${demande.typeMotif.libelle}</span>
                </div>
                <c:if test="${demande.sousTypeDemande != null}">
                    <div class="detail-row">
                        <span class="detail-label">Sous-Type:</span>
                        <span class="detail-value">${demande.sousTypeDemande.libelle}</span>
                    </div>
                </c:if>
            </div>

            <!-- Card 4: Complétude du Dossier -->
            <div class="card">
                <h2>Complétude du Dossier</h2>
                <c:if test="${not empty completion}">
                    <div class="detail-row">
                        <span class="detail-label">Statut:</span>
                        <span class="detail-value ${completion.success ? 'present' : 'obligatoire'}">
                            ${completion.success ? '✓ Complet' : '✗ Incomplet'}
                        </span>
                    </div>
                    <div class="completion-bar">
                        <div class="completion-fill" style="width: ${completion.success ? '100' : '50'}%"></div>
                    </div>
                    <p style="font-size: 13px; color: #666; margin-top: 10px;">
                        ${completion.message}
                    </p>
                    <c:if test="${not empty completion.errors}">
                        <ul style="margin-left: 20px; margin-top: 10px; font-size: 13px;">
                            <c:forEach var="err" items="${completion.errors}">
                                <li style="margin: 5px 0; ${fn:contains(err, 'OBLIGATOIRE') ? 'color: #e74c3c' : 'color: #f39c12'}">
                                    ${err}
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </c:if>
            </div>

            <!-- Card 5: Documents Justificatifs -->
            <div class="card">
                <h2>Documents Justificatifs</h2>
                <c:if test="${not empty pieces}">
                    <div class="pieceslist">
                        <ul>
                            <c:forEach var="piece" items="${pieces}">
                                <li>
                                    <span class="piece-icon ${piece.typeDocument.obligatoire ? 'obligatoire' : 'optionnelle'}">
                                        ${piece.present ? '✓' : '○'}
                                    </span>
                                    <strong>${piece.typeDocument.libelle}</strong>
                                    <span style="font-size: 12px; color: #999;">
                                        (${piece.typeDocument.obligatoire ? 'obligatoire' : 'optionnel'})
                                    </span>
                                    <span style="float: right; font-size: 12px; color: #27ae60;">
                                        ${piece.present ? 'Reçu' : 'Manquant'}
                                    </span>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
                <c:if test="${empty pieces}">
                    <p style="color: #999; font-style: italic;">Aucun document à fournir.</p>
                </c:if>
            </div>

            <!-- Card 6: Historique des Statuts (full width) -->
            <div class="card full-width">
                <h2>Historique des Statuts</h2>
                <div class="timeline">
                    <c:if test="${not empty historique}">
                        <c:forEach var="hist" items="${historique}">
                            <div class="timeline-item">
                                <div class="timeline-marker completed">
                                    <span>✓</span>
                                </div>
                                <div class="timeline-content">
                                    <div class="timeline-status">
                                        ${hist.statut.libelle}
                                    </div>
                                    <div class="timeline-date">
                                        ${hist.dateChangement}
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:if>
                    <c:if test="${empty historique}">
                        <p style="color: #999; font-style: italic;">Aucun changement d'état enregistré.</p>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Actions -->
        <div class="actions" style="justify-content: center; margin-top: 30px;">
            <c:if test="${demande.statutDemande.libelle == 'DOSSIER_CREE'}">
                <a href="/demande/${demande.id}/modifier" class="btn">Modifier Dossier</a>
            </c:if>
            <a href="/demande/${demande.id}/generer-recepisse" class="btn" download="recepisse_${demande.id}.pdf">Télécharger Récépissé PDF</a>
            <a href="/demande/nouveau" class="btn secondary">Nouvelle Demande</a>
        </div>
    </div>
</body>
</html>
