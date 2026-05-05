<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .search-container {
        background: white;
        padding: 28px;
        border: 1px solid #9a9a9a;
        border-radius: 4px;
        margin-bottom: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .search-title {
        font-size: 22px;
        font-weight: 700;
        color: var(--text-main);
        margin-bottom: 20px;
        text-transform: uppercase;
        border-bottom: 2px solid var(--accent-teal);
        padding-bottom: 12px;
    }

    .search-form {
        display: grid;
        grid-template-columns: 2fr 2fr 1fr;
        gap: 16px;
        margin-bottom: 20px;
        align-items: flex-end;
    }

    .search-form .form-group {
        display: flex;
        flex-direction: column;
    }

    .search-form label {
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
        font-size: 14px;
    }

    .search-form input,
    .search-form select {
        padding: 10px 12px;
        border: 1px solid #8f8f8f;
        border-radius: 2px;
        font-size: 14px;
        background: #fff;
    }

    .search-form input:focus,
    .search-form select:focus {
        outline: none;
        border-color: var(--accent);
        background-color: #fcfdff;
    }

    .btn-search {
        padding: 10px 24px;
        background: var(--accent-teal);
        color: white;
        border: none;
        border-radius: 2px;
        font-weight: 600;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .btn-search:hover {
        background: #16a085;
    }

    .results-section {
        background: white;
        padding: 28px;
        border: 1px solid #9a9a9a;
        border-radius: 4px;
        margin-top: 20px;
    }

    .transfer-panel {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
        margin-top: 18px;
    }

    .transfer-card {
        border: 1px solid #d7d7d7;
        border-radius: 4px;
        padding: 18px;
        background: #fcfcfc;
    }

    .transfer-card h4 {
        margin-top: 0;
        margin-bottom: 12px;
        color: var(--text-main);
        border-left: 4px solid var(--accent-teal);
        padding-left: 10px;
    }

    .transfer-form {
        margin-top: 18px;
        display: grid;
        gap: 16px;
    }

    .transfer-form .form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px;
    }

    .transfer-form .form-group {
        display: flex;
        flex-direction: column;
    }

    .transfer-form label {
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
        font-size: 14px;
    }

    .transfer-form input,
    .transfer-form select {
        padding: 10px 12px;
        border: 1px solid #8f8f8f;
        border-radius: 2px;
        font-size: 14px;
        background: #fff;
    }

    .transfer-actions {
        display: flex;
        gap: 12px;
        align-items: center;
        margin-top: 10px;
    }

    .btn-linker {
        padding: 10px 24px;
        background: var(--accent-dark);
        color: white;
        border: none;
        border-radius: 2px;
        font-weight: 700;
        cursor: pointer;
    }

    .btn-linker:hover {
        opacity: 0.92;
    }

    .results-title {
        font-size: 18px;
        font-weight: 700;
        color: var(--text-main);
        margin-bottom: 16px;
        border-left: 4px solid var(--accent);
        padding-left: 12px;
    }

    .results-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 16px;
    }

    .results-table thead {
        background: var(--accent-dark);
        color: white;
    }

    .results-table th {
        padding: 12px;
        text-align: left;
        font-weight: 600;
        border: 1px solid #ddd;
    }

    .results-table td {
        padding: 12px;
        border: 1px solid #e0e0e0;
    }

    .results-table tbody tr:hover {
        background: #f5f5f5;
    }

    .btn-select {
        padding: 8px 16px;
        background: var(--accent-teal);
        color: white;
        border: none;
        border-radius: 2px;
        cursor: pointer;
        font-weight: 600;
        font-size: 13px;
        transition: background-color 0.2s;
    }

    .btn-select:hover {
        background: #16a085;
    }

    .info-box {
        background: #e7f3ff;
        border: 1px solid #b3d9ff;
        color: #004085;
        padding: 16px;
        border-radius: 4px;
        margin-bottom: 20px;
    }

    .info-box h3 {
        margin-top: 0;
        margin-bottom: 8px;
    }

    .no-results {
        text-align: center;
        padding: 40px;
        color: var(--text-soft);
        font-size: 16px;
    }

    .search-hint {
        font-size: 13px;
        color: var(--text-soft);
        margin-top: 8px;
    }

    @media (max-width: 1024px) {
        .search-form {
            grid-template-columns: 1fr;
        }
    }
</style>

<div class="search-container">
    <h2 class="search-title">Rechercher un Visa - Transfert</h2>
    
    <div class="info-box">
        <h3>Mode Transfert de Visa</h3>
        <p>Recherchez une demande avec visa valide, puis sélectionnez-la pour afficher le passeport actuel, le visa à transférer et saisir le nouveau passeport avant de lier les deux.</p>
        <c:if test="${not empty message}">
            <div style="margin-top: 12px; padding: 12px 14px; background: #d4edda; border: 1px solid #c3e6cb; color: #155724; border-radius: 4px;">
                ${message}
            </div>
        </c:if>
        <c:if test="${not empty erreur}">
            <div style="margin-top: 12px; padding: 12px 14px; background: #f8d7da; border: 1px solid #f1b0b7; color: #842029; border-radius: 4px;">
                ${erreur}
            </div>
        </c:if>
    </div>

    <form method="get" action="/demande/transfert" class="search-form">
        <div class="form-group">
            <label for="search_criterion">Critère de Recherche *</label>
            <select id="search_criterion" name="criterion" required>
                <option value="">-- Sélectionner un critère --</option>
                <option value="numeroDemande">Numéro de Demande</option>
                <option value="numeroVisa">Numéro de Visa</option>
                <option value="nomDemandeur">Nom du Demandeur</option>
            </select>
            <span class="search-hint">Sélectionnez le type de recherche</span>
            <span class="search-hint" style="display:block; color:#0b6b3a; font-weight:600;">Filtre actif: seules les demandes avec visa VALIDE et passeport lié sont affichées.</span>
        </div>

        <div class="form-group">
            <label for="search_value">Valeur de Recherche *</label>
            <input type="text" id="search_value" name="searchValue" placeholder="Entrez la valeur..." required>
            <span class="search-hint">Ex: "DEMO-2026-001" ou "VIS-123456"</span>
        </div>

        <div class="form-group">
            <button type="submit" class="btn-search">Rechercher</button>
        </div>
    </form>
