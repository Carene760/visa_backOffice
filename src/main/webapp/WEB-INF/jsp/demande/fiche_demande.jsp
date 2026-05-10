<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .search-card {
        background: #fff;
        border: 1px solid #d8d8d8;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 16px;
    }
    .search-grid {
        display: grid;
        grid-template-columns: 180px 1fr auto;
        gap: 10px;
    }
    .search-grid input, .search-grid select {
        padding: 9px 10px;
        border: 1px solid #cfd4dc;
        border-radius: 6px;
    }
    .search-grid button {
        padding: 10px 14px;
        border: 0;
        border-radius: 6px;
        background: #173f70;
        color: #fff;
        font-weight: 600;
    }
    table {
        width: 100%;
        border-collapse: collapse;
        background: #fff;
    }
    th, td {
        border: 1px solid #e5e7eb;
        padding: 10px;
        text-align: left;
        font-size: 14px;
    }
    th {
        background: #f8fafc;
    }
    .fiche-card {
        background: #fff;
        border: 1px solid #d8d8d8;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 16px;
    }
    .fiche-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
        gap: 16px;
        margin-top: 14px;
    }
    .media-box {
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        padding: 10px;
        background: #fafafa;
    }
    .media-box img {
        width: 100%;
        max-width: 280px;
        height: auto;
        display: block;
        border-radius: 6px;
        border: 1px solid #d9d9d9;
    }
    .pdf-btn {
        display: inline-block;
        margin-top: 12px;
        padding: 10px 14px;
        border-radius: 6px;
        text-decoration: none;
        background: #173f70;
        color: #fff;
        font-weight: 600;
    }
</style>

<div class="search-card">
    <h2>Recherche Fiche Demande</h2>
    <form method="get" action="/demande/fiche-demande" class="search-grid" style="margin-top: 12px;">
        <select name="criterion">
            <option value="numeroDemande" ${criterion == 'numeroDemande' ? 'selected' : ''}>Numero Demande</option>
            <option value="nomDemandeur" ${criterion == 'nomDemandeur' ? 'selected' : ''}>Nom Demandeur</option>
            <option value="numeroPasseport" ${criterion == 'numeroPasseport' ? 'selected' : ''}>Numero Passeport</option>
            <option value="numeroVisa" ${criterion == 'numeroVisa' ? 'selected' : ''}>Numero Visa</option>
        </select>
        <input type="text" name="searchValue" value="${searchValue}" placeholder="Entrez la valeur de recherche" required>
        <button type="submit">Rechercher</button>
    </form>
</div>

<c:if test="${ficheDemande != null}">
    <div class="fiche-card">
        <h2>Fiche Demande #<c:out value="${ficheDemande.id}" /></h2>
        <p style="margin-top: 8px; color: #4b5563;">
            <strong>Statut:</strong>
            <c:out value="${statutDemande}" />
        </p>

        <div class="fiche-grid">
            <div class="media-box">
                <h4>Photo Webcam</h4>
                <c:choose>
                    <c:when test="${not empty photoWebcamBase64}">
                        <img src="${photoWebcamBase64}" alt="Photo webcam" />
                    </c:when>
                    <c:otherwise>
                        <p style="color: #6b7280;">Photo non disponible.</p>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="media-box">
                <h4>Signature Souris</h4>
                <c:choose>
                    <c:when test="${not empty signatureSourisBase64}">
                        <img src="${signatureSourisBase64}" alt="Signature souris" />
                    </c:when>
                    <c:otherwise>
                        <p style="color: #6b7280;">Signature non disponible.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <a class="pdf-btn" href="/demande/${ficheDemande.id}/generer-recepisse">Exporter PDF</a>
    </div>
</c:if>

<c:if test="${searchResults != null}">
    <table>
        <thead>
            <tr>
                <th>ID Demande</th>
                <th>Demandeur</th>
                <th>Passeport</th>
                <th>Visa</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="r" items="${searchResults}">
                <tr>
                    <td><c:out value="${r.demande.id}" /></td>
                    <td>
                        <c:out value="${r.demandeur.nom}" />
                        <c:if test="${r.demandeur.prenom != null}">
                            <c:out value=" ${r.demandeur.prenom}" />
                        </c:if>
                    </td>
                    <td><c:out value="${r.passeport != null ? r.passeport.numero : '-'}" /></td>
                    <td><c:out value="${r.visa != null ? r.visa.reference : '-'}" /></td>
                    <td>
                        <a href="/demande/fiche-demande?criterion=${criterion}&searchValue=${searchValue}&demandeId=${r.demande.id}">Voir fiche</a>
                        &nbsp;|&nbsp;
                        <a href="/demande/${r.demande.id}/detail">Voir detail</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty searchResults}">
                <tr>
                    <td colspan="5">Aucun resultat.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</c:if>
