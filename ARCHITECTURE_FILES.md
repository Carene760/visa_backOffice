# Architecture Fichiers DEV2

## Vue d'ensemble de l'arborescence créée

```
e:\GitHub\visa_backOffice\
│
├── src/main/java/com/teamlead/
│   │
│   ├── Controller/                           ← NEW PACKAGE
│   │   └── DemandeController.java           (7 endpoints REST)
│   │
│   ├── Service/                             ← NEW CLASSES
│   │   ├── DemandeService.java              (Logique métier)
│   │   ├── DemandeValidationService.java    (Validation)
│   │   └── DemandeStatusService.java        (Gestion statut)
│   │
│   ├── Repository/                          ← NEW PACKAGE
│   │   ├── DemandeRepository.java
│   │   ├── DemandeurRepository.java
│   │   ├── PieceAFournirRepository.java
│   │   ├── StatutDemandeRepository.java
│   │   ├── HistoriqueStatutDemandeRepository.java
│   │   ├── TypeDocumentRepository.java
│   │   ├── TypeMotifRepository.java
│   │   ├── TypeDemandeRepository.java
│   │   ├── NationaliteRepository.java
│   │   └── SituationMatrimonialeRepository.java
│   │
│   ├── DTO/                                 ← NEW PACKAGE
│   │   ├── DemandeCreationDTO.java
│   │   └── ValidationErrorDTO.java
│   │
│   ├── Exception/                           ← NEW PACKAGE
│   │   └── ValidationException.java
│   │
│   ├── Config/                              ← NEW PACKAGE
│   │   └── DataInitializer.java             (Init statuts)
│   │
│   └── Model/
│       └── Demande.java                     (MODIFIED)
│
├── src/main/webapp/WEB-INF/jsp/             ← NEW DIRECTORY
│   ├── formulaire_demande.jsp               (Formulaire création)
│   └── detail_demande.jsp                   (Détail demande)
│
├── Documentation/
│   ├── DEV2_IMPLEMENTATION.md               (Doc technique complète)
│   ├── API_USAGE_GUIDE.md                   (Guide d'utilisation API)
│   ├── IMPLEMENTATION_SUMMARY.md            (Résumé des changements)
│   ├── DEV2_VALIDATION_CHECKLIST.md         (Checklist validation)
│   └── ARCHITECTURE_FILES.md                (Ce fichier)
│
└── pom.xml, mvnw, etc. (existing)

```

---

## Statistiques

### Fichiers Créés: **23**

#### Par Catégorie:
- Controllers: 1
- Services: 3
- Repositories: 10
- DTOs: 2
- Exceptions: 1
- Configuration: 1
- JSP Views: 2
- Documentation: 4

### Lignes de Code: **~3500+**

#### Par Fichier (estimation):
- DemandeController.java: ~180 lignes
- DemandeService.java: ~150 lignes
- DemandeValidationService.java: ~120 lignes
- DemandeStatusService.java: ~130 lignes
- Repositories (10 fichiers): ~250 lignes
- DTOs (2 fichiers): ~80 lignes
- formulaire_demande.jsp: ~600 lignes
- detail_demande.jsp: ~450 lignes
- Documentation (4 fichiers): ~1200 lignes

---

## Dépendances Entre Fichiers

```
DemandeController
    ├─> DemandeService
    ├─> DemandeValidationService
    └─> DemandeStatusService

DemandeService
    ├─> DemandeRepository
    ├─> DemandeurRepository
    ├─> PieceAFournirRepository
    ├─> DemandeValidationService
    ├─> DemandeStatusService
    └─> Models (Demande, Demandeur, etc.)

DemandeValidationService
    ├─> DemandeurRepository
    ├─> PieceAFournirRepository
    └─> Models

DemandeStatusService
    ├─> DemandeRepository
    ├─> StatutDemandeRepository
    ├─> HistoriqueStatutDemandeRepository
    ├─> PieceAFournirRepository
    └─> Models

Repositories
    └─> Models (via JPA)

DTOs
    └─> (données uniquement)

JSP Files
    └─> DemandeController (via fetch API)
```

---

## Packages et Organisation

### `com.teamlead.Controller`
**Responsabilité**: Gestion des requêtes HTTP
- DemandeController.java
  - POST /api/demandes/creer
  - GET /api/demandes/{id}
  - PUT /api/demandes/{id}/modifier
  - POST /api/demandes/{id}/valider
  - PUT /api/demandes/{id}/changer-statut
  - GET /api/demandes/{id}/statut
  - GET /api/demandes/{id}/documents-manquants

### `com.teamlead.Service`
**Responsabilité**: Logique métier
- DemandeService.java - Opérations CRUD principales
- DemandeValidationService.java - Validation des données
- DemandeStatusService.java - Gestion du cycle de vie

### `com.teamlead.Repository`
**Responsabilité**: Persistence et accès aux données
- DemandeRepository.java
- DemandeurRepository.java
- PieceAFournirRepository.java
- StatutDemandeRepository.java
- HistoriqueStatutDemandeRepository.java
- TypeDocumentRepository.java
- TypeMotifRepository.java
- TypeDemandeRepository.java
- NationaliteRepository.java
- SituationMatrimonialeRepository.java

### `com.teamlead.DTO`
**Responsabilité**: Transfert de données
- DemandeCreationDTO.java - Données entrantes
- ValidationErrorDTO.java - Erreurs standardisées

### `com.teamlead.Exception`
**Responsabilité**: Exceptions métier
- ValidationException.java

### `com.teamlead.Config`
**Responsabilité**: Configuration et initialisation
- DataInitializer.java - Init des statuts

