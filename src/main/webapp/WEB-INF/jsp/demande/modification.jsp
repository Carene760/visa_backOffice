<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modification de Demande</title>
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
            max-width: 1000px;
            margin: 0 auto;
            padding: 20px;
        }
        header {
            background-color: #2c3e50;
            color: white;
            padding: 20px;
            margin-bottom: 30px;
            border-radius: 5px;
        }
        h1 {
            margin: 0;
            font-size: 24px;
        }
        .alert {
            padding: 15px;
            margin: 20px 0;
            border-radius: 5px;
            border-left: 4px solid;
        }
        .alert-danger {
            background-color: #f8d7da;
            border-left-color: #f5c6cb;
            color: #721c24;
        }
        .alert-info {
            background-color: #d1ecf1;
            border-left-color: #bee5eb;
            color: #0c5460;
        }
        .alert-warning {
            background-color: #fff3cd;
            border-left-color: #ffeeba;
            color: #856404;
        }
        .alert-success {
            background-color: #d4edda;
            border-left-color: #c3e6cb;
            color: #155724;
        }
        .completion-status {
            padding: 15px;
            margin: 20px 0;
            border-radius: 5px;
            font-weight: bold;
        }
        .completion-status.complet {
            background-color: #d4edda;
            color: #155724;
            border-left: 4px solid #28a745;
        }
        .completion-status.incomplet {
            background-color: #f8d7da;
            color: #721c24;
            border-left: 4px solid #dc3545;
        }
        .completion-details {
            margin-top: 10px;
            font-size: 14px;
        }
        .completion-item {
            padding: 5px 0;
            display: flex;
            align-items: center;
        }
        .completion-item.obligatoire {
            color: #dc3545;
        }
        .completion-item.optionnelle {
            color: #ffc107;
        }
        .icon {
            margin-right: 8px;
        }
        .form-section {
            background-color: white;
            padding: 20px;
            margin: 20px 0;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        .form-section h2 {
            color: #2c3e50;
            margin-bottom: 15px;
            font-size: 18px;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
        }
        .form-group {
            margin-bottom: 15px;
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }
        .form-group.full {
            grid-column: 1 / -1;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #2c3e50;
        }
        label .required {
            color: #e74c3c;
        }
        input[type="text"],
        input[type="email"],
        input[type="tel"],
        input[type="date"],
        select {
            width: 100%;
            padding: 10px;
            border: 1px solid #bdc3c7;
            border-radius: 4px;
            font-size: 14px;
        }
        input[type="text"]:focus,
        input[type="email"]:focus,
        input[type="tel"]:focus,
        input[type="date"]:focus,
        select:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 5px rgba(52, 152, 219, 0.3);
        }
        .checkbox-group {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 10px;
        }
        .checkbox-item {
            display: flex;
            align-items: center;
        }
        input[type="checkbox"] {
            margin-right: 8px;
            width: 16px;
            height: 16px;
        }
        .actions {
            display: flex;
            gap: 10px;
            margin-top: 30px;
            justify-content: center;
        }
        button {
            padding: 12px 30px;
            font-size: 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
            transition: background-color 0.3s;
        }
        .btn-primary {
            background-color: #3498db;
            color: white;
        }
        .btn-primary:hover {
            background-color: #2980b9;
        }
        .btn-secondary {
            background-color: #95a5a6;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #7f8c8d;
        }
        .btn-danger {
            background-color: #e74c3c;
            color: white;
        }
        .btn-danger:hover {
            background-color: #c0392b;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Modification de Demande #${demandeId}</h1>
            <p>Vous pouvez modifier les informations tant que le dossier est en statut DOSSIER_CREE</p>
        </header>

        <!-- Affichage des erreurs -->
        <c:if test="${not empty erreur}">
            <div class="alert alert-danger">
                <strong>Erreur:</strong> ${erreur}
                <c:if test="${not empty erreurs}">
                    <ul style="margin-top: 10px;">
                        <c:forEach var="err" items="${erreurs}">
                            <li>${err}</li>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>
        </c:if>

        <!-- Affichage de la complétude du dossier -->
        <c:if test="${not empty completion}">
            <div class="completion-status ${completion.success ? 'complet' : 'incomplet'}">
                <div>
                    ${completion.success ? '✓ Dossier complet' : '✗ Dossier incomplet'}
                </div>
                <div class="completion-details">
                    ${completion.message}
                </div>
                <c:if test="${not empty completion.errors}">
                    <div class="completion-details" style="margin-top: 10px;">
                        <c:forEach var="err" items="${completion.errors}">
                            <div class="completion-item ${fn:contains(err, 'OBLIGATOIRE') ? 'obligatoire' : 'optionnelle'}">
                                ${fn:contains(err, 'OBLIGATOIRE') ? '⚠ Obligatoire:' : '⚠ Optionnelle:'} 
                                ${fn:substringAfter(err, ': ')}
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </c:if>

        <form method="POST" action="/demande/${demandeId}/modifier">
            <!-- Section 1: Informations du Demandeur -->
            <div class="form-section">
                <h2>1. Informations du Demandeur</h2>
                <div class="form-group">
                    <div>
                        <label for="demandeur_nom">Nom <span class="required">*</span></label>
                        <input type="text" id="demandeur_nom" name="demandeur_nom" required value="${demandeDTO.nom}">
                    </div>
                    <div>
                        <label for="demandeur_prenom">Prénom</label>
                        <input type="text" id="demandeur_prenom" name="demandeur_prenom" value="${demandeDTO.prenom}">
                    </div>
                </div>
                <div class="form-group">
                    <div>
                        <label for="demandeur_nom_naissance">Nom de naissance</label>
                        <input type="text" id="demandeur_nom_naissance" name="demandeur_nom_naissance" value="${demandeDTO.nomNaissance}">
                    </div>
                    <div>
                        <label for="demandeur_date_naissance">Date de naissance <span class="required">*</span></label>
                        <input type="date" id="demandeur_date_naissance" name="demandeur_date_naissance" required value="${demandeDTO.dateNaissance}">
                    </div>
                </div>
                <div class="form-group">
                    <div>
                        <label for="demandeur_lieu_naissance">Lieu de naissance</label>
                        <input type="text" id="demandeur_lieu_naissance" name="demandeur_lieu_naissance" value="${demandeDTO.lieuNaissance}">
                    </div>
                    <div>
                        <label for="demandeur_nationalite">Nationalité <span class="required">*</span></label>
                        <select id="demandeur_nationalite" name="demandeur_nationalite" required>
                            <option value="">-- Sélectionner une nationalité --</option>
                            <c:forEach var="nat" items="${nationalites}">
                                <option value="${nat.id}" ${nat.id == demandeDTO.idNationalite ? 'selected' : ''}>${nat.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <div>
                        <label for="demandeur_situation">Situation matrimoniale</label>
                        <select id="demandeur_situation" name="demandeur_situation">
                            <option value="">-- Sélectionner une situation --</option>
                            <c:forEach var="sit" items="${situations}">
                                <option value="${sit.id}" ${sit.id == demandeDTO.idSituationMatrimoniale ? 'selected' : ''}>${sit.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="demandeur_email">Email</label>
                        <input type="email" id="demandeur_email" name="demandeur_email" value="${demandeDTO.email}">
                    </div>
                </div>
                <div class="form-group">
                    <div>
                        <label for="demandeur_telephone">Téléphone <span class="required">*</span></label>
                        <input type="tel" id="demandeur_telephone" name="demandeur_telephone" required value="${demandeDTO.telephone}">
                    </div>
                    <div>
                        <label for="demandeur_adresse">Adresse à Madagascar <span class="required">*</span></label>
                        <input type="text" id="demandeur_adresse" name="demandeur_adresse" required value="${demandeDTO.adresseMadagascar}">
                    </div>
                </div>
            </div>

            <!-- Section 2: Informations du Passeport -->
            <div class="form-section">
                <h2>2. Informations du Passeport</h2>
                <div class="form-group">
                    <div>
                        <label for="passeport_numero">Numéro du passeport <span class="required">*</span></label>
                        <input type="text" id="passeport_numero" name="passeport_numero" required value="${demandeDTO.numeroPasseport}">
                    </div>
                    <div>
                        <label for="passeport_date_delivrance">Date de délivrance <span class="required">*</span></label>
                        <input type="date" id="passeport_date_delivrance" name="passeport_date_delivrance" required value="${demandeDTO.dateDelivrancePasseport}">
                    </div>
                </div>
                <div class="form-group">
                    <div>
                        <label for="passeport_date_expiration">Date d'expiration <span class="required">*</span></label>
                        <input type="date" id="passeport_date_expiration" name="passeport_date_expiration" required value="${demandeDTO.dateExpirationPasseport}">
                    </div>
                </div>
            </div>

            <!-- Section 3: Informations du Visa -->
            <div class="form-section">
                <h2>3. Informations du Visa</h2>
                <div class="form-group">
                    <div>
                        <label for="visa_reference">Référence du visa <span class="required">*</span></label>
                        <input type="text" id="visa_reference" name="visa_reference" required value="${demandeDTO.referenceVisa}">
                    </div>
                    <div>
                        <label for="visa_date_entree">Date d'entrée <span class="required">*</span></label>
                        <input type="date" id="visa_date_entree" name="visa_date_entree" required value="${demandeDTO.dateEntreeVisa}">
                    </div>
                </div>
                <div class="form-group">
                    <div>
                        <label for="visa_lieu_entree">Lieu d'entrée</label>
                        <input type="text" id="visa_lieu_entree" name="visa_lieu_entree" value="${demandeDTO.lieuEntreeVisa}">
                    </div>
                    <div>
                        <label for="visa_date_expiration">Date d'expiration <span class="required">*</span></label>
                        <input type="date" id="visa_date_expiration" name="visa_date_expiration" required value="${demandeDTO.dateExpirationVisa}">
                    </div>
                </div>
            </div>

            <!-- Section 4: Type de Demande -->
            <div class="form-section">
                <h2>4. Type et Motif de Demande</h2>
                <div class="form-group">
                    <div>
                        <label for="type_demande">Type de demande <span class="required">*</span></label>
                        <select id="type_demande" name="type_demande" required>
                            <option value="">-- Sélectionner un type --</option>
                            <c:forEach var="type" items="${typesDemande}">
                                <option value="${type.id}" ${type.id == demandeDTO.idTypeDemande ? 'selected' : ''}>${type.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="type_motif">Motif de demande <span class="required">*</span></label>
                        <select id="type_motif" name="type_motif" required>
                            <option value="">-- Sélectionner un motif --</option>
                            <c:forEach var="motif" items="${typeMotifs}">
                                <option value="${motif.id}" ${motif.id == demandeDTO.idTypeMotif ? 'selected' : ''}>${motif.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Section 5: Documents Justificatifs -->
            <div class="form-section">
                <h2>5. Documents Justificatifs</h2>
                <div class="form-group full">
                    <div>
                        <label>Documents communs</label>
                        <div class="checkbox-group">
                            <c:forEach var="doc" items="${documentsCommuns}">
                                <div class="checkbox-item">
                                    <input type="checkbox" id="doc_${doc.id}" name="documents" value="${doc.id}" <c:if test="${not empty demandeDTO.piecesPresentes and demandeDTO.piecesPresentes.contains(doc.id)}">checked</c:if>>
                                    <label for="doc_${doc.id}" style="margin-bottom: 0; font-weight: normal;">
                                        ${doc.libelle} ${doc.obligatoire ? '(obligatoire)' : '(optionnel)'}
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
                <div class="form-group full">
                    <div>
                        <label>Documents spécifiques</label>
                        <div class="checkbox-group">
                            <c:forEach var="doc" items="${documentsSpecifiques}">
                                <div class="checkbox-item">
                                    <input type="checkbox" id="doc_${doc.id}" name="documents" value="${doc.id}" <c:if test="${not empty demandeDTO.piecesPresentes and demandeDTO.piecesPresentes.contains(doc.id)}">checked</c:if>>
                                    <label for="doc_${doc.id}" style="margin-bottom: 0; font-weight: normal;">
                                        ${doc.libelle} ${doc.obligatoire ? '(obligatoire)' : '(optionnel)'}
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Boutons d'action -->
            <div class="actions">
                <button type="submit" class="btn-primary">Valider Modification</button>
                <button type="button" class="btn-secondary" onclick="window.history.back()">Annuler</button>
            </div>
        </form>
    </div>
</body>
</html>
