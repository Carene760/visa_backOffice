<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<style>
    .form-container {
        background: var(--card-bg);
        padding: 28px;
        border: 1px solid #9a9a9a;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    }

    .form-banner {
        margin-bottom: 20px;
        padding-bottom: 12px;
        border-bottom: 1px solid #888;
    }

    .form-banner h2 {
        margin: 0;
        text-align: center;
        color: var(--text-main);
        font-size: 24px;
        text-transform: uppercase;
        letter-spacing: 0.7px;
    }

    .form-banner p {
        margin-top: 8px;
        text-align: center;
        color: var(--text-soft);
        font-size: 13px;
    }

    .form-section {
        margin-bottom: 20px;
        padding: 16px;
        background: #fff;
        border: 1px solid var(--line);
    }

    .form-section h3 {
        color: var(--text-main);
        font-size: 17px;
        margin: 0 0 14px 0;
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
        background: #fff;
        transition: border-color 0.2s, background-color 0.2s;
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

    .document-section h4 {
        color: #333;
        font-size: 16px;
        margin: 0 0 15px 0;
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
    }

    .document-item input[type="checkbox"] {
        width: 18px;
        height: 18px;
        cursor: pointer;
    }

    .select-all-row {
        display: flex;
        align-items: center;
        gap: 10px;
        margin: 0 0 12px 0;
        padding: 10px 12px;
        background: #f4f7fb;
        border: 1px solid #d9e2ef;
        border-radius: 4px;
        font-size: 14px;
        font-weight: 600;
    }

    .document-item label {
        margin: 0;
        font-weight: 400;
        cursor: pointer;
        font-size: 13px;
    }

    .mandatory-badge,
    .optional-badge {
        display: inline-block;
        padding: 2px 6px;
        border-radius: 3px;
        font-size: 11px;
        font-weight: bold;
        margin-left: 5px;
        color: #fff;
    }

    .mandatory-badge {
        background: #b82121;
    }

    .optional-badge {
        background: #6a6a6a;
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
    }

    .btn-reset {
        background: #ecf0f1;
        color: #333;
    }

    .btn-reset:hover {
        background: #bdc3c7;
    }

    .info-note {
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

    .checkbox-row {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 16px;
        background: #f9f9f9;
        border: 1px solid #e0e0e0;
        border-radius: 4px;
        margin-top: 12px;
    }

    .checkbox-row input[type="checkbox"] {
        width: 20px;
        height: 20px;
        cursor: pointer;
    }

    .checkbox-row label {
        margin: 0;
        cursor: pointer;
        font-weight: 500;
        font-size: 14px;
    }

    .checkbox-description {
        display: block;
        font-size: 12px;
        color: var(--text-soft);
        margin-top: 4px;
        font-weight: 400;
    }

    @media (max-width: 900px) {
        .document-list {
            grid-template-columns: 1fr;
        }
    }

    @media (max-width: 700px) {
        .form-container {
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

<div class="form-container">
    <div class="form-banner">
        <h2>${not empty formContextTitle ? formContextTitle : pageTitle}</h2>
        <p>${formContextHelp}</p>
    </div>

    <c:if test="${not empty erreur}">
        <div class="error-container" style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 12px 20px; margin-bottom: 20px; color: #721c24;">
            <h3 style="margin-top: 0; color: #721c24;">Erreurs de validation</h3>
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

    <form method="post" action="/demande/creer">
        <!-- Champ caché pour passer le type de demande Sprint 2 -->
        <input type="hidden" name="type_demande_sprint2" value="${not empty typeDemandeSprint2 ? typeDemandeSprint2 : param.type}">
        <!-- Champ caché pour l'identifiant du type de visa (1=TRANSFORMABLE, 2=VALIDE) -->
        <input type="hidden" id="id_type_visa" name="id_type_visa" value="1">

        <!-- SECTION 1: Type de Demande (déplacée en première position) -->
        <div class="form-section">
            <h3>Type de Demande</h3>
            <div class="form-rows full">
                <div class="form-group">
                    <label>Nature de la demande</label>
                    <input type="text" value="${not empty formContextTitle ? formContextTitle : pageTitle}" readonly>
                </div>
            </div>
                <script>
                    document.addEventListener('DOMContentLoaded', function() {
                        var checkbox = document.getElementById('sansdonneesAnterieures');
                        var hidden = document.getElementById('id_type_visa');
                        var display = document.getElementById('visa_type_display');
                        function updateVisaType() {
                            if (!hidden || !display) return;
                            if (checkbox && checkbox.checked) {
                                hidden.value = '2';
                                display.value = 'VALIDE';
                            } else {
                                hidden.value = '1';
                                display.value = 'TRANSFORMABLE';
                            }
                        }
                        if (checkbox) checkbox.addEventListener('change', updateVisaType);
                        updateVisaType();
                    });
                </script>
            <div class="form-rows">
                <div class="form-group">
                    <label for="type_motif" class="required">Statut du visa demandé</label>
                    <select id="type_motif" name="type_motif" onchange="updateDocuments()">
                        <option value="">-- Sélectionner --</option>
                        <c:forEach var="motif" items="${typeMotifs}">
                            <option value="${motif.id}" <c:if test="${motif.id == demandeDTO.idTypeMotif}">selected</c:if>>${motif.libelle}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="type_demande" class="required">Type de Demande</label>
                    <input type="hidden" id="type_demande" name="type_demande" value="1">
                    <input type="text" value="NOUVEAU_TITRE" readonly>
                    <p class="info-note">Type par défaut: NOUVEAU_TITRE (id=1)</p>
                </div>
            </div>
            
            <!-- Checkbox "Sans données antérieures" pour NOUVEAU_TITRE -->
            <c:if test="${typeDemandeSprint2 eq 'NOUVEAU_TITRE' or param.type eq 'NOUVEAU_TITRE' or demandeDTO.idTypeDemande == 1}">
                <div class="checkbox-row">
                    <input type="checkbox" id="sansdonneesAnterieures" name="sansdonneesAnterieures" value="true" 
                           <c:if test="${demandeDTO.sansdonneesAnterieures}">checked</c:if>>
                    <label for="sansdonneesAnterieures">
                        Cette demande est sans données antérieures
                        <span class="checkbox-description">
                            Cochez cette case si cette demande existait déjà et est approuvée, mais vous n'avez pas les données précédentes.
                            Vous serez redirigé vers le formulaire Duplicata pré-rempli après création.
                        </span>
                    </label>
                </div>
            </c:if>
        </div>

        <div class="form-section">
            <h3>Informations Personnelles</h3>
            <div class="form-rows">
                <div class="form-group">
                    <label for="demandeur_nom" class="required">Nom</label>
                    <input type="text" id="demandeur_nom" name="demandeur_nom" value="${demandeDTO.nom}">
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
                    <input type="date" id="demandeur_date_naissance" name="demandeur_date_naissance" value="${demandeDTO.dateNaissance}">
                </div>
                <div class="form-group">
                    <label for="demandeur_lieu_naissance">Lieu de Naissance</label>
                    <input type="text" id="demandeur_lieu_naissance" name="demandeur_lieu_naissance" value="${demandeDTO.lieuNaissance}">
                </div>
                <div class="form-group">
                    <label for="demandeur_nationalite" class="required">Nationalité</label>
                    <select id="demandeur_nationalite" name="demandeur_nationalite">
                        <option value="">-- Sélectionner --</option>
                        <c:forEach var="nat" items="${nationalites}">
                            <option value="${nat.id}" <c:if test="${nat.id == demandeDTO.idNationalite}">selected</c:if>>${nat.libelle}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="demandeur_situation" class="required">Situation Matrimoniale</label>
                    <select id="demandeur_situation" name="demandeur_situation">
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
                    <textarea id="demandeur_adresse" name="demandeur_adresse">${demandeDTO.adresseMadagascar}</textarea>
                </div>
            </div>
            <div class="form-rows">
                <div class="form-group">
                    <label for="demandeur_email">Email</label>
                    <input type="email" id="demandeur_email" name="demandeur_email" value="${demandeDTO.email}">
                </div>
                <div class="form-group">
                    <label for="demandeur_telephone" class="required">Téléphone</label>
                    <input type="tel" id="demandeur_telephone" name="demandeur_telephone" value="${demandeDTO.telephone}">
                </div>
            </div>
        </div>

        <div class="form-section">
            <h3>Informations Passeport</h3>
            <div class="form-rows">
                <div class="form-group">
                    <label for="passeport_numero" class="required">Numéro de Passeport</label>
                    <input type="text" id="passeport_numero" name="passeport_numero" value="${demandeDTO.numeroPasseport}">
                </div>
                <div class="form-group">
                    <label for="passeport_date_delivrance" class="required">Date de Délivrance</label>
                    <input type="date" id="passeport_date_delivrance" name="passeport_date_delivrance" value="${demandeDTO.dateDelivrancePasseport}">
                </div>
                <div class="form-group">
                    <label for="passeport_date_expiration" class="required">Date d'Expiration</label>
                    <input type="date" id="passeport_date_expiration" name="passeport_date_expiration" value="${demandeDTO.dateExpirationPasseport}">
                </div>
            </div>
        </div>

        <div class="form-section">
            <h3>Informations Visa</h3>
            <div class="form-rows">
                <div class="form-group">
                    <label for="visa_reference" class="required">Référence du Visa</label>
                    <input type="text" id="visa_reference" name="visa_reference" value="${demandeDTO.referenceVisa}">
                </div>
                <div class="form-group">
                    <label for="visa_date_entree" class="required">Date d'Entrée</label>
                    <input type="date" id="visa_date_entree" name="visa_date_entree" value="${demandeDTO.dateEntreeVisa}">
                </div>
                <div class="form-group">
                    <label for="visa_lieu_entree">Lieu d'Entrée</label>
                    <input type="text" id="visa_lieu_entree" name="visa_lieu_entree" value="${demandeDTO.lieuEntreeVisa}">
                </div>
                <div class="form-group">
                    <label for="visa_date_expiration" class="required">Date d'Expiration</label>
                    <input type="date" id="visa_date_expiration" name="visa_date_expiration" value="${demandeDTO.dateExpirationVisa}">
                </div>
                <div class="form-group">
                    <label>Type de visa (conception)</label>
                    <input type="text" id="visa_type_display" value="TRANSFORMABLE" readonly>
                </div>
            </div>
        </div>

        <div class="form-section">
            <h3>Pièces Communes</h3>
            <div class="document-section">
                <h4>Documents à fournir</h4>
                <div class="select-all-row">
                    <input type="checkbox" id="selectAllDocuments">
                    <label for="selectAllDocuments" style="margin: 0; font-weight: 600;">Tout sélectionner</label>
                </div>
                <div class="document-list">
                    <c:forEach var="doc" items="${documentsCommuns}">
                        <div class="document-item">
                            <input type="checkbox" id="doc_${doc.id}" name="documents" value="${doc.id}">
                            <label for="doc_${doc.id}">
                                ${doc.libelle}
                                <c:choose>
                                    <c:when test="${doc.obligatoire}">
                                        <span class="mandatory-badge">OBLIGATOIRE</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="optional-badge">OPTIONNEL</span>
                                    </c:otherwise>
                                </c:choose>
                            </label>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="form-section" id="specific-docs-section" style="display: none;">
            <h3>Pièces Complémentaires</h3>
            <div class="document-section" id="specific-docs-container">
                <div id="specific-docs-template" style="display: none;">
                    <c:forEach var="doc" items="${documentsSpecifiques}">
                        <div class="document-item specific-doc-item" data-motif-id="${doc.typeMotif.id}">
                            <input type="checkbox" id="doc_spec_${doc.id}" name="documents" value="${doc.id}">
                            <label for="doc_spec_${doc.id}">
                                ${doc.libelle}
                                <c:choose>
                                    <c:when test="${doc.obligatoire}">
                                        <span class="mandatory-badge">OBLIGATOIRE</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="optional-badge">OPTIONNEL</span>
                                    </c:otherwise>
                                </c:choose>
                            </label>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="form-section">
            <h3>Résumé Avant Enregistrement</h3>
            <div class="resume-box">
                Vérifier les informations d'état civil, du passeport, du visa et les pièces choisies.
                L'interface s'adapte au mode choisi dans le sidebar.
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-submit">Soumettre la Demande</button>
            <button type="reset" class="btn-reset">Réinitialiser</button>
        </div>
    </form>
</div>

<script>
    const modeTestParam = '${param.modeTest}';
    const modeTestEnabled = modeTestParam === '1' || modeTestParam === 'true' || modeTestParam === 'on';
    
    // Garde en mémoire les cases cochées des pièces communes (après erreur ou changement motif)
    let checkedDocuments = new Set();

    function updateDocuments() {
        const motifSelect = document.getElementById('type_motif');
        const motifId = motifSelect ? motifSelect.value : '';
        const specificSection = document.getElementById('specific-docs-section');
        const specificContainer = document.getElementById('specific-docs-container');
        const template = document.getElementById('specific-docs-template');

        if (!specificSection || !specificContainer || !template) {
            return;
        }

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
                specificContainer.innerHTML = '<div class="document-list">' + itemsHtml.join('') + '</div>';
            }
            
            // Recheck les spécifiques qui étaient cochées
            const newCheckboxes = specificContainer.querySelectorAll('input[type="checkbox"]');
            newCheckboxes.forEach(checkbox => {
                if (checkedDocuments.has(checkbox.value)) {
                    checkbox.checked = true;
                }
            });
        } else {
            specificSection.style.display = 'none';
        }
    }

    function applyTestDefaults() {
        const defaults = {
            demandeur_nom: 'Dupont',
            demandeur_prenom: 'Jean',
            demandeur_nom_naissance: 'Durand',
            demandeur_date_naissance: '1990-01-15',
            demandeur_lieu_naissance: 'Paris',
            demandeur_nationalite: '1',
            demandeur_situation: '1',
            demandeur_adresse: '123 Rue de la Paix, Antananarivo 101',
            demandeur_email: 'jean.dupont@example.com',
            demandeur_telephone: '+261 34 12 34 567',
            passeport_numero: 'AB123456',
            passeport_date_delivrance: '2019-06-10',
            passeport_date_expiration: '2029-06-09',
            visa_reference: 'VIS-2024-0001',
            visa_date_entree: '2024-01-20',
            visa_lieu_entree: 'Ivato',
            visa_date_expiration: '2026-01-19',
            type_motif: '1',
            type_demande: '1'
        };

        Object.keys(defaults).forEach(key => {
            const element = document.getElementById(key);
            if (element) {
                element.value = defaults[key];
            }
        });

        const checkboxes = document.querySelectorAll('input[name="documents"]');
        if (checkboxes.length > 0) {
            checkboxes.forEach(checkbox => {
                checkbox.checked = true;
                checkedDocuments.add(checkbox.value);
            });
        }

        updateDocuments();
    }

    function bindSelectAll() {
        const selectAll = document.getElementById('selectAllDocuments');
        if (!selectAll) {
            return;
        }

        selectAll.addEventListener('change', function () {
            document.querySelectorAll('input[name="documents"]').forEach(checkbox => {
                checkbox.checked = selectAll.checked;
                if (selectAll.checked) {
                    checkedDocuments.add(checkbox.value);
                } else {
                    checkedDocuments.delete(checkbox.value);
                }
            });
        });
    }

    function trackDocumentCheckboxes() {
        document.addEventListener('change', function(e) {
            if (e.target.name === 'documents') {
                if (e.target.checked) {
                    checkedDocuments.add(e.target.value);
                } else {
                    checkedDocuments.delete(e.target.value);
                }
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        // Restaure les cases cochées lors du chargement (après erreur de formulaire)
        document.querySelectorAll('input[name="documents"]:checked').forEach(checkbox => {
            checkedDocuments.add(checkbox.value);
        });
        
        trackDocumentCheckboxes();
        updateDocuments();
        bindSelectAll();
        // Auto-select all documents when 'sans données antérieures' is checked
        const sansCheckbox = document.getElementById('sansdonneesAnterieures');
        if (sansCheckbox) {
            sansCheckbox.addEventListener('change', function () {
                const selectAll = document.getElementById('selectAllDocuments');
                const specificContainer = document.getElementById('specific-docs-container');
                if (this.checked) {
                    if (selectAll) { selectAll.checked = true; selectAll.dispatchEvent(new Event('change')); }
                    // check visible specific docs
                    if (specificContainer) {
                        specificContainer.querySelectorAll('input[name="documents"]').forEach(cb => { cb.checked = true; checkedDocuments.add(cb.value); });
                    }
                } else {
                    if (selectAll) { selectAll.checked = false; selectAll.dispatchEvent(new Event('change')); }
                    if (specificContainer) {
                        specificContainer.querySelectorAll('input[name="documents"]').forEach(cb => { cb.checked = false; checkedDocuments.delete(cb.value); });
                    }
                }
            });
            // apply initial state if checked on load
            if (sansCheckbox.checked) {
                sansCheckbox.dispatchEvent(new Event('change'));
            }
        }
        if (modeTestEnabled) {
            applyTestDefaults();
        }
    });
</script>