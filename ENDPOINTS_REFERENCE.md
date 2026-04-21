# Endpoints REST DEV2 - Référence Complète

## Base URL
```
http://localhost:8080/api/demandes
```

---

## Endpoints

### 1️⃣ POST /creer
**Créer une nouvelle demande**

**URL Complète**: `POST http://localhost:8080/api/demandes/creer`

**Headers**:
```
Content-Type: application/json
```

**Body (Exemple)**:
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

**Réponse Succès (201 Created)**:
```json
{
  "success": true,
  "message": "Demande créée avec succès",
  "errors": [],
  "demandeId": 5
}
```

**Réponse Erreur (400 Bad Request)**:
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

**Statuts HTTP**:
- `201` - Créé avec succès
- `400` - Erreur de validation
- `500` - Erreur serveur

---

### 2️⃣ GET /{demandeId}
**Récupérer les détails d'une demande**

**URL Complète**: `GET http://localhost:8080/api/demandes/5`

**Parameters**:
- `demandeId` (path) - ID de la demande

**Réponse (200 OK)**:
```json
{
  "id": 5,
  "demandeur": {
    "id": 3,
    "nom": "Dupont",
    "prenom": "Jean",
    "email": "jean.dupont@example.com",
    "telephone": "+33612345678",
    "dateNaissance": "1990-05-15",
    "adresseMadagascar": "45 Avenue des Champs, Antananarivo, Madagascar",
    "dateCreation": "2024-04-21T10:30:00"
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
  "dateExpirationDemande": null
}
```

**Réponse Erreur (404 Not Found)**:
```json
{
  "success": false,
  "message": "Demande introuvable",
  "errors": [],
  "demandeId": null
}
```

---

### 3️⃣ PUT /{demandeId}/modifier
**Modifier une demande existante**

**URL Complète**: `PUT http://localhost:8080/api/demandes/5/modifier`

**Parameters**:
- `demandeId` (path) - ID de la demande

**Headers**:
```
Content-Type: application/json
```

**Body** (même structure que création, fields optionnels):
```json
{
  "nom": "Dupont",
  "prenom": "Jean-Pierre",
  "email": "jean.pierre@example.com",
  "telephone": "+33612345679",
  "piecesPresentes": [1, 2, 3, 4, 5, 6]
}
```

**Réponse Succès (200 OK)**:
```json
{
  "success": true,
  "message": "Demande modifiée avec succès",
  "errors": [],
  "demandeId": 5
}
```

---

### 4️⃣ POST /{demandeId}/valider
**Valider une demande (vérifier documents obligatoires)**

**URL Complète**: `POST http://localhost:8080/api/demandes/5/valider`

**Parameters**:
- `demandeId` (path) - ID de la demande

**Body**: (vide)

**Réponse Succès (200 OK)**:
```json
{
  "success": true,
  "message": "Tous les documents obligatoires sont présents",
  "errors": [],
  "demandeId": null
}
```

**Réponse Erreur (400 Bad Request)**:
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

### 5️⃣ PUT /{demandeId}/changer-statut
**Changer le statut de la demande**

**URL Complète**: `PUT http://localhost:8080/api/demandes/5/changer-statut`

**Parameters**:
- `demandeId` (path) - ID de la demande

**Headers**:
```
Content-Type: application/json
```

**Body**:
```json
{
  "statut": "dossier_complet"
}
```

**Réponse Succès (200 OK)**:
```json
{
  "success": true,
  "message": "Statut changé avec succès",
  "errors": [],
  "demandeId": null
}
```

**Réponse Erreur (400 Bad Request)**:
```json
{
  "success": false,
  "message": "Impossible de changer le statut: documents obligatoires manquants",
  "errors": [],
  "demandeId": null
}
```

---

### 6️⃣ GET /{demandeId}/statut
**Récupérer le statut courant d'une demande**

**URL Complète**: `GET http://localhost:8080/api/demandes/5/statut`

**Parameters**:
- `demandeId` (path) - ID de la demande

**Réponse (200 OK)**:
```json
{
  "id": 1,
  "libelle": "dossier cree"
}
```

**Réponse Alternative (200 OK)**:
```json
{
  "id": 2,
  "libelle": "dossier complet"
}
```

---

### 7️⃣ GET /{demandeId}/documents-manquants
**Récupérer la liste des documents obligatoires manquants**

**URL Complète**: `GET http://localhost:8080/api/demandes/5/documents-manquants`

**Parameters**:
- `demandeId` (path) - ID de la demande

**Réponse (Documents Manquants) (200 OK)**:
```json
[
  "Passeport",
  "Contrat de travail",
  "Attestation de l'autorisation d'emploi",
  "Extrait du casier judiciaire"
]
```

**Réponse (Aucun Manquant) (200 OK)**:
```json
[]
```

