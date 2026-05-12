<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .resume-card {
        background: #fff;
        border: 1px solid #d8d8d8;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 16px;
    }
    .resume-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        margin-top: 16px;
    }
    .resume-btn {
        display: inline-block;
        padding: 10px 14px;
        border-radius: 6px;
        text-decoration: none;
        font-weight: 600;
    }
    .btn-primary {
        background: #173f70;
        color: #fff;
    }
    .btn-secondary {
        background: #ecf0f1;
        color: #1f2937;
    }
    .status-pill {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 999px;
        background: #eef5ff;
        color: #173f70;
        font-size: 12px;
        font-weight: 700;
    }
</style>

<div class="resume-card">
    <h2>Reprendre une etape du dossier</h2>
    <p style="margin-top: 8px; color: #4b5563;">
        Dossier #<strong><c:out value="${demandeId}" /></strong>
    </p>
    <p style="margin-top: 8px; color: #4b5563;">
        Statut actuel:
        <span class="status-pill">
            <c:out value="${demande.statutDemande != null ? demande.statutDemande.libelle : 'INCONNU'}" />
        </span>
    </p>

    <c:if test="${completion != null && completion.message != null}">
        <p style="margin-top: 10px; color: #4b5563;">
            <c:out value="${completion.message}" />
        </p>
    </c:if>

    <div class="resume-actions">
        <a class="resume-btn btn-primary" href="/demande/${demandeId}/photo-signature-capture">Reprendre Photo/Signature</a>
        <a class="resume-btn btn-primary" href="/demande/${demandeId}/upload-scanner">Aller a Upload Fichiers</a>
        <a class="resume-btn btn-secondary" href="/demande/${demandeId}/modifier/formulaire">Modifier informations dossier</a>
        <a class="resume-btn btn-secondary" href="/demande/${demandeId}/detail">Retour detail dossier</a>
    </div>
</div>
