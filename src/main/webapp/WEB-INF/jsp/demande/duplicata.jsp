<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
        border-bottom: 2px solid var(--accent-blue);
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
        background: var(--accent-blue);
        color: white;
        border: none;
        border-radius: 2px;
        font-weight: 600;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .btn-search:hover {
        background: #2980b9;
    }

    .results-section {
        background: white;
        padding: 28px;
        border: 1px solid #9a9a9a;
        border-radius: 4px;
        margin-top: 20px;
    }

    .duplicata-panel {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
        margin-top: 18px;
    }

    .duplicata-card {
        border: 1px solid #d7d7d7;
        border-radius: 4px;
        padding: 18px;
        background: #fcfcfc;
    }

    .duplicata-card h4 {
        margin-top: 0;
        margin-bottom: 12px;
        color: var(--text-main);
        border-left: 4px solid var(--accent-blue);
        padding-left: 10px;
    }

    .duplicata-form {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

    .duplicata-form .form-group {
        display: flex;
        flex-direction: column;
    }

    .duplicata-form label {
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
        font-size: 14px;
    }

    .duplicata-form input,
    .duplicata-form select,
    .duplicata-form textarea {
        padding: 10px 12px;
        border: 1px solid #8f8f8f;
        border-radius: 2px;
        font-size: 14px;
        font-family: inherit;
        background: #fff;
    }

    .duplicata-form input:focus,
    .duplicata-form select:focus,
    .duplicata-form textarea:focus {
        outline: none;
        border-color: var(--accent);
        background-color: #fcfdff;
    }

    .duplicata-form .form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px;
    }

    .duplicata-form .form-actions {
        display: flex;
        gap: 12px;
        margin-top: 20px;
        padding-top: 16px;
        border-top: 1px solid #e0e0e0;
    }

    .duplicata-form .btn-submit {
        padding: 12px 24px;
        background: var(--accent-blue);
        color: white;
        border: none;
        border-radius: 2px;
        font-weight: 600;
        cursor: pointer;
        font-size: 14px;
        transition: background-color 0.2s;
        flex-shrink: 0;
    }

    .duplicata-form .btn-submit:hover {
        background: #2980b9;
    }

    .duplicata-form .btn-reset {
        padding: 12px 24px;
        background: #95a5a6;
        color: white;
        border: none;
        border-radius: 2px;
        font-weight: 600;
        cursor: pointer;
        font-size: 14px;
        transition: background-color 0.2s;
        text-decoration: none;
        display: inline-flex;
        align-items: center;
        flex-shrink: 0;
    }

    .duplicata-form .btn-reset:hover {
        background: #7f8c8d;
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

    .preselect-info {
        background: #d4edda;
        border: 1px solid #c3e6cb;
        color: #155724;
        padding: 16px;
        border-radius: 4px;
        margin-bottom: 20px;
    }

    .preselect-info h3 {
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

    /* Formulaire Duplicata CarteResident */
    .duplicata-form-section {
        background: white;
        padding: 28px;
        border: 1px solid #9a9a9a;
        border-radius: 4px;
        margin-top: 20px;
    }

    .duplicata-form-title {
        font-size: 18px;
        font-weight: 700;
        color: var(--text-main);
        margin-bottom: 16px;
        border-left: 4px solid var(--accent);
        padding-left: 12px;
    }

    .form-section-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
        margin-bottom: 20px;
    }

    .form-section-grid .form-group {
        display: flex;
        flex-direction: column;
    }

    .form-section-grid label {
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
        font-size: 14px;
    }

    .form-section-grid input,
    .form-section-grid select,
    .form-section-grid textarea {
        padding: 10px 12px;
        border: 1px solid #8f8f8f;
        border-radius: 2px;
        font-size: 14px;
        background: #fff;
        font-family: inherit;
    }

    .form-section-grid textarea {
        resize: vertical;
        min-height: 80px;
    }

    .form-section-grid input:focus,
    .form-section-grid select:focus,
    .form-section-grid textarea:focus {
        outline: none;
        border-color: var(--accent);
        background-color: #fcfdff;
    }

    .form-actions {
        display: flex;
        gap: 12px;
        margin-top: 20px;
    }

    .btn-submit {
        padding: 10px 24px;
        background: var(--accent-blue);
        color: white;
        border: none;
        border-radius: 2px;
        font-weight: 600;
        cursor: pointer;
        transition: background-color 0.2s;
        font-size: 14px;
    }

    .btn-submit:hover {
        background: #2980b9;
    }

    .btn-reset {
        padding: 10px 24px;
        background: #95a5a6;
        color: white;
        border: none;
        border-radius: 2px;
        font-weight: 600;
        cursor: pointer;
        transition: background-color 0.2s;
        font-size: 14px;
    }

    .btn-reset:hover {
        background: #7f8c8d;
    }

    .disabled-form {
        opacity: 0.6;
        pointer-events: none;
    }

    .form-info {
        background: #e3f2fd;
        border: 1px solid #90caf9;
        color: #1565c0;
        padding: 12px;
        border-radius: 4px;
        font-size: 13px;
        margin-bottom: 16px;
    }

    @media (max-width: 1024px) {
        .form-section-grid {
            grid-template-columns: 1fr;
        }

        .duplicata-panel {
            grid-template-columns: 1fr;
        }

        .duplicata-form .form-row {
            grid-template-columns: 1fr;
        }
    }
</style>

<div class="search-container">
    <h2 class="search-title">Rechercher une Demande - Duplicata</h2>
    
    <c:if test="${not empty message}">
        <div class="preselect-info">
            <h3>Demande Pré-sélectionnée</h3>
            <p>${message}</p>
            <p style="margin-bottom: 0; font-size: 13px; color: #155724;">
                Le système peut créer plusieurs duplicata pour une même demande. Chaque duplicata reçoit une référence unique avec suffixe horodaté.
            </p>
        </div>
    </c:if>

    <form method="get" action="/demande/duplicata" class="search-form">
        <div class="form-group">
            <label for="search_criterion">Critère de Recherche *</label>
            <select id="search_criterion" name="criterion" required>
                <option value="">-- Sélectionner un critère --</option>
                <option value="numeroPasseport">Numéro de Passeport</option>
                <option value="numeroDemande">Numéro de Demande</option>
                <option value="nomDemandeur">Nom du Demandeur</option>
            </select>
            <span class="search-hint">Sélectionnez le type de recherche</span>
            <span class="search-hint" style="display:block; color:#0b6b3a; font-weight:600;">Filtre actif: seules les demandes avec visa VALIDE et passeport lié sont affichées.</span>
        </div>

        <div class="form-group">
            <label for="search_value">Valeur de Recherche *</label>
            <input type="text" id="search_value" name="searchValue" placeholder="Entrez la valeur..." required>
            <span class="search-hint">Ex: "6789012345" ou "RAZAFIMANJATO"</span>
        </div>

        <div class="form-group">
            <button type="submit" class="btn-search">Rechercher</button>
        </div>
    </form>
</div>

<!-- Section Résultats / Formulaire -->
<c:if test="${not empty demandePreselect}">
    <c:set var="visaEffectif" value="${not empty visaSource ? visaSource : demandePreselect.visa}" />
    <div class="results-section">
        <h3 class="results-title">Formulaire de Duplicata</h3>
        <p style="margin-top: 0; color: var(--text-soft);">La demande sélectionnée remplace la liste. Le duplicata est borné par la période du visa associé: date d'émission entre l'émission et l'expiration du visa, et date d'expiration identique au visa.</p>

        <div class="duplicata-panel">
            <div class="duplicata-card">
                <h4>Demande et carte source</h4>
                <table style="width: 100%; border-collapse: collapse; font-size: 14px;">
                    <tr style="border-bottom: 1px solid #e5e5e5;">
                        <td style="padding: 8px 0; font-weight: 600; width: 40%;">Numéro demande</td>
                        <td style="padding: 8px 0;">#${demandePreselect.id}</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #e5e5e5;">
                        <td style="padding: 8px 0; font-weight: 600;">Demandeur</td>
                        <td style="padding: 8px 0;">${demandePreselect.demandeur.nom} ${demandePreselect.demandeur.prenom}</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #e5e5e5;">
                        <td style="padding: 8px 0; font-weight: 600;">Passeport</td>
                        <td style="padding: 8px 0;">
                            <c:choose>
                                <c:when test="${not empty demandePreselect.passeport}">${demandePreselect.passeport.numero}</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr style="border-bottom: 1px solid #e5e5e5;">
                        <td style="padding: 8px 0; font-weight: 600;">Visa associé</td>
                        <td style="padding: 8px 0;">
                            <c:choose>
                                <c:when test="${not empty visaEffectif}">
                                    ${visaEffectif.reference}
                                    <c:if test="${not empty visaEffectif.dateExpiration}">
                                        <span style="color: var(--text-soft);">(expire le ${visaEffectif.dateExpiration})</span>
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
                                <c:when test="${not empty demandePreselect.statut}">${demandePreselect.statut.libelle}</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr style="border-bottom: 1px solid #e5e5e5;">
                        <td style="padding: 8px 0; font-weight: 600;">Ancienne Carte Résident</td>
                        <td style="padding: 8px 0;">${referenceCarteAncienne}</td>
                    </tr>
                </table>
            </div>

            <div class="duplicata-card">
                <h4>Créer le duplicata</h4>
                <form method="post" action="/demande/duplicata/creerDuplicataCarteResident" class="duplicata-form">
                    <input type="hidden" name="demandeId" value="${demandePreselect.id}">
                    <c:if test="${not empty carteResident}">
                        <input type="hidden" name="carteResidentSourceId" value="${carteResident.id}">
                    </c:if>

                    <div class="form-group">
                        <label for="cr_reference">Référence du duplicata</label>
                        <input type="text" id="cr_reference" name="referenceDuplicata" value="${referenceDuplicataProposee}" placeholder="Laisser vide pour auto-générer">
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="cr_dateEntree">Date d'Entrée</label>
                            <c:choose>
                                <c:when test="${not empty carteResident}">
                                    <input type="date" id="cr_dateEntree" name="dateEntree" value="${carteResident.dateEntree}" required>
                                </c:when>
                                <c:otherwise>
                                    <input type="date" id="cr_dateEntree" name="dateEntree" required>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="form-group">
                            <label for="cr_dateExpiration">Date d'Expiration</label>
                            <input type="date" id="cr_dateExpiration" name="dateExpiration" value="${visaEffectif.dateExpiration}" readonly required style="background: #f0f0f0;">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="cr_lieuEntree">Lieu d'Entrée</label>
                            <c:choose>
                                <c:when test="${not empty carteResident}">
                                    <input type="text" id="cr_lieuEntree" name="lieuEntree" value="${carteResident.lieuEntree}">
                                </c:when>
                                <c:otherwise>
                                    <input type="text" id="cr_lieuEntree" name="lieuEntree">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="form-group">
                            <label for="cr_dateEmission">Date d'Émission *</label>
                            <c:choose>
                                <c:when test="${not empty dateEmissionCarteAncienne}">
                                    <input type="date" id="cr_dateEmission" name="dateEmission" value="${dateEmissionCarteAncienne}" required
                                           min="${visaDateEmission}"
                                           max="${visaDateExpiration}"
                                           style="color: #333;">
                                </c:when>
                                <c:otherwise>
                                    <input type="date" id="cr_dateEmission" name="dateEmission" value="${visaDateEmission}" required
                                           min="${visaDateEmission}"
                                           max="${visaDateExpiration}"
                                           style="color: #999;">
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-submit">✓ Confirmer Duplicata</button>
                        <a href="/demande/duplicata" class="btn-reset">↻ Réinitialiser</a>
                    </div>
                </form>
            </div>
        </div>

        <c:if test="${not empty duplicatasExistants}">
            <div style="margin-top: 20px; padding: 16px; border: 1px solid #b3d9ff; background: #eef7ff; border-radius: 4px;">
                <h4 style="margin-top: 0; margin-bottom: 12px; color: var(--text-main);">Duplicata(s) déjà créés pour cette demande</h4>
                <table style="width: 100%; border-collapse: collapse; font-size: 14px; background: white;">
                    <thead style="background: var(--accent-dark); color: white;">
                        <tr>
                            <th style="padding: 10px; border: 1px solid #ddd; text-align: left;">Référence</th>
                            <th style="padding: 10px; border: 1px solid #ddd; text-align: left;">Visa</th>
                            <th style="padding: 10px; border: 1px solid #ddd; text-align: left;">Date émission</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="duplicata" items="${duplicatasExistants}">
                            <tr>
                                <td style="padding: 10px; border: 1px solid #e0e0e0;">${duplicata.reference}</td>
                                <td style="padding: 10px; border: 1px solid #e0e0e0;">
                                    <c:choose>
                                        <c:when test="${not empty duplicata.visa}">${duplicata.visa.reference}</c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="padding: 10px; border: 1px solid #e0e0e0;">
                                    <c:choose>
                                        <c:when test="${not empty duplicata.dateEmission}">${duplicata.dateEmission}</c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</c:if>

<c:if test="${empty demandePreselect and not empty searchResults}">
    <div class="results-section">
        <h3 class="results-title">Résultats de Recherche</h3>
        <table class="results-table">
            <thead>
                <tr>
                    <th>Numéro Demande</th>
                    <th>Demandeur</th>
                    <th>Passeport</th>
                    <th>Date Expiration</th>
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
                                <c:when test="${not empty demande.passeport}">${demande.passeport.numero}</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty demande.passeport and not empty demande.passeport.dateExpiration}">${demande.passeport.dateExpiration}</c:when>
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
                            <a href="/demande/duplicata?demandeId=${demande.id}" class="btn-select" style="text-decoration: none; display: inline-block;">Sélectionner</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<c:if test="${empty demandePreselect and empty searchResults}">
    <c:if test="${not empty criterion and not empty searchValue}">
        <div class="results-section">
            <div class="no-results">
                Aucune demande ne correspond à vos critères. Veuillez vérifier et réessayer.
            </div>
        </div>
    </c:if>
</c:if>

