<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="container">
    <h2>Fiche demande</h2>

    <form method="get" action="/demande/fiche">
        <label for="demandeId">Rechercher par numéro de dossier</label>
        <input type="text" id="demandeId" name="demandeId" placeholder="Numéro de dossier" />
        <button type="submit">Rechercher</button>
    </form>

    <c:if test="${not empty erreur}">
        <div class="alert alert-danger">${erreur}</div>
    </c:if>

    <c:if test="${not empty demande}">
        <h3>Demande #${demande.id} - ${demande.statutDemande.libelle}</h3>
        <div>
            <strong>Demandeur:</strong> ${demande.demandeur.nom} ${demande.demandeur.prenom}
        </div>
        <div>
            <strong>Date de naissance:</strong> <c:out value="${demande.demandeur.dateNaissance}" />
        </div>
        <div style="margin-top:10px;">
            <c:if test="${not empty photoBase64}">
                <div style="display:inline-block;margin-right:12px;">
                    <div><strong>Photo webcam</strong></div>
                    <img alt="photo" src="data:image/png;base64,${photoBase64}" style="max-width:200px;max-height:200px;border:1px solid #ccc;" />
                </div>
            </c:if>
            <c:if test="${not empty signatureBase64}">
                <div style="display:inline-block;">
                    <div><strong>Signature souris</strong></div>
                    <img alt="signature" src="data:image/png;base64,${signatureBase64}" style="max-width:300px;max-height:120px;border:1px solid #ccc;" />
                </div>
            </c:if>
        </div>

        <div style="margin-top:14px;">
            <a class="btn btn-primary" href="/demande/${demande.id}/generer-fiche">Exporter PDF</a>
            <a class="btn" href="/demande/${demande.id}/detail">Voir le détail</a>
        </div>

        <h4 style="margin-top:18px;">Documents scannés</h4>
        <c:if test="${not empty scans}">
            <ul>
                <c:forEach var="s" items="${scans}">
                    <li>${s.nomFichier} - <c:out value="${s.pieceAFournir != null ? s.pieceAFournir.typeDocument.libelle : '-'}"/></li>
                </c:forEach>
            </ul>
        </c:if>
        <c:if test="${empty scans}">
            <div>Aucun document scanné.</div>
        </c:if>

    </c:if>
</div>