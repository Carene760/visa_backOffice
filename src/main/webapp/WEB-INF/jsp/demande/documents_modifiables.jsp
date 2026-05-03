<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Documents modifiables</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f4f7fb; color: #1f2937; margin: 0; }
        .wrap { max-width: 1200px; margin: 0 auto; padding: 24px; }
        .panel { background: #fff; border: 1px solid #dbe3ee; border-radius: 14px; padding: 20px; box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05); }
        .topbar { display: flex; justify-content: space-between; gap: 16px; align-items: center; margin-bottom: 18px; }
        .search { display: flex; gap: 10px; flex-wrap: wrap; }
        .search input { padding: 10px 12px; border: 1px solid #cfd8e3; border-radius: 8px; min-width: 280px; }
        .search button, .action { border: none; border-radius: 8px; padding: 10px 14px; text-decoration: none; cursor: pointer; }
        .search button { background: #2563eb; color: #fff; }
        .action { background: #0f766e; color: #fff; display: inline-block; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; border-bottom: 1px solid #e5e7eb; text-align: left; }
        th { background: #f8fafc; font-size: 13px; text-transform: uppercase; letter-spacing: .04em; }
        .badge { display: inline-block; padding: 4px 8px; border-radius: 999px; font-size: 12px; }
        .badge.pending { background: #fef3c7; color: #92400e; }
        .badge.ready { background: #dcfce7; color: #166534; }
        .empty { color: #6b7280; font-style: italic; padding: 18px 0; }
    </style>
</head>
<body>
    <div class="wrap">
        <div class="panel">
            <div class="topbar">
                <div>
                    <h1 style="margin:0; font-size: 24px;">Documents modifiables</h1>
                    <div style="color:#6b7280; margin-top:6px;">Demandes dont le statut n'est pas encore SCAN_TERMINE</div>
                </div>
                <form class="search" method="get" action="/demande/documents-modifiables">
                    <input type="text" name="q" value="${q}" placeholder="Rechercher par id, nom, statut">
                    <button type="submit">Rechercher</button>
                </form>
            </div>

            <c:if test="${not empty demandes}">
                <table>
                    <thead>
                        <tr>
                            <th>Dossier</th>
                            <th>Demandeur</th>
                            <th>Statut</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="demande" items="${demandes}">
                            <tr>
                                <td>#${demande.id}</td>
                                <td>
                                    <div><strong>${demande.demandeur.nom} ${demande.demandeur.prenom}</strong></div>
                                    <div style="color:#6b7280; font-size: 13px;">${demande.dateDemande}</div>
                                </td>
                                <td>
                                    <span class="badge ${demande.statutDemande.libelle == 'DOSSIER_CREE' ? 'pending' : 'ready'}">
                                        ${demande.statutDemande.libelle}
                                    </span>
                                </td>
                                <td>
                                    <a class="action" href="/demande/${demande.id}/detail">✎ Modifier</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${empty demandes}">
                <div class="empty">Aucune demande modifiable trouvée.</div>
            </c:if>
        </div>
    </div>
</body>
</html>