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
    }
</style>

<div class="search-container">
    <h2 class="search-title">Rechercher une Demande - Duplicata</h2>
    
    <c:if test="${not empty message}">
        <div class="preselect-info">
            <h3>Demande Pré-sélectionnée</h3>
            <p>${message}</p>
            <c:if test="${not empty demandePreselect}">
                <hr style="margin: 12px 0; border: none; border-top: 1px solid #b3d9c8;">
                <table class="preselect-table" style="width: 100%; border-collapse: collapse; font-size: 14px;">
                    <tr style="border-bottom: 1px solid #d4edda;">
                        <td style="padding: 8px; font-weight: 600; width: 30%; background: #f0f8f5;">Numéro Demande:</td>
                        <td style="padding: 8px;">#${demandePreselect.id}</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #d4edda;">
                        <td style="padding: 8px; font-weight: 600; background: #f0f8f5;">Demandeur:</td>
                        <td style="padding: 8px;">${demandePreselect.demandeur.nom} ${demandePreselect.demandeur.prenom}</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #d4edda;">
                        <td style="padding: 8px; font-weight: 600; background: #f0f8f5;">Passeport:</td>
                        <td style="padding: 8px;">${demandePreselect.passeport.numero}</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #d4edda;">
                        <td style="padding: 8px; font-weight: 600; background: #f0f8f5;">Statut:</td>
                        <td style="padding: 8px;">${demandePreselect.statut.libelle}</td>
                    </tr>
                </table>
                <p style="margin-top: 12px; font-size: 13px; color: #155724;">
                    ✓ Cliquez le bouton <strong>Confirmer Duplicata</strong> ci-dessous pour finaliser.
                </p>
                <form method="post" action="/demande/duplicata/select" style="margin-top: 12px;">
                    <input type="hidden" name="demandeId" value="${demandePreselect.id}">
                    <button type="submit" class="btn-select" style="padding: 10px 24px; font-size: 15px;">✓ Confirmer Duplicata</button>
                </form>
            </c:if>
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

<!-- Section Résultats (affichée si recherche effectuée) -->
<c:if test="${not empty searchResults}">
    <div class="results-section">
        <h3 class="results-title">Résultats de Recherche</h3>
        
        <c:if test="${empty searchResults}">
            <div class="no-results">
                Aucune demande ne correspond à vos critères. Veuillez vérifier et réessayer.
            </div>
        </c:if>

        <c:if test="${not empty searchResults}">
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
                                    <c:if test="${not empty demande.passeport}">
                                        ${demande.passeport.numero}
                                    </c:if>
                                    <c:if test="${empty demande.passeport}">
                                        N/A
                                    </c:if>
                                </td>
                            <td>
                                    <c:if test="${not empty demande.passeport and not empty demande.passeport.dateExpiration}">
                                    ${demande.passeport.dateExpiration}
                                    </c:if>
                                    <c:if test="${empty demande.passeport or empty demande.passeport.dateExpiration}">
                                    N/A
                                </c:if>
                            </td>
                                <td>
                                    <c:if test="${not empty demande.statut}">
                                        ${demande.statut.libelle}
                                    </c:if>
                                </td>
                            <td>
                                <a href="/demande/duplicata?demandeId=${demande.id}" class="btn-select" style="text-decoration: none; display: inline-block;">Sélectionner</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</c:if>

<!-- Formulaire Création Duplicata CarteResident -->
<c:if test="${not empty carteResident}">
    <div class="duplicata-form-section">
        <h3 class="duplicata-form-title">Créer un Duplicata - Carte Résident</h3>
        
        <div class="form-info">
            ℹ️ Les données ci-dessous proviennent de la carte résident existante. Modifiez si nécessaire avant de créer le duplicata (en cas de perte).
        </div>

        <form method="post" action="/demande/duplicata/creerDuplicataCarteResident">
            <input type="hidden" name="demandeId" value="${carteResident.demande.id}">
            <input type="hidden" name="carteResidentSourceId" value="${carteResident.id}">

            <div class="form-section-grid">
                <div class="form-group">
                    <label for="cr_reference">Référence Carte Résident (Original)</label>
                    <input type="text" id="cr_reference" name="referenceSource" value="${carteResident.reference}" readonly style="background: #f0f0f0;">
                </div>

                <div class="form-group">
                    <label for="cr_dateEntree">Date d'Entrée</label>
                    <input type="date" id="cr_dateEntree" name="dateEntree" 
                           value="<fmt:formatDate value='${carteResident.dateEntree}' pattern='yyyy-MM-dd'/>" required>
                </div>

                <div class="form-group">
                    <label for="cr_lieuEntree">Lieu d'Entrée</label>
                    <input type="text" id="cr_lieuEntree" name="lieuEntree" value="${carteResident.lieuEntree}">
                </div>

                <div class="form-group">
                    <label for="cr_dateExpiration">Date d'Expiration</label>
                    <input type="date" id="cr_dateExpiration" name="dateExpiration" 
                           value="<fmt:formatDate value='${carteResident.dateExpiration}' pattern='yyyy-MM-dd'/>" required>
                </div>

                <div class="form-group">
                    <label for="cr_dateEmission">Date d'Émission</label>
                    <input type="date" id="cr_dateEmission" name="dateEmission" 
                           value="<fmt:formatDate value='${carteResident.dateEmission}' pattern='yyyy-MM-dd'/>" required>
                </div>

                <div class="form-group">
                    <label for="cr_motif">Motif du Duplicata *</label>
                    <select id="cr_motif" name="motif" required>
                        <option value="">-- Sélectionner un motif --</option>
                        <option value="perte">Carte perdue</option>
                        <option value="vol">Carte volée</option>
                        <option value="usure">Usure</option>
                        <option value="deterioration">Détérioration</option>
                        <option value="autre">Autre</option>
                    </select>
                </div>

                <div class="form-group" style="grid-column: 1 / -1;">
                    <label for="cr_remarques">Remarques / Observations</label>
                    <textarea id="cr_remarques" name="remarques" placeholder="Détails supplémentaires (optionnel)"></textarea>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-submit">✓ Créer le Duplicata</button>
                <button type="reset" class="btn-reset">↻ Réinitialiser</button>
            </div>
        </form>
    </div>
</c:if>
