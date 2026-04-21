# Guide d'Utilisation des APIs DEV2

## Endpoints disponibles

### 1. Créer une nouvelle demande

**URL:** `POST /api/demandes/creer`

**Body (JSON):**
```json
{
  "nom": "Dupont",
  "prenom": "Jean",
  "nomNaissance": "Dupin",
  "email": "jean.dupont@example.com",
  "telephone": "+33612345678",
  "idNationalite": 1,
  "dateNaissance": "1990-05-15",
  "lieuNaissance": "Paris",
  "idSituationMatrimoniale": 1,
  "adresseMadagascar": "45 Avenue des Champs, Antananarivo, Madagascar",
  "numeroPasseport": "AB123456",
  "dateDelivrancePasseport": "2020-03-10",
  "dateExpirationPasseport": "2030-03-10",
  "referenceVisa": "VIS123456",
  "dateEntreeVisa": "2024-01-15",
  "lieuEntreeVisa": "Aéroport d'Ivato",
  "dateExpirationVisa": "2024-03-15",
  "idTypeDemande": 1,
  "idTypeMotif": 1,
  "piecesPresentes": [1, 2, 3, 4]
}
```

**Réponse (succès):**
```json
{
  "success": true,
  "message": "Demande créée avec succès",
  "errors": [],
  "demandeId": 5
}
```

**Réponse (erreur):**
```json
{
  "success": false,
  "message": "Erreurs de validation détectées",
  "errors": [
    "Le nom du demandeur est obligatoire",
    "Le numéro de téléphone est obligatoire"
  ],
  "demandeId": null
}
```

**Statut HTTP:**
- `201 Created` - Demande créée avec succès
- `400 Bad Request` - Erreurs de validation
- `500 Internal Server Error` - Erreur serveur

---

### 2. Récupérer les détails d'une demande

**URL:** `GET /api/demandes/{demandeId}`

**Exemple:** `GET /api/demandes/5`

**Réponse:**
```json
{
  "id": 5,
  "demandeur": {
    "id": 3,
    "nom": "Dupont",
    "prenom": "Jean",
    "nomNaissance": "Dupin",
    "email": "jean.dupont@example.com",
    "telephone": "+33612345678",
    "nationalite": {
      "id": 1,
      "libelle": "Française"
    },
    "dateNaissance": "1990-05-15",
    "lieuNaissance": "Paris",
    "situationMatrimoniale": {
      "id": 1,
      "libelle": "Célibataire"
    },
    "adresseMadagascar": "45 Avenue des Champs, Antananarivo, Madagascar",
    "dateCreation": "2024-04-21T10:30:00",
    "dateModification": null
  },
  "dateDemande": "2024-04-21T10:30:00",
  "typeMotif": {
    "id": 1,
    "libelle": "Travail"
  },
  "typeDemande": {
    "id": 1,
    "libelle": "Travailleur"
  },
  "dateTraitement": null,
  "dateExpirationDemande": null,
  "dateModification": null
}
```

---

### 3. Valider une demande

**URL:** `POST /api/demandes/{demandeId}/valider`

**Exemple:** `POST /api/demandes/5/valider`

**Réponse (tous docs présents):**
```json
{
  "success": true,
  "message": "Tous les documents obligatoires sont présents",
  "errors": [],
  "demandeId": null
}
```

**Réponse (docs manquants):**
```json
{
  "success": false,
  "message": "Documents obligatoires manquants",
  "errors": [
    "Le document obligatoire 'Passeport' est manquant",
    "Le document obligatoire 'Photos' est manquant"
  ],
  "demandeId": null
}
```

---

### 4. Modifier une demande existante

**URL:** `PUT /api/demandes/{demandeId}/modifier`

**Exemple:** `PUT /api/demandes/5/modifier`

**Body (JSON):** (même structure que création)
```json
{
  "nom": "Dupont",
  "email": "jean.dupont@gmail.com",
  "piecesPresentes": [1, 2, 3, 4, 5]
}
```

**Réponse:**
```json
{
  "success": true,
  "message": "Demande modifiée avec succès",
  "errors": [],
  "demandeId": 5
}
```

---

### 5. Récupérer le statut courant

**URL:** `GET /api/demandes/{demandeId}/statut`

**Exemple:** `GET /api/demandes/5/statut`