---

## Statuts HTTP Principaux

| Code | Signification | Quand |
|------|---------------|-------|
| 200 | OK | Requête réussie (GET, PUT, POST validation) |
| 201 | Created | Nouvelle ressource créée (POST création) |
| 400 | Bad Request | Données invalides ou erreur validation |
| 404 | Not Found | Ressource introuvable |
| 500 | Server Error | Erreur interne serveur |

---

## Statuts de Demande Valides

| Statut | Code | Signification |
|--------|------|---------------|
| Dossier créé | `dossier cree` | Initial, incomplet |
| Dossier complet | `dossier_complet` | Tous documents présents |
| En traitement | `en_traitement` | Traitement en cours |
| Approuvé | `approuve` | Demande approuvée |
| Rejeté | `rejete` | Demande rejetée |

---

## Exemples avec cURL

### Créer une demande:
```bash
curl -X POST http://localhost:8080/api/demandes/creer \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Dupont",
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

### Voir documents manquants:
```bash
curl -X GET http://localhost:8080/api/demandes/5/documents-manquants
```

### Voir statut:
```bash
curl -X GET http://localhost:8080/api/demandes/5/statut
```

### Changer statut:
```bash
curl -X PUT http://localhost:8080/api/demandes/5/changer-statut \
  -H "Content-Type: application/json" \
  -d '{"statut": "dossier_complet"}'
```

### Modifier une demande:
```bash
curl -X PUT http://localhost:8080/api/demandes/5/modifier \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nouveau@example.com",
    "telephone": "+33612345679"
  }'
```

---

## Exemples avec JavaScript/Fetch

### Créer:
```javascript
const response = await fetch('/api/demandes/creer', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    nom: 'Dupont',
    telephone: '+33612345678',
    dateNaissance: '1990-05-15',
    adresseMadagascar: 'Antananarivo',
    idNationalite: 1,
    piecesPresentes: [1, 2, 3]
  })
});
const data = await response.json();
console.log(data);
```

### Récupérer:
```javascript
const response = await fetch('/api/demandes/5');
const data = await response.json();
console.log(data);
```

### Valider:
```javascript
const response = await fetch('/api/demandes/5/valider', {
  method: 'POST'
});
const data = await response.json();
console.log(data);
```

---

## Validation des Données

### Champs Obligatoires:
- `nom` - Texte non vide
- `telephone` - Format valide
- `dateNaissance` - Date valide (YYYY-MM-DD)
- `adresseMadagascar` - Texte non vide

### Formats Acceptés:
- **Date**: YYYY-MM-DD (ISO 8601)
- **Téléphone**: +33... ou 0... avec chiffres
- **Email**: format email valide

---

## Codes d'Erreur et Messages

### Validation du Demandeur:
```
"Le nom du demandeur est obligatoire"
"Le numéro de téléphone est obligatoire"
"L'adresse à Madagascar est obligatoire"
"La date de naissance est obligatoire"
"Le format du numéro de téléphone est invalide"
"Le format de l'email est invalide"
```

### Validation Documents:
```
"Le document obligatoire 'X' est manquant"
"Documents obligatoires manquants"
```

### Logique Métier:
```
"Demande introuvable"
"Impossible de changer le statut: documents obligatoires manquants"
"Erreur lors de la création de la demande"
```

---

## Response Types

### Success Response:
```json
{
  "success": true,
  "message": "Message de succès",
  "errors": [],
  "demandeId": 5
}
```

### Error Response:
```json
{
  "success": false,
  "message": "Message d'erreur",
  "errors": ["Erreur 1", "Erreur 2"],
  "demandeId": null
}
```

---

## Authentification

**Actuellement**: Aucune (ouvert)

**À Ajouter** (Phase 2):
- JWT tokens
- Role-based access control (RBAC)
- Request authentication headers

---

## Rate Limiting

**Actuellement**: Aucun

**À Ajouter** (Phase 2):
- Rate limiting par IP
- Rate limiting par utilisateur
- Cache-Control headers

---

## CORS

**Configuration Actuelle**: Par défaut Spring Boot

**À Configurer si Nécessaire**:
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
```

---

## Versioning API

**Version Actuelle**: 1.0

**Schéma**:
```
/api/v1/demandes/...
```

**À Implémenter** (Phase 2) si besoin de backward compatibility

---

## Notes Importantes

✓ Tous les endpoints retournent JSON
✓ Les dates sont en format ISO 8601
✓ Les erreurs sont toujours dans ValidationErrorDTO
✓ Les transactions garantissent l'intégrité
✓ Les repositories ont des requêtes optimisées
✓ Les services sont transactionnels
✓ Validation côté serveur systématique

---

**Version**: 1.0
**Date**: 21 Avril 2024
**Status**: ✅ Complete
