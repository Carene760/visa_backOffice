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
        <p>Recherchez une demande existante avec un visa actif. Vous pourrez alors créer un nouveau passeport et lier le visa existant à ce nouveau passeport.</p>
        <c:if test="${not empty message}">
            <div class="preselect-info" style="margin-top: 16px; background: #d4edda; border: 1px solid #c3e6cb;">
                <h4 style="margin-top: 0; margin-bottom: 8px;">Demande Pré-sélectionnée</h4>
                <p>${message}</p>
                <c:if test="${not empty demandePreselect}">
                    <table style="width: 100%; border-collapse: collapse; font-size: 14px; margin-top: 8px;">
                        <tr style="border-bottom: 1px solid #c3e6cb;">
                            <td style="padding: 6px; font-weight: 600; width: 30%; background: #f0f8f5;">Numéro Demande:</td>
                            <td style="padding: 6px;">#${demandePreselect.id}</td>
                        </tr>
                        <tr style="border-bottom: 1px solid #c3e6cb;">
                            <td style="padding: 6px; font-weight: 600; background: #f0f8f5;">Demandeur:</td>
                            <td style="padding: 6px;">${demandePreselect.demandeur.nom} ${demandePreselect.demandeur.prenom}</td>
                        </tr>
                        <tr style="border-bottom: 1px solid #c3e6cb;">
                            <td style="padding: 6px; font-weight: 600; background: #f0f8f5;">Visa Référence:</td>
                            <td style="padding: 6px;">
                                <c:if test="${not empty demandePreselect.visa}">
                                    ${demandePreselect.visa.reference}
                                </c:if>
                                <c:if test="${empty demandePreselect.visa}">
                                    N/A
                                </c:if>
                            </td>
                        </tr>
                        <tr style="border-bottom: 1px solid #c3e6cb;">
                            <td style="padding: 6px; font-weight: 600; background: #f0f8f5;">Ancien Passeport:</td>
                            <td style="padding: 6px;">
                                <c:if test="${not empty demandePreselect.passeport}">
                                    ${demandePreselect.passeport.numero}
                                </c:if>
                                <c:if test="${empty demandePreselect.passeport}">
                                    N/A
                                </c:if>
                            </td>
                        </tr>
                        <tr style="border-bottom: 1px solid #c3e6cb;">
                            <td style="padding: 6px; font-weight: 600; background: #f0f8f5;">Statut:</td>
                            <td style="padding: 6px;">
                                <c:if test="${not empty demandePreselect.statut}">
                                    ${demandePreselect.statut.libelle}
                                </c:if>
                            </td>
                        </tr>
                    </table>
                    <p style="margin-top: 8px; font-size: 13px; color: #155724;">
                        ✓ Cliquez le bouton <strong>Confirmer Transfert</strong> ci-dessous pour créer le nouveau passeport et lier le visa.
                    </p>
                    <form method="post" action="/demande/transfert/select" style="margin-top: 8px;">
                        <input type="hidden" name="demandeId" value="${demandePreselect.id}">
                        <c:if test="${not empty motifsTransfert}">
                            <div style="margin:8px 0;">
                                <label for="motifTransfertId">Motif du Transfert</label>
                                <select id="motifTransfertId" name="motifTransfertId">
                                    <c:forEach var="m" items="${motifsTransfert}">
                                        <option value="${m.id}">${m.libelle}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </c:if>
                        <button type="submit" class="btn-select" style="padding: 10px 24px; font-size: 15px;">✓ Confirmer Transfert</button>
                    </form>
                </c:if>
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

<!-- Section Résultats (affichée si recherche effectuée) -->
<c:if test="${not empty searchResults}">
    <div class="results-section">
        <h3 class="results-title">Résultats de Recherche</h3>
        
        <c:if test="${empty searchResults}">
            <div class="no-results">
                Aucune demande avec visa ne correspond à vos critères. Veuillez vérifier et réessayer.
            </div>
        </c:if>

        <c:if test="${not empty searchResults}">
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
                                <c:if test="${not empty demande.visa}">
                                    ${demande.visa.reference}
                                </c:if>
                                <c:if test="${empty demande.visa}">
                                    N/A
                                </c:if>
                            </td>
                            <td>
                                    <c:if test="${not empty demande.visa and not empty demande.visa.dateExpiration}">
                                    ${demande.visa.dateExpiration}
                                </c:if>
                                    <c:if test="${empty demande.visa or empty demande.visa.dateExpiration}">
                                    N/A
                                </c:if>
                            </td>
                                <td>
                                    <c:if test="${not empty demande.passeport}">
                                        ${demande.passeport.numero}
                                    </c:if>
                                    <c:if test="${empty demande.passeport}">
                                        N/A
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${not empty demande.statut}">
                                        ${demande.statut.libelle}
                                    </c:if>
                                </td>
                            <td>
                                <a href="/demande/transfert?demandeId=${demande.id}" class="btn-select" style="text-decoration: none; display: inline-block;">Sélectionner</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</c:if>