</div>

<!-- Section Résultats / Formulaire -->
<c:if test="${not empty demandeSelectionnee}">
    <div class="results-section">
        <h3 class="results-title">Formulaire de Transfert</h3>
        <p style="margin-top: 0; color: var(--text-soft);">La demande sélectionnée remplace la liste. Vous pouvez maintenant compléter le nouveau passeport et lier le visa.</p>

        <div class="transfer-panel">
            <div class="transfer-card">
                <h4>Demande et éléments à transférer</h4>
                <table style="width: 100%; border-collapse: collapse; font-size: 14px;">
                    <tr style="border-bottom: 1px solid #e5e5e5;">
                        <td style="padding: 8px 0; font-weight: 600; width: 40%;">Numéro demande</td>
                        <td style="padding: 8px 0;">#${demandeSelectionnee.id}</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #e5e5e5;">
                        <td style="padding: 8px 0; font-weight: 600;">Demandeur</td>
                        <td style="padding: 8px 0;">${demandeSelectionnee.demandeur.nom} ${demandeSelectionnee.demandeur.prenom}</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #e5e5e5;">
                        <td style="padding: 8px 0; font-weight: 600;">Ancien passeport</td>
                        <td style="padding: 8px 0;">
                            <c:choose>
                                <c:when test="${not empty demandeSelectionnee.passeport}">
                                    ${demandeSelectionnee.passeport.numero}
                                </c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr style="border-bottom: 1px solid #e5e5e5;">
                        <td style="padding: 8px 0; font-weight: 600;">Visa à transférer</td>
                        <td style="padding: 8px 0;">
                            <c:choose>
                                <c:when test="${not empty demandeSelectionnee.visa}">
                                    ${demandeSelectionnee.visa.reference}
                                    <c:if test="${not empty demandeSelectionnee.visa.dateExpiration}">
                                        <span style="color: var(--text-soft);">(expire le ${demandeSelectionnee.visa.dateExpiration})</span>
                                    </c:if>
                                </c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px 0; font-weight: 600;">Statut</td>
                        <td style="padding: 8px 0;">
                            <c:choose>
                                <c:when test="${not empty demandeSelectionnee.statut}">
                                    ${demandeSelectionnee.statut.libelle}
                                </c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="transfer-card">
                <h4>Nouveau passeport</h4>
                <form method="post" action="/demande/transfert/lier" class="transfer-form">
                    <input type="hidden" name="demandeId" value="${demandeSelectionnee.id}">
                    <div class="form-group">
                        <label for="numeroPasseport">Numéro du nouveau passeport *</label>
                        <input type="text" id="numeroPasseport" name="numeroPasseport" required placeholder="Saisir le numéro du nouveau passeport">
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="dateDelivrancePasseport">Date de délivrance *</label>
                            <input type="date" id="dateDelivrancePasseport" name="dateDelivrancePasseport" required>
                        </div>
                        <div class="form-group">
                            <label for="dateExpirationPasseport">Date d'expiration *</label>
                            <input type="date" id="dateExpirationPasseport" name="dateExpirationPasseport" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="motifTransfertId">Motif de transfert *</label>
                        <select id="motifTransfertId" name="motifTransfertId" required>
                            <option value="">-- Sélectionner un motif --</option>
                            <c:forEach var="m" items="${motifsTransfert}">
                                <option value="${m.id}">${m.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="transfer-actions">
                        <button type="submit" class="btn-linker">Lier</button>
                        <a href="/demande/transfert" class="btn-select" style="text-decoration: none; display: inline-block;">Annuler</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>

<c:if test="${empty demandeSelectionnee and not empty searchResults}">
    <div class="results-section">
        <h3 class="results-title">Résultats de Recherche</h3>

        <table class="results-table">
            <thead>
                <tr>
                    <th>Numéro Demande</th>
                    <th>Demandeur</th>
                    <th>Visa Référence</th>
                    <th>Date Expiration Visa</th>
                    <th>Passeport (Ancien)</th>
                    <th>Statut</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="demande" items="${searchResults}">
                    <tr>
                        <td>#${demande.id}</td>
                        <td>${demande.demandeur.nom} ${demande.demandeur.prenom}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty demande.visa}">${demande.visa.reference}</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty demande.visa and not empty demande.visa.dateExpiration}">${demande.visa.dateExpiration}</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty demande.passeport}">${demande.passeport.numero}</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty demande.statut}">${demande.statut.libelle}</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="/demande/transfert?demandeId=${demande.id}" class="btn-select" style="text-decoration: none; display: inline-block;">Sélectionner</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<c:if test="${empty demandeSelectionnee and empty searchResults}">
    <c:if test="${not empty criterion and not empty searchValue}">
        <div class="results-section">
            <div class="no-results">
                Aucune demande avec visa ne correspond à vos critères. Veuillez vérifier et réessayer.
            </div>
        </div>
    </c:if>
</c:if>