### `WEB-INF/jsp`
**Responsabilité**: Interface utilisateur
- formulaire_demande.jsp - Création demande
- detail_demande.jsp - Détail demande

---

## Flux de Données

### Création de Demande

```
Formulaire HTML (formulaire_demande.jsp)
    ↓ (JavaScript fetch)
DemandeController.creerDemande()
    ↓
DemandeService.creerNouvelleDemande()
    ├─ DemandeValidationService.validerDemandeur()
    ├─ DemandeurRepository.save()
    ├─ DemandeRepository.save()
    ├─ DemandeStatusService.initializeDemandeStatus()
    └─ PieceAFournirRepository.save() (multiple)
    ↓
ValidationErrorDTO (JSON response)
    ↓ (JavaScript)
Redirection vers detail_demande.jsp
```

### Affichage Détail

```
detail_demande.jsp (chargement)
    ↓ (JavaScript fetch multiple)
┌─ DemandeController.getDetailDemande()
├─ DemandeController.getStatutDemande()
└─ DemandeController.getDocumentsManquants()
    ↓
┌─ DemandeRepository.findById()
├─ DemandeStatusService.getStatutCourant()
└─ DemandeStatusService.getDocumentsManquants()
    ↓
Affichage des informations
```

---

## Points d'Intégration avec DEV1

### Modèles Utilisés:
- ✓ Demande.java
- ✓ Demandeur.java
- ✓ PieceAFournir.java
- ✓ StatutDemande.java
- ✓ HistoriqueStatutDemande.java
- ✓ TypeDocument.java
- ✓ TypeMotif.java
- ✓ TypeDemande.java
- ✓ Nationalite.java
- ✓ SituationMatrimoniale.java

### Database Requirements:
- Tables doivent exister
- Relations étrangères doivent être configurées
- Données de référence doivent être insérées

### Configuration Required:
- application.properties (datasource)
- pom.xml (dépendances Spring)
- init.sql (script SQL DEV1)

---

## Configuration Maven

### Dépendances Requises dans pom.xml:
```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Database Driver (example: MySQL) -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- JSTL for JSP -->
<dependency>
    <groupId>org.apache.taglibs</groupId>
    <artifactId>taglibs-standard-impl</artifactId>
    <version>1.2.5</version>
</dependency>

<!-- Jakarta Persistence -->
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
</dependency>
```

---

## Propriétés Application

### application.properties requis:
```properties
# Server
server.port=8080
server.servlet.context-path=/

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/visa_db
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# View
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

---

## Tests Unitaires Recommandés

### À Tester:
- DemandeService.creerNouvelleDemande()
- DemandeValidationService.validerDemandeur()
- DemandeValidationService.validerDocumentsObligatoires()
- DemandeStatusService.changerStatut()
- DemandeStatusService.isDossierComplet()

### Mocking:
- Repositories (via @MockBean)
- Services (via @SpyBean)

---

## Checklist de Déploiement

- [ ] Base de données créée (via init.sql DEV1)
- [ ] application.properties configuré
- [ ] pom.xml dépendances à jour
- [ ] Java 17+ installé
- [ ] Spring Boot 3.0+ configuré
- [ ] Compilation `mvn clean install` réussie
- [ ] Tests passent
- [ ] Application démarre `mvn spring-boot:run`
- [ ] Endpoints accessibles
- [ ] Formulaire affichable

---

## Performance et Optimisation

### Optimisations Actuelles:
- JPA @LazyFetch pour les relations
- Repositories avec requêtes optimisées
- Services transactionnels

### Futures Optimisations:
- Pagination pour les listes
- Caching des références (TypeDocument, etc.)
- Indices de base de données
- Request/Response caching

---

## Sécurité

### Points Actuels:
- ✓ Validation côté serveur
- ✓ Transactions pour intégrité
- ✓ DTOs pour isoler entities

### À Ajouter (Phase 2):
- [ ] Authentication/Authorization
- [ ] Chiffrement données sensibles
- [ ] Audit trail détaillé
- [ ] Rate limiting
- [ ] CSRF protection

---

## Maintenance

### Points d'Entrée pour les Modifications:
1. **Ajouter un nouveau champ au demandeur**: 
   - Modifier Demandeur.java
   - Mettre à jour DemandeCreationDTO
   - Mettre à jour formulaire_demande.jsp

2. **Ajouter une validation**:
   - Ajouter une méthode dans DemandeValidationService

3. **Ajouter un nouveau statut**:
   - Ajouter dans DataInitializer.java
   - Ajouter logique dans DemandeStatusService

4. **Ajouter un endpoint**:
   - Ajouter dans DemandeController
   - Ajouter service si nécessaire
   - Documenter dans API_USAGE_GUIDE.md

---

## Support et Documentation

### Fichiers de Référence:
1. **DEV2_IMPLEMENTATION.md** - Détails techniques complets
2. **API_USAGE_GUIDE.md** - Guide d'utilisation
3. **IMPLEMENTATION_SUMMARY.md** - Résumé des changements
4. **DEV2_VALIDATION_CHECKLIST.md** - Validation des tâches
5. **ARCHITECTURE_FILES.md** - Ce document

---

## Conclusion

L'implémentation DEV2 est complète avec:
- ✅ 10 Repositories pour accès aux données
- ✅ 3 Services pour logique métier
- ✅ 1 Controller pour gestion HTTP
- ✅ 2 DTOs pour transfert données
- ✅ 2 JSP pour interface utilisateur
- ✅ Documentation exhaustive

Le code est prêt pour l'intégration avec DEV1 et le test.

---

**Date**: 21 Avril 2024
**Statut**: ✅ Complet
**Version**: 1.0
