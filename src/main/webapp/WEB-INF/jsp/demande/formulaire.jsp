<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demande de Transformation de Visa</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        h1 {
            color: #333;
            margin-bottom: 30px;
            text-align: center;
            border-bottom: 3px solid #667eea;
            padding-bottom: 15px;
        }
        .form-section {
            margin-bottom: 30px;
            padding: 20px;
            background: #f9f9f9;
            border-left: 4px solid #667eea;
            border-radius: 5px;
        }
        .form-section h2 {
            color: #667eea;
            font-size: 18px;
            margin-bottom: 20px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        .form-rows {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        .form-rows.full {
            grid-template-columns: 1fr;
        }
        .form-group {
            display: flex;
            flex-direction: column;
        }
        label {
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
            font-size: 14px;
        }
        .required::after {
            content: " *";
            color: red;
        }
        input[type="text"],
        input[type="email"],
        input[type="tel"],
        input[type="date"],
        select,
        textarea {
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        input[type="text"]:focus,
        input[type="email"]:focus,
        input[type="tel"]:focus,
        input[type="date"]:focus,
        select:focus,
        textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
        }
        textarea {
            resize: vertical;
            min-height: 80px;
        }
        .document-section {
            margin-top: 20px;
            padding: 15px;
            background: white;
            border-radius: 5px;
        }
        .document-section h3 {
            color: #333;
            font-size: 16px;
            margin-bottom: 15px;
        }
        .document-list {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }
        .document-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px;
            background: #f0f0f0;
            border-radius: 5px;
        }
        .document-item input[type="checkbox"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
        }
        .document-item label {
            margin: 0;
            font-weight: 400;
            cursor: pointer;
            font-size: 13px;
        }
        .mandatory-badge {
            display: inline-block;
            background: #ff6b6b;
            color: white;
            padding: 2px 6px;
            border-radius: 3px;
            font-size: 11px;
            font-weight: bold;
            margin-left: 5px;
        }
        .optional-badge {
            display: inline-block;
            background: #95a5a6;
            color: white;
            padding: 2px 6px;
            border-radius: 3px;
            font-size: 11px;
            font-weight: bold;
            margin-left: 5px;
        }
        .motif-selector {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-top: 10px;
        }
        .motif-option {
            padding: 15px;
            border: 2px solid #ddd;
            border-radius: 5px;
            cursor: pointer;
            transition: all 0.3s;
            text-align: center;
        }
        .motif-option:hover {
            border-color: #667eea;
            background: #f9f9f9;
        }
        .motif-option input[type="radio"] {
            margin-right: 10px;
        }
        .motif-option input[type="radio"]:checked + label {
            font-weight: bold;
            color: #667eea;
        }
        .form-actions {
            display: flex;
            gap: 15px;
            margin-top: 30px;
            justify-content: center;
        }
        button {
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        .btn-submit {
            background: #667eea;
            color: white;
        }
        .btn-submit:hover {
            background: #5568d3;
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .btn-reset {
            background: #ecf0f1;
            color: #333;
        }
        .btn-reset:hover {
            background: #bdc3c7;
        }
        .error-message {
            background: #fff3cd;
            color: #856404;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #ffc107;
        }
        .success-message {
            background: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #28a745;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Demande de Transformation de Visa en Long Séjour</h1>

        <c:if test="${not empty erreur}">
            <div class="error-message">
                <strong>Erreur:</strong> ${erreur}
            </div>
        </c:if>

        <form method="POST" action="/demande/creer">
            <!-- Section 1: Informations Personnelles du Demandeur -->
            <div class="form-section">
                <h2>Informations Personnelles</h2>
                <div class="form-rows">
                    <div class="form-group">
                        <label for="demandeur_nom" class="required">Nom</label>
                        <input type="text" id="demandeur_nom" name="demandeur.nom" required>
                    </div>
                    <div class="form-group">
                        <label for="demandeur_prenom" class="required">Prénom</label>
                        <input type="text" id="demandeur_prenom" name="demandeur.prenom" required>
                    </div>
                    <div class="form-group">
                        <label for="demandeur_nom_naissance">Nom à la Naissance</label>
                        <input type="text" id="demandeur_nom_naissance" name="demandeur.nomNaissance">
                    </div>
                    <div class="form-group">
                        <label for="demandeur_date_naissance" class="required">Date de Naissance</label>
                        <input type="date" id="demandeur_date_naissance" name="demandeur.dateNaissance" required>
                    </div>
                    <div class="form-group">
                        <label for="demandeur_nationalite" class="required">Nationalité</label>
                        <select id="demandeur_nationalite" name="demandeur.nationalite.id" required>
                            <option value="">-- Sélectionner --</option>
                            <c:forEach var="nat" items="${nationalites}">
                                <option value="${nat.id}">${nat.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="demandeur_situation" class="required">Situation Matrimoniale</label>
                        <select id="demandeur_situation" name="demandeur.situationMatrimoniale.id" required>
                            <option value="">-- Sélectionner --</option>
                            <c:forEach var="sit" items="${situations}">
                                <option value="${sit.id}">${sit.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-rows full">
                    <div class="form-group">
                        <label for="demandeur_adresse" class="required">Adresse à Madagascar</label>
                        <textarea id="demandeur_adresse" name="demandeur.adresseMadagascar" required></textarea>
                    </div>
                </div>
                <div class="form-rows">
                    <div class="form-group">
                        <label for="demandeur_email" class="required">Email</label>
                        <input type="email" id="demandeur_email" name="demandeur.email" required>
                    </div>
                    <div class="form-group">
                        <label for="demandeur_telephone" class="required">Téléphone</label>
                        <input type="tel" id="demandeur_telephone" name="demandeur.telephone" required>
                    </div>
                </div>
            </div>

            <!-- Section 2: Informations Passeport -->
            <div class="form-section">
                <h2>Informations Passeport</h2>
                <div class="form-rows">
                    <div class="form-group">
                        <label for="passeport_numero" class="required">Numéro de Passeport</label>
                        <input type="text" id="passeport_numero" name="passeport.numero" required>
                    </div>
                    <div class="form-group">
                        <label for="passeport_date_delivrance" class="required">Date de Délivrance</label>
                        <input type="date" id="passeport_date_delivrance" name="passeport.dateDelivrance" required>
                    </div>
                    <div class="form-group">
                        <label for="passeport_date_expiration" class="required">Date d'Expiration</label>
                        <input type="date" id="passeport_date_expiration" name="passeport.dateExpiration" required>
                    </div>
                </div>
            </div>

            <!-- Section 3: Informations Visa Transformable -->
            <div class="form-section">
                <h2>Informations Visa Transformable</h2>
                <div class="form-rows">
                    <div class="form-group">
                        <label for="visa_reference" class="required">Référence du Visa</label>
                        <input type="text" id="visa_reference" name="visaTransformable.reference" required>
                    </div>
                    <div class="form-group">
                        <label for="visa_date_entree" class="required">Date d'Entrée</label>
                        <input type="date" id="visa_date_entree" name="visaTransformable.dateEntree" required>
                    </div>
                    <div class="form-group">
                        <label for="visa_lieu_entree">Lieu d'Entrée</label>
                        <input type="text" id="visa_lieu_entree" name="visaTransformable.lieuEntree">
                    </div>
                    <div class="form-group">
                        <label for="visa_date_expiration" class="required">Date d'Expiration</label>
                        <input type="date" id="visa_date_expiration" name="visaTransformable.dateExpiration" required>
                    </div>
                </div>
            </div>

            <!-- Section 4: Type de Motif -->
            <div class="form-section">
                <h2>Type de Demande</h2>
                <div class="form-group">
                    <label for="type_motif" class="required">Motif de Transformation</label>
                    <select id="type_motif" name="typeMotif.id" required onchange="updateDocuments()">
                        <option value="">-- Sélectionner --</option>
                        <c:forEach var="motif" items="${typeMotifs}">
                            <option value="${motif.id}">${motif.libelle}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="type_demande" class="required">Type de Demande</label>
                    <select id="type_demande" name="typeDemande.id" required>
                        <option value="">-- Sélectionner --</option>
                        <c:forEach var="type" items="${typesDemande}">
                            <option value="${type.id}">${type.libelle}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <!-- Section 5: Documents Communs -->
            <div class="form-section">
                <h2>Pièces Communes</h2>
                <div class="document-section">
                    <h3>Documents à fournir</h3>
                    <div class="document-list">
                        <c:forEach var="doc" items="${documentsCommuns}">
                            <div class="document-item">
                                <input type="checkbox" id="doc_${doc.id}" name="documents" value="${doc.id}">
                                <label for="doc_${doc.id}">
                                    ${doc.libelle}
                                    <c:if test="${doc.obligatoire}">
                                        <span class="mandatory-badge">OBLIGATOIRE</span>
                                    </c:if>
                                    <c:if test="${!doc.obligatoire}">
                                        <span class="optional-badge">OPTIONNEL</span>
                                    </c:if>
                                </label>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <!-- Section 6: Documents Spécifiques (loaded dynamically) -->
            <div class="form-section" id="specific-docs-section" style="display: none;">
                <h2>Pièces Complémentaires</h2>
                <div class="document-section" id="specific-docs-container">
                </div>
            </div>

            <!-- Form Actions -->
            <div class="form-actions">
                <button type="submit" class="btn-submit">Soumettre la Demande</button>
                <button type="reset" class="btn-reset">Réinitialiser</button>
            </div>
        </form>
    </div>

    <script>
        // Store type-specific documents mapping
        const documentsByMotif = {
            <c:forEach var="motif" items="${typeMotifs}" varStatus="status">
                ${motif.id}: [
                    <c:forEach var="doc" items="${documentsCommuns}" varStatus="docStatus">
                        ${doc.id}<c:if test="${!docStatus.last}">,</c:if>
                    </c:forEach>
                ]<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };

        function updateDocuments() {
            const motifSelect = document.getElementById('type_motif');
            const motifId = motifSelect.value;
            const specificSection = document.getElementById('specific-docs-section');
            const specificContainer = document.getElementById('specific-docs-container');

            if (motifId) {
                // For now, show/hide the section based on selection
                specificSection.style.display = 'block';
                // Additional document loading logic can be implemented here
            } else {
                specificSection.style.display = 'none';
            }
        }

        // Form validation
        document.querySelector('form').addEventListener('submit', function(e) {
            const requiredFields = this.querySelectorAll('[required]');
            let valid = true;

            requiredFields.forEach(field => {
                if (!field.value) {
                    valid = false;
                    field.style.borderColor = '#ff6b6b';
                } else {
                    field.style.borderColor = '#ddd';
                }
            });

            if (!valid) {
                e.preventDefault();
                alert('Veuillez remplir tous les champs obligatoires (marqués avec *)');
            }
        });
    </script>
</body>
</html>
