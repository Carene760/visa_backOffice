<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/jsp/layout.jsp">
    <jsp:param name="pageTitle" value="Demande de Visa Transformable" />
</jsp:include>

<div class="main-content">
    <div class="container">
        <style>
            .container {
                max-width: 960px;
                margin: 0 auto;
                background: var(--card-bg);
                padding: 28px;
                border: 1px solid #9a9a9a;
                box-shadow: 0 8px 24px rgba(0,0,0,0.08);
            }
            h1 {
                color: var(--text-main);
                margin-bottom: 24px;
                text-align: center;
                border-bottom: 1px solid #888;
                padding-bottom: 12px;
                font-size: 24px;
                text-transform: uppercase;
                letter-spacing: 0.7px;
            }
            .form-section {
                margin-bottom: 20px;
                padding: 16px;
                background: #fff;
                border: 1px solid var(--line);
            }
            .form-section h2 {
                color: var(--text-main);
                font-size: 17px;
                margin-bottom: 14px;
                text-transform: uppercase;
                border-left: 4px solid var(--accent);
                padding-left: 10px;
            }
            .form-rows {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 16px;
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
                border: 1px solid #8f8f8f;
                border-radius: 2px;
                font-size: 14px;
                transition: border-color 0.2s, background-color 0.2s;
                background: #fff;
            }
            input[type="text"]:focus,
            input[type="email"]:focus,
            input[type="tel"]:focus,
            input[type="date"]:focus,
            select:focus,
            textarea:focus {
                outline: none;
                border-color: var(--accent);
                background-color: #fcfdff;
                box-shadow: none;
            }
            textarea {
                resize: vertical;
                min-height: 80px;
            }
            .document-section {
                margin-top: 8px;
                padding: 12px;
                background: #fcfcfc;
                border: 1px dashed #b7b7b7;
            }
            .document-section h3 {
                color: #333;
                font-size: 16px;
                margin-bottom: 15px;
            }
            .document-list {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 10px;
            }
            .document-item {
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 10px;
                background: #fafafa;
                border: 1px solid #d9d9d9;
                border-radius: 2px;
                transition: background-color 0.2s, border-color 0.2s;
            }
            .document-item:hover {
                background: #f3f7fb;
                border-color: #b8c7da;
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
                background: #b82121;
                color: white;
                padding: 2px 6px;
                border-radius: 3px;
                font-size: 11px;
                font-weight: bold;
                margin-left: 5px;
            }
            .optional-badge {
                display: inline-block;
                background: #6a6a6a;
                color: white;
                padding: 2px 6px;
                border-radius: 3px;
                font-size: 11px;
                font-weight: bold;
                margin-left: 5px;
            }
            .form-actions {
                display: flex;
                gap: 15px;
                margin-top: 30px;
                justify-content: center;
                border-top: 1px solid #d7d7d7;
                padding-top: 18px;
            }
            button {
                padding: 12px 30px;
                border: none;
                border-radius: 2px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: background-color 0.2s, transform 0.2s;
            }
            .btn-submit {
                background: var(--accent);
                color: white;
            }
            .btn-submit:hover {
                background: var(--accent-dark);
                transform: translateY(-1px);
                box-shadow: none;
            }
            .btn-reset {
                background: #ecf0f1;
                color: #333;
            }
            .btn-reset:hover {
                background: #bdc3c7;
            }
        </style>
            margin-top: 8px;
            font-size: 13px;
            color: var(--text-soft);
        }
        .resume-box {
            padding: 10px;
            border: 1px dashed #999;
            background: #fcfcfc;
            line-height: 1.6;
        }
        @media (max-width: 900px) {
            .document-list {
                grid-template-columns: 1fr;
            }
        }
        @media (max-width: 700px) {
            body {
                padding: 10px;
            }
            .container {
                padding: 16px;
            }
            .form-rows {
                grid-template-columns: 1fr;
                gap: 12px;
            }
            button {
                width: 100%;
            }
            .form-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Demande de Visa Transformable en Long Sejour</h1>

        <!-- Affichage des erreurs de validation -->
        <c:if test="${not empty erreur}">
            <div class="error-container" style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 12px 20px; margin-bottom: 20px; color: #721c24;">
                <h3 style="margin-top: 0; color: #721c24;">❌ Erreurs de validation</h3>
                <p><strong>${erreur}</strong></p>
                <c:if test="${not empty erreurs}">
                    <ul style="margin: 10px 0 0 20px;">
                        <c:forEach var="err" items="${erreurs}">
                            <li>${err}</li>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>
        </c:if>

        <form method = "post" action="/demande/creer">
            <!-- Section 1: Informations Personnelles du Demandeur -->
            <div class="form-section">
                <h2>Informations Personnelles</h2>
                <div class="form-rows">
                    <div class="form-group">
                        <label for="demandeur_nom" class="required">Nom</label>
                        <input type="text" id="demandeur_nom" name="demandeur_nom" value="${demandeDTO.nom}" required>
                    </div>
                    <div class="form-group">
                        <label for="demandeur_prenom">Prénom</label>
                        <input type="text" id="demandeur_prenom" name="demandeur_prenom" value="${demandeDTO.prenom}">
                    </div>
                    <div class="form-group">
                        <label for="demandeur_nom_naissance">Nom à la Naissance</label>
                        <input type="text" id="demandeur_nom_naissance" name="demandeur_nom_naissance" value="${demandeDTO.nomNaissance}">
                    </div>
                    <div class="form-group">
                        <label for="demandeur_date_naissance" class="required">Date de Naissance</label>
                        <input type="date" id="demandeur_date_naissance" name="demandeur_date_naissance" value="${demandeDTO.dateNaissance}" required>
                    </div>
                    <div class="form-group">
                        <label for="demandeur_lieu_naissance">Lieu de Naissance</label>
                        <input type="text" id="demandeur_lieu_naissance" name="demandeur_lieu_naissance" value="${demandeDTO.lieuNaissance}">
                    </div>
                    <div class="form-group">
                        <label for="demandeur_nationalite" class="required">Nationalité</label>
                        <select id="demandeur_nationalite" name="demandeur_nationalite" required>
                            <option value="">-- Sélectionner --</option>
                            <c:forEach var="nat" items="${nationalites}">
                                <option value="${nat.id}" <c:if test="${nat.id == demandeDTO.idNationalite}">selected</c:if>>${nat.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="demandeur_situation" class="required">Situation Matrimoniale</label>
                        <select id="demandeur_situation" name="demandeur_situation" required>
                            <option value="">-- Sélectionner --</option>
                            <c:forEach var="sit" items="${situations}">
                                <option value="${sit.id}" <c:if test="${sit.id == demandeDTO.idSituationMatrimoniale}">selected</c:if>>${sit.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-rows full">
                    <div class="form-group">
                        <label for="demandeur_adresse" class="required">Adresse à Madagascar</label>
                        <textarea id="demandeur_adresse" name="demandeur_adresse" required>${demandeDTO.adresseMadagascar}</textarea>
                    </div>
                </div>
                <div class="form-rows">
                    <div class="form-group">
                        <label for="demandeur_email">Email</label>
                        <input type="email" id="demandeur_email" name="demandeur_email" value="${demandeDTO.email}">
                    </div>
                    <div class="form-group">
                        <label for="demandeur_telephone" class="required">Téléphone</label>
                        <input type="tel" id="demandeur_telephone" name="demandeur_telephone" value="${demandeDTO.telephone}" required>
                    </div>
                </div>
            </div>

            <!-- Section 2: Informations Passeport -->
            <div class="form-section">
                <h2>Informations Passeport</h2>
                <div class="form-rows">
                    <div class="form-group">
                        <label for="passeport_numero" class="required">Numéro de Passeport</label>
                        <input type="text" id="passeport_numero" name="passeport_numero" value="${demandeDTO.numeroPasseport}" required>
                    </div>
                    <div class="form-group">
                        <label for="passeport_date_delivrance" class="required">Date de Délivrance</label>
                        <input type="date" id="passeport_date_delivrance" name="passeport_date_delivrance" value="${demandeDTO.dateDelivrancePasseport}" required>
                    </div>
                    <div class="form-group">
                        <label for="passeport_date_expiration" class="required">Date d'Expiration</label>
                        <input type="date" id="passeport_date_expiration" name="passeport_date_expiration" value="${demandeDTO.dateExpirationPasseport}" required>
                    </div>
                </div>
            </div>

            <!-- Section 3: Informations Visa -->
            <div class="form-section">
                <h2>Informations Visa</h2>
                <div class="form-rows">
                    <div class="form-group">
                        <label for="visa_reference" class="required">Référence du Visa</label>
                        <input type="text" id="visa_reference" name="visa_reference" value="${demandeDTO.referenceVisa}" required>
                    </div>
                    <div class="form-group">
                        <label for="visa_date_entree" class="required">Date d'Entrée</label>
                        <input type="date" id="visa_date_entree" name="visa_date_entree" value="${demandeDTO.dateEntreeVisa}" required>
                    </div>
                    <div class="form-group">
                        <label for="visa_lieu_entree">Lieu d'Entrée</label>
                        <input type="text" id="visa_lieu_entree" name="visa_lieu_entree" value="${demandeDTO.lieuEntreeVisa}">
                    </div>
                    <div class="form-group">
                        <label for="visa_date_expiration" class="required">Date d'Expiration</label>
                        <input type="date" id="visa_date_expiration" name="visa_date_expiration" value="${demandeDTO.dateExpirationVisa}" required>
                    </div>
                    <div class="form-group">
                        <label>Type de visa (conception)</label>
                        <input type="text" value="TRANSFORMABLE" readonly>
                    </div>
                </div>
            </div>

            <!-- Section 4: Type de Demande / Categorie -->
            <div class="form-section">
                <h2>Type de Demande</h2>
                <div class="form-group">
                    <label for="type_motif" class="required">Motif de Transformation</label>
                    <label>Nature de la demande (Sprint 1)</label>
                    <input type="text" value="Nouveau titre" readonly>
                </div>

                <div class="form-group">
                    <label for="type_motif" class="required">Statut du visa demandé</label>
                    <select id="type_motif" name="type_motif" required onchange="updateDocuments()">
                        <option value="">-- Sélectionner --</option>
                        <c:forEach var="motif" items="${typeMotifs}">
                            <option value="${motif.id}" <c:if test="${motif.id == demandeDTO.idTypeMotif}">selected</c:if>>${motif.libelle}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="type_demande" class="required">Type de Demande</label>
                    <select id="type_demande" name="type_demande" required>
                        <option value="">-- Sélectionner --</option>
                        <c:forEach var="type" items="${typesDemande}">
                            <option value="${type.id}" <c:if test="${type.id == demandeDTO.idTypeDemande}">selected</c:if>>${type.libelle}</option>
                        </c:forEach>
                    </select>
                    <p class="info-note">Choisir uniquement Travailleur ou Investisseur pour le Sprint 1.</p>
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

            <!-- Section 6: Documents Spécifiques par Catégorie -->
            <div class="form-section" id="specific-docs-section" style="display: none;">
                <h2>Pièces Complémentaires</h2>
                <div class="document-section" id="specific-docs-container">
                    <div id="specific-docs-template" style="display: none;">
                        <c:forEach var="doc" items="${documentsSpecifiques}">
                            <div class="document-item specific-doc-item" data-motif-id="${doc.typeMotif.id}">
                                <input type="checkbox" id="doc_spec_${doc.id}" name="documents" value="${doc.id}">
                                <label for="doc_spec_${doc.id}">
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

            <div class="form-section">
                <h2>Résumé Avant Enregistrement</h2>
                <div class="resume-box">
                    Vérifier les informations d'etat civil, du passeport, du visa transformable et les pieces cochees.
                    Dans le Sprint 1, aucun upload n'est gere: seules les cases de presence des dossiers sont utilisees.
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
        // Placeholder map for specific documents by motif.
        // Populated later when specific document rendering is implemented.
        const documentsByMotif = {};

        function updateDocuments() {
            const motifSelect = document.getElementById('type_motif');
            const motifId = motifSelect.value;
            const specificSection = document.getElementById('specific-docs-section');
            const specificContainer = document.getElementById('specific-docs-container');
            const template = document.getElementById('specific-docs-template');

            if (motifId) {
                specificSection.style.display = 'block';
                const allSpecificItems = template.querySelectorAll('.specific-doc-item');
                const itemsHtml = [];

                allSpecificItems.forEach(item => {
                    if (item.getAttribute('data-motif-id') === motifId) {
                        itemsHtml.push(item.outerHTML);
                    }
                });

                if (itemsHtml.length === 0) {
                    specificContainer.innerHTML = '<p>Aucune pièce complémentaire définie pour cette catégorie.</p>';
                } else {
                    specificContainer.innerHTML = '<div class="document-list">' + itemsHtml.join('') + '</div>' + template.outerHTML;
                }
            } else {
                specificSection.style.display = 'none';
            }
        }

    </script>
    </div>
</div>
</body>
</html>
