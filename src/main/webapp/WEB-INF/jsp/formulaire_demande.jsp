<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demande de Visa Transformable en Long Séjour</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #333;
            margin-bottom: 10px;
            text-align: center;
        }

        .subtitle {
            color: #666;
            text-align: center;
            margin-bottom: 30px;
            font-size: 14px;
        }

        .form-section {
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #ddd;
        }

        .form-section:last-child {
            border-bottom: none;
        }

        h2 {
            color: #0066cc;
            font-size: 18px;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 1px solid #e0e0e0;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            display: block;
            color: #333;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .required {
            color: red;
        }

        input[type="text"],
        input[type="email"],
        input[type="tel"],
        input[type="date"],
        input[type="number"],
        select,
        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            font-family: Arial, sans-serif;
        }

        input[type="text"]:focus,
        input[type="email"]:focus,
        input[type="tel"]:focus,
        input[type="date"]:focus,
        input[type="number"]:focus,
        select:focus,
        textarea:focus {
            outline: none;
            border-color: #0066cc;
            box-shadow: 0 0 5px rgba(0, 102, 204, 0.3);
        }

        textarea {
            resize: vertical;
            min-height: 80px;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .checkbox-group {
            background-color: #f9f9f9;
            padding: 15px;
            border-radius: 4px;
        }

        .checkbox-item {
            margin-bottom: 10px;
            display: flex;
            align-items: center;
        }

        input[type="checkbox"] {
            margin-right: 10px;
            cursor: pointer;
            width: 18px;
            height: 18px;
        }

        .checkbox-item label {
            margin: 0;
            font-weight: normal;
            cursor: pointer;
        }

        .form-actions {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin-top: 30px;
        }

        button {
            padding: 12px 30px;
            font-size: 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            transition: background-color 0.3s;
        }

        .btn-submit {
            background-color: #0066cc;
            color: white;
        }

        .btn-submit:hover {
            background-color: #0052a3;
        }

        .btn-reset {
            background-color: #ddd;
            color: #333;
        }

        .btn-reset:hover {
            background-color: #ccc;
        }

        .error-messages {
            background-color: #fee;
            color: #c33;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            border-left: 4px solid #c33;
            display: none;
        }

        .error-messages.show {
            display: block;
        }

        .error-messages ul {
            margin-left: 20px;
        }

        .error-messages li {
            margin-bottom: 5px;
        }

        .success-message {
            background-color: #efe;
            color: #3c3;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            border-left: 4px solid #3c3;
            display: none;
        }

        .success-message.show {
            display: block;
        }

        .field-help {
            font-size: 12px;
            color: #666;
            margin-top: 3px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Demande de Visa Transformable en Long Séjour</h1>
        <p class="subtitle">Veuillez remplir ce formulaire avec vos informations</p>

        <div id="errorMessages" class="error-messages">
            <strong>Erreurs détectées :</strong>
            <ul id="errorList"></ul>
        </div>

        <div id="successMessage" class="success-message">
            <strong>Succès !</strong> Votre demande a été créée avec l'ID: <span id="demandeId"></span>
        </div>

        <form id="demandeForm" name="demandeForm">
            <!-- Section 1: Informations du Demandeur -->
            <div class="form-section">
                <h2>1. Informations Personnelles</h2>

                <div class="form-row">
                    <div class="form-group">
                        <label for="nom">Nom <span class="required">*</span></label>
                        <input type="text" id="nom" name="nom" required>
                    </div>
                    <div class="form-group">
                        <label for="prenom">Prénom</label>
                        <input type="text" id="prenom" name="prenom">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="nomNaissance">Nom de Naissance</label>
                        <input type="text" id="nomNaissance" name="nomNaissance">
                    </div>
                    <div class="form-group">
                        <label for="dateNaissance">Date de Naissance <span class="required">*</span></label>
                        <input type="date" id="dateNaissance" name="dateNaissance" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="lieuNaissance">Lieu de Naissance</label>
                        <input type="text" id="lieuNaissance" name="lieuNaissance">
                    </div>
                    <div class="form-group">
                        <label for="idNationalite">Nationalité <span class="required">*</span></label>
                        <select id="idNationalite" name="idNationalite" required>
                            <option value="">-- Sélectionnez une nationalité --</option>
                            <option value="1">Française</option>
                            <option value="2">Belge</option>
                            <option value="3">Suisse</option>
                            <option value="4">Canadienne</option>
                            <option value="5">Autre</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="idSituationMatrimoniale">Situation Matrimoniale</label>
                        <select id="idSituationMatrimoniale" name="idSituationMatrimoniale">
                            <option value="">-- Sélectionnez une situation --</option>
                            <option value="1">Célibataire</option>
                            <option value="2">Marié(e)</option>
                            <option value="3">Divorcé(e)</option>
                            <option value="4">Veuf/Veuve</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Section 2: Coordonnées de Contact -->
            <div class="form-section">
                <h2>2. Coordonnées de Contact</h2>

                <div class="form-row">
                    <div class="form-group">
                        <label for="telephone">Numéro de Téléphone <span class="required">*</span></label>
                        <input type="tel" id="telephone" name="telephone" required>
                        <p class="field-help">Format: +33... ou 0...</p>
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email">
                    </div>
                </div>

                <div class="form-group">
                    <label for="adresseMadagascar">Adresse à Madagascar <span class="required">*</span></label>
                    <textarea id="adresseMadagascar" name="adresseMadagascar" required></textarea>
                </div>
            </div>

            <!-- Section 3: Informations du Passeport -->
            <div class="form-section">
                <h2>3. Informations du Passeport</h2>

                <div class="form-row">
                    <div class="form-group">
                        <label for="numeroPasseport">Numéro de Passeport</label>
                        <input type="text" id="numeroPasseport" name="numeroPasseport">
                    </div>
                    <div class="form-group">
                        <label for="dateDelivrancePasseport">Date de Délivrance</label>
                        <input type="date" id="dateDelivrancePasseport" name="dateDelivrancePasseport">
                    </div>
                </div>

                <div class="form-group">
                    <label for="dateExpirationPasseport">Date d'Expiration (Validité 6+ mois)</label>
                    <input type="date" id="dateExpirationPasseport" name="dateExpirationPasseport">
                </div>
            </div>

            <!-- Section 4: Informations du Visa Transformable -->
            <div class="form-section">
                <h2>4. Informations du Visa Transformable</h2>

                <div class="form-row">
                    <div class="form-group">
                        <label for="referenceVisa">Référence du Visa</label>
                        <input type="text" id="referenceVisa" name="referenceVisa">
                    </div>
                    <div class="form-group">
                        <label for="dateEntreeVisa">Date d'Entrée à Madagascar</label>
                        <input type="date" id="dateEntreeVisa" name="dateEntreeVisa">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="lieuEntreeVisa">Lieu d'Entrée (Port/Aéroport)</label>
                        <input type="text" id="lieuEntreeVisa" name="lieuEntreeVisa">
                    </div>
                    <div class="form-group">
                        <label for="dateExpirationVisa">Date d'Expiration du Visa</label>
                        <input type="date" id="dateExpirationVisa" name="dateExpirationVisa">
                    </div>
                </div>
            </div>

            <!-- Section 5: Type de Demande et Motif -->
            <div class="form-section">
                <h2>5. Type de Demande et Motif</h2>

                <div class="form-row">
                    <div class="form-group">
                        <label for="idTypeDemande">Type de Demande</label>
                        <select id="idTypeDemande" name="idTypeDemande">
                            <option value="">-- Sélectionnez un type --</option>
                            <option value="1">Travailleur</option>
                            <option value="2">Investisseur</option>
                            <option value="3">Autre</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="idTypeMotif">Motif du Long-Séjour</label>
                        <select id="idTypeMotif" name="idTypeMotif">
                            <option value="">-- Sélectionnez un motif --</option>
                            <option value="1">Travail</option>
                            <option value="2">Investissement</option>
                            <option value="3">Études</option>
                            <option value="4">Famille</option>
                            <option value="5">Autre</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Section 6: Documents à Fournir -->
            <div class="form-section">
                <h2>6. Documents à Fournir</h2>
                <p style="color: #666; margin-bottom: 15px;">Cochez les documents que vous fournissez:</p>

                <div class="checkbox-group">
                    <div class="checkbox-item">
                        <input type="checkbox" id="doc1" name="piecesPresentes" value="1">
                        <label for="doc1">Passeport original signé (validité 6+ mois)</label>
                    </div>
                    <div class="checkbox-item">
                        <input type="checkbox" id="doc2" name="piecesPresentes" value="2">
                        <label for="doc2">Formulaire complété</label>
                    </div>
                    <div class="checkbox-item">
                        <input type="checkbox" id="doc3" name="piecesPresentes" value="3">
                        <label for="doc3">Deux photos identiques récentes (4,5cm x 3,5cm)</label>
                    </div>
                    <div class="checkbox-item">
                        <input type="checkbox" id="doc4" name="piecesPresentes" value="4">
                        <label for="doc4">Billet d'avion (aller-retour ou OPEN)</label>
                    </div>
                    <div class="checkbox-item">
                        <input type="checkbox" id="doc5" name="piecesPresentes" value="5">
                        <label for="doc5">Contrat de travail / Statuts de la société</label>
                    </div>
                    <div class="checkbox-item">
                        <input type="checkbox" id="doc6" name="piecesPresentes" value="6">
                        <label for="doc6">Attestation de l'autorisation d'emploi</label>
                    </div>
                    <div class="checkbox-item">
                        <input type="checkbox" id="doc7" name="piecesPresentes" value="7">
                        <label for="doc7">Extrait du casier judiciaire (Bulletin N°3)</label>
                    </div>
                    <div class="checkbox-item">
                        <input type="checkbox" id="doc8" name="piecesPresentes" value="8">
                        <label for="doc8">Attestation de changement de résidence</label>
                    </div>
                </div>
            </div>

            <!-- Résumé avant enregistrement -->
            <div class="form-section">
                <h2>7. Vérification et Confirmation</h2>
                <div style="background-color: #f0f8ff; padding: 15px; border-radius: 4px; border-left: 4px solid #0066cc;">
                    <p style="color: #333; margin-bottom: 10px;">
                        <strong>Note importante :</strong> Votre dossier sera créé avec le statut "Dossier créé" et les documents manquants 
                        empêcheront la progression du dossier. Vous pouvez compléter votre dossier ultérieurement.
                    </p>
                    <p style="color: #666; font-size: 12px;">
                        Les champs marqués d'un <span class="required">*</span> sont obligatoires.
                    </p>
                </div>
            </div>

            <!-- Boutons d'action -->
            <div class="form-actions">
                <button type="submit" class="btn-submit">Soumettre la Demande</button>
                <button type="reset" class="btn-reset">Réinitialiser</button>
            </div>
        </form>
    </div>

    <script>
        document.getElementById('demandeForm').addEventListener('submit', function(e) {
            e.preventDefault();

            // Récupérer les données du formulaire
            const formData = new FormData(this);
            const piecesPresentes = formData.getAll('piecesPresentes');

            const demandeData = {
                nom: formData.get('nom'),
                prenom: formData.get('prenom'),
                nomNaissance: formData.get('nomNaissance'),
                email: formData.get('email'),
                telephone: formData.get('telephone'),
                idNationalite: formData.get('idNationalite') ? parseInt(formData.get('idNationalite')) : null,
                dateNaissance: formData.get('dateNaissance'),
                lieuNaissance: formData.get('lieuNaissance'),
                idSituationMatrimoniale: formData.get('idSituationMatrimoniale') ? parseInt(formData.get('idSituationMatrimoniale')) : null,
                adresseMadagascar: formData.get('adresseMadagascar'),
                numeroPasseport: formData.get('numeroPasseport'),
                dateDelivrancePasseport: formData.get('dateDelivrancePasseport'),
                dateExpirationPasseport: formData.get('dateExpirationPasseport'),
                referenceVisa: formData.get('referenceVisa'),
                dateEntreeVisa: formData.get('dateEntreeVisa'),
                lieuEntreeVisa: formData.get('lieuEntreeVisa'),
                dateExpirationVisa: formData.get('dateExpirationVisa'),
                idTypeDemande: formData.get('idTypeDemande') ? parseInt(formData.get('idTypeDemande')) : null,
                idTypeMotif: formData.get('idTypeMotif') ? parseInt(formData.get('idTypeMotif')) : null,
                piecesPresentes: piecesPresentes.map(p => parseInt(p))
            };

            // Envoyer les données au serveur
            fetch('/api/demandes/creer', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(demandeData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Afficher le message de succès
                    document.getElementById('successMessage').classList.add('show');
                    document.getElementById('demandeId').textContent = data.demandeId;
                    document.getElementById('errorMessages').classList.remove('show');
                    document.getElementById('demandeForm').reset();

                    // Redirection après 3 secondes
                    setTimeout(() => {
                        window.location.href = '/api/demandes/' + data.demandeId;
                    }, 3000);
                } else {
                    // Afficher les erreurs
                    document.getElementById('errorMessages').classList.add('show');
                    document.getElementById('successMessage').classList.remove('show');
                    const errorList = document.getElementById('errorList');
                    errorList.innerHTML = '';
                    if (data.errors && data.errors.length > 0) {
                        data.errors.forEach(error => {
                            const li = document.createElement('li');
                            li.textContent = error;
                            errorList.appendChild(li);
                        });
                    } else {
                        const li = document.createElement('li');
                        li.textContent = data.message || 'Erreur lors de la création de la demande';
                        errorList.appendChild(li);
                    }
                }
            })
            .catch(error => {
                console.error('Erreur:', error);
                document.getElementById('errorMessages').classList.add('show');
                document.getElementById('successMessage').classList.remove('show');
                const errorList = document.getElementById('errorList');
                errorList.innerHTML = '<li>Erreur de communication avec le serveur</li>';
            });
        });
    </script>
</body>
</html>
