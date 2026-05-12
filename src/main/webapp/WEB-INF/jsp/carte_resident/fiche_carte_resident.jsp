<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="container">
    <h2>Fiche Carte Résident</h2>

    <form method="get" action="/demande/carte/fiche">
        <label for="carteId">Numéro de carte (id)</label>
        <input type="text" id="carteId" name="carteId" placeholder="ID de la carte" />
        <button type="submit">Rechercher</button>
    </form>

    <c:if test="${not empty erreur}">
        <div class="alert alert-danger">${erreur}</div>
    </c:if>

    <c:if test="${not empty carte}">
        <h3>Carte #${carte.id} - Réf: ${carte.reference}</h3>
        <div><strong>Date émission:</strong> ${carte.dateEmission}</div>
        <div><strong>Date expiration:</strong> ${carte.dateExpiration}</div>
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
            <a class="btn btn-primary" href="/demande/carte/${carte.id}/generer-fiche">Exporter PDF</a>
            <a class="btn" href="/demande/${carte.demande.id}/detail">Voir la demande</a>
        </div>
    </c:if>
</div>