**Réponse:**
```json
{
  "id": 1,
  "libelle": "dossier cree"
}
```

---

### 6. Récupérer les documents manquants

**URL:** `GET /api/demandes/{demandeId}/documents-manquants`

**Exemple:** `GET /api/demandes/5/documents-manquants`

**Réponse (docs manquants):**
```json
[
  "Passeport",
  "Contrat de travail",
  "Attestation de l'autorisation d'emploi"
]
```

**Réponse (aucun manquant):**
```json
[]
```

---

### 7. Changer le statut de la demande

**URL:** `PUT /api/demandes/{demandeId}/changer-statut`

**Exemple:** `PUT /api/demandes/5/changer-statut`

**Body (JSON):**
```json
{
  "statut": "dossier_complet"
}
```

**Réponse (succès):**
```json
{
  "success": true,
  "message": "Statut changé avec succès",
  "errors": [],
  "demandeId": null
}
```

**Réponse (docs obligatoires manquants):**
```json
{
  "success": false,
  "message": "Impossible de changer le statut: documents obligatoires manquants",
  "errors": [],
  "demandeId": null
}
```

---

## Validation des Champs

### Champs Obligatoires (Demandeur):
- ✓ `nom` - Texte non vide
- ✓ `telephone` - Format valide (avec regex)
- ✓ `adresseMadagascar` - Texte non vide
- ✓ `dateNaissance` - Date valide
- Optional: `email` - Doit être au format email si fourni

### Documents Obligatoires:
Dépendent du `idTypeMotif` et `idTypeDemande`:
- Passeport (obligatoire)
- Formulaire (obligatoire)
- Photos (obligatoire)
- Billet d'avion (obligatoire)
- Documents spécifiques selon le type

---

## Exemples complets avec cURL

### Créer une demande:
```bash
curl -X POST http://localhost:8080/api/demandes/creer \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Dupont",
    "prenom": "Jean",
    "email": "jean@example.com",
    "telephone": "+33612345678",
    "dateNaissance": "1990-05-15",
    "adresseMadagascar": "Antananarivo",
    "idNationalite": 1,
    "piecesPresentes": [1, 2, 3]
  }'
```

### Récupérer une demande:
```bash
curl -X GET http://localhost:8080/api/demandes/5
```

### Valider une demande:
```bash
curl -X POST http://localhost:8080/api/demandes/5/valider
```

### Voir les docs manquants:
```bash
curl -X GET http://localhost:8080/api/demandes/5/documents-manquants
```

### Changer le statut:
```bash
curl -X PUT http://localhost:8080/api/demandes/5/changer-statut \
  -H "Content-Type: application/json" \
  -d '{"statut": "dossier_complet"}'
```

---

## Statuts disponibles

| Statut | Description |
|--------|-------------|
| `dossier cree` | Dossier créé mais incomplet |
| `dossier complet` | Tous les documents obligatoires présents |
| `en_traitement` | Le dossier est en cours de traitement |
| `approuve` | Le dossier a été approuvé |
| `rejete` | Le dossier a été rejeté |

---

## Codes d'Erreur HTTP

| Code | Signification |
|------|---------------|
| `200 OK` | Requête réussie |
| `201 Created` | Ressource créée avec succès |
| `400 Bad Request` | Erreur de validation ou données invalides |
| `404 Not Found` | Ressource non trouvée |
| `500 Internal Server Error` | Erreur du serveur |

---

## Notes importantes

1. **Validation côté serveur**: Toutes les données sont validées côté serveur
2. **Transactions**: Les opérations sont transactionnelles (tout ou rien)
3. **Historique**: Chaque changement de statut est enregistré
4. **Modification autorisée**: Même avec dossier incomplet
5. **Passage statut bloqué**: Si docs obligatoires manquants
6. **Dates**: Format ISO 8601 (YYYY-MM-DD)

---

## Format des Erreurs

Toutes les erreurs retournent une réponse `ValidationErrorDTO` avec:
- `success`: boolean (toujours false en cas d'erreur)
- `message`: String (message général)
- `errors`: List<String> (liste des erreurs spécifiques)
- `demandeId`: Integer (ID de la demande si applicable, null sinon)

---

## Interface Web

### Formulaire de création:
Accédez à: `GET /formulaire_demande.jsp`

### Détail de la demande:
Accédez à: `GET /detail_demande.jsp?demandeId={id}`
