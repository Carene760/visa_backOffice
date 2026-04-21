# DEV2 - Validation de Demande et Gestion du Statut

## Vue d'ensemble

Cette implémentation couvre la **Branche 2** du Sprint 1 pour les tâches **dev2** du projet de visa transformable en long séjour. Elle inclut:

1. **Controller** - Gestion des requêtes HTTP et des soumissions de formulaire
2. **Services** - Logique métier pour la validation et le statut
3. **Repositories** - Accès aux données
4. **DTOs** - Transfert de données
5. **JSP** - Interface utilisateur

---

## Architecture et Composants

### 1. Controllers

#### `DemandeController.java`
Route base: `/api/demandes`

**Endpoints:**
- `POST /api/demandes/creer` - Crée une nouvelle demande
- `PUT /api/demandes/{demandeId}/modifier` - Modifie une demande existante
- `GET /api/demandes/{demandeId}` - Récupère les détails d'une demande
- `POST /api/demandes/{demandeId}/valider` - Valide une demande
- `PUT /api/demandes/{demandeId}/changer-statut` - Change le statut
- `GET /api/demandes/{demandeId}/documents-manquants` - Liste les docs manquants
- `GET /api/demandes/{demandeId}/statut` - Récupère le statut courant

### 2. Services

#### `DemandeValidationService.java`
Gère la validation des données:
- `validerDemandeur()` - Valide les champs obligatoires du demandeur
- `validerDocumentsObligatoires()` - Vérifie la présence des docs obligatoires
- `validerEnregistrement()` - Valide l'enregistrement complet
- `toutsDocumentsObligatoiresPresents()` - Vérifie l'intégrité des docs
- `compterDocumentsObligatoiresPresents()` - Compte les docs obligatoires

#### `DemandeStatusService.java`
Gère le cycle de vie et le statut des demandes:
- `initializeDemandeStatus()` - Initialise avec le statut "dossier cree"
- `peutChangerStatut()` - Vérifie si le passage à un autre statut est possible
- `changerStatut()` - Change le statut si les conditions sont remplies
- `getStatutCourant()` - Récupère le statut actuel
- `isDossierComplet()` - Vérifie si tous les docs obligatoires sont présents
- `getDocumentsManquants()` - Liste les docs obligatoires manquants

#### `DemandeService.java`
Service principal pour la gestion des demandes:
- `creerNouvelleDemande()` - Crée une nouvelle demande avec validation
- `modifierDemande()` - Modifie une demande existante
- `getDemandeById()` - Récupère une demande par ID
- `peutPasserAuStatutSuivant()` - Vérifie si passage au statut suivant possible

### 3. Repositories

- `DemandeRepository.java` - Gestion des demandes
- `DemandeurRepository.java` - Gestion des demandeurs
- `PieceAFournirRepository.java` - Gestion des documents
- `StatutDemandeRepository.java` - Gestion des statuts
- `HistoriqueStatutDemandeRepository.java` - Suivi historique des changements

### 4. DTOs

#### `DemandeCreationDTO.java`
Structure pour la création/modification de demande:
```java
// Demandeur
String nom, prenom, nomNaissance, email, telephone
Integer idNationalite, idSituationMatrimoniale
LocalDate dateNaissance
String lieuNaissance, adresseMadagascar

// Passeport
String numeroPasseport
LocalDate dateDelivrancePas seport, dateExpirationPas seport

// Visa
String referenceVisa, lieuEntreeVisa
LocalDate dateEntreeVisa, dateExpirationVisa

// Demande
Integer idTypeDemande, idTypeMotif
List<Integer> piecesPresentes
```

#### `ValidationErrorDTO.java`
Réponse standardisée pour la validation:
```java
boolean success
String message
List<String> errors
Integer demandeId
```

### 5. Exceptions

#### `ValidationException.java`
Exception customisée pour les erreurs de validation

### 6. JSP/Interface

#### `formulaire_demande.jsp`
Formulaire complet pour créer une nouvelle demande:
- 7 sections avec validations côté client
- Responsive design
- Gestion des erreurs avec affichage détaillé
- Redirection automatique après succès

#### `detail_demande.jsp`
Page de détail pour afficher une demande:
- Affichage des infos du demandeur
- Statut courant et alerte sur les docs manquants
- Liste des documents manquants
- Actions disponibles (modifier, télécharger)

---

## Logique de Validation

### Champs Obligatoires du Demandeur:
- ✓ Nom
- ✓ Numéro de téléphone (format validé)
- ✓ Adresse à Madagascar
- ✓ Date de naissance
- ✓ Email (format validé s'il est fourni)

### Documents Obligatoires:
Selon le type de demande (Travailleur/Investisseur):
- Passeport valide (6+ mois)
- Formulaire
- Deux photos (4.5cm x 3.5cm)
- Billet d'avion
- Documents spécifiques selon le motif

### Règles de Statut:
1. **Création**: Statut initial = "dossier cree"
2. **Modification**: Peut être modifiée sans documents complets
3. **Progression**: Passage au statut suivant BLOQUÉ si documents obligatoires manquants
4. **Historique**: Chaque changement enregistré dans `historique_statut_demande`

---

## Flux de Travail

### Création de Demande:
```
1. Utilisateur remplit le formulaire
2. Validation côté client
3. Soumission POST /api/demandes/creer
4. DemandeService.creerNouvelleDemande()
   ├─ Valider demandeur
   ├─ Sauvegarder demandeur
   ├─ Créer demande
   ├─ Initialiser statut "dossier cree"
   └─ Créer pièces à fournir
5. Réponse ValidationErrorDTO
6. Redirection vers détail si succès
```

### Modification de Demande:
```
1. Utilisateur modifie et soumet
2. PUT /api/demandes/{id}/modifier
3. DemandeService.modifierDemande()
   ├─ Vérifier existence demande
   ├─ Mettre à jour demandeur
   ├─ Supprimer anciennes pièces
   └─ Créer nouvelles pièces
4. Réponse de confirmation
```

### Changement de Statut:
```
1. PUT /api/demandes/{id}/changer-statut
2. DemandeStatusService.changerStatut()
   ├─ Vérifier peutChangerStatut()
   │  └─ Tous les docs obligatoires présents?
   ├─ SI OUI: Créer historique
   └─ SI NON: Retourner erreur
```

---

## Intégration Base de Données

### Schéma SQL requis:

Les tables doivent exister:
- `demande` (id_demandeur, date_demande, id_type_motif, id_type_demande, etc.)
- `demandeur` (id, nom, prenom, email, telephone, etc.)
- `piece_a_fournir` (id_demande, id_type_document, present, etc.)
- `statut_demande` (id, libelle)
- `historique_statut_demande` (id_demande, id_statut, date_changement)
- `type_document` (id, libelle, obligatoire)

---

## Utilisation

### Créer une demande:
```bash
POST /api/demandes/creer
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean@example.com",
  "telephone": "+33612345678",
  "dateNaissance": "1990-05-15",
  "adresseMadagascar": "Antananarivo",
  "idNationalite": 1,
  "piecesPresentes": [1, 2, 3]
}
```

### Récupérer détails:
```bash
GET /api/demandes/1
```

### Vérifier documents manquants:
```bash
GET /api/demandes/1/documents-manquants
```

### Changer statut:
```bash
PUT /api/demandes/1/changer-statut
Content-Type: application/json

{
  "statut": "dossier_complet"
}
```

---

## Points Importants

✓ **Transactions**: Utilisation de `@Transactional` pour l'intégrité
✓ **Validation**: Validation côté serveur systématique
✓ **Erreurs claires**: Messages d'erreur détaillés pour l'utilisateur
✓ **Historique**: Tous les changements de statut enregistrés
✓ **Modification**: Permet la modification même avec dossier incomplet
✓ **Statut initial**: Systématiquement "dossier cree"
✓ **Documents optionnels**: Enregistrement autorisé même sans docs non-obligatoires

---

## Prochaines Étapes (Dev1)

Ces tâches dev2 dépendent de dev1:
- ✗ Formulaire JSP (formulaire HTML + JavaScript)
- ✗ Script SQL d'initialisation
- ✗ Configuration application.properties

---

## Notes

- L'implémentation est prête pour l'intégration avec dev1
- Les endpoints sont protégés par validation du serveur
- Les réponses utilisent JSON
- Pas d'upload de fichiers dans ce sprint
- L'historique des statuts est automatiquement maintenu
