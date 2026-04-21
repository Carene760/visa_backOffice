# Quick Start Guide - DEV2

## 🚀 Démarrage Rapide

### Prérequis
- ✓ Java 17+
- ✓ Maven 3.6+
- ✓ MySQL/PostgreSQL
- ✓ Git

---

## Step 1: Cloner et Configurer

```bash
# Naviguer vers le projet
cd e:\GitHub\visa_backOffice

# Vérifier que les fichiers existent
dir src/main/java/com/teamlead/Controller/
dir src/main/java/com/teamlead/Service/
dir src/main/java/com/teamlead/Repository/
```

---

## Step 2: Configurer la Base de Données

### application.properties
```properties
# Server
server.port=8080

# Database (adapter à votre configuration)
spring.datasource.url=jdbc:mysql://localhost:3306/visa_db
spring.datasource.username=root
spring.datasource.password=votre_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# View
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

### Créer la Base de Données

```sql
-- Terminal MySQL
mysql -u root -p

CREATE DATABASE visa_db;
USE visa_db;

-- Exécuter init.sql de DEV1
SOURCE scripts/migration/sprint1/init.sql;
```

---

## Step 3: Compiler et Lancer

```bash
# Nettoyer et compiler
mvn clean install

# Lancer l'application
mvn spring-boot:run

# Ou avec Maven wrapper
./mvnw spring-boot:run
```

L'application sera accessible à: **http://localhost:8080**

---

## Step 4: Tester les APIs

### 4.1 Créer une Demande

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

**Réponse Attendue**:
```json
{
  "success": true,
  "message": "Demande créée avec succès",
  "demandeId": 1
}
```

### 4.2 Récupérer une Demande

```bash
curl -X GET http://localhost:8080/api/demandes/1
```

### 4.3 Voir les Documents Manquants

```bash
curl -X GET http://localhost:8080/api/demandes/1/documents-manquants
```

### 4.4 Voir le Statut

```bash
curl -X GET http://localhost:8080/api/demandes/1/statut
```

---

## Step 5: Accéder à l'Interface Web

### Formulaire de Création

```
http://localhost:8080/formulaire_demande.jsp
```

### Détail d'une Demande

```
http://localhost:8080/detail_demande.jsp
```

---

## Structure Fichiers Créés

### Controllers (1)
- `src/main/java/com/teamlead/Controller/DemandeController.java`

### Services (3)
- `src/main/java/com/teamlead/Service/DemandeService.java`
- `src/main/java/com/teamlead/Service/DemandeValidationService.java`
- `src/main/java/com/teamlead/Service/DemandeStatusService.java`

### Repositories (10)
- `src/main/java/com/teamlead/Repository/*.java`

### DTOs (2)
- `src/main/java/com/teamlead/DTO/DemandeCreationDTO.java`
- `src/main/java/com/teamlead/DTO/ValidationErrorDTO.java`

### JSP (2)
- `src/main/webapp/WEB-INF/jsp/formulaire_demande.jsp`
- `src/main/webapp/WEB-INF/jsp/detail_demande.jsp`

---

## Endpoints Principaux

| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/demandes/creer` | Créer demande |
| GET | `/api/demandes/{id}` | Détails demande |
| PUT | `/api/demandes/{id}/modifier` | Modifier demande |
| GET | `/api/demandes/{id}/statut` | Statut courant |
| GET | `/api/demandes/{id}/documents-manquants` | Docs manquants |

---

## Troubleshooting

### Erreur: "Table doesn't exist"
```
Solution: Exécuter init.sql de DEV1
mysql < scripts/migration/sprint1/init.sql
```

### Erreur: "Connection refused"
```
Solution: Vérifier que MySQL est en cours d'exécution
mysql -u root -p
```

### Erreur: "Build failed"
```
Solution: Nettoyer et recompiler
mvn clean install -X
```

### Erreur: "Port 8080 already in use"
```
Solution: Changer le port dans application.properties
server.port=8081
```

---

## Commandes Utiles

```bash
# Compiler sans lancer
mvn clean compile

# Exécuter les tests
mvn test

# Générer WAR pour déploiement
mvn clean package

# Générer la documentation
mvn javadoc:javadoc

# Vérifier les dépendances
mvn dependency:tree
```

---

## Validation de l'Installation

- [ ] Application démarre sans erreur
- [ ] Endpoints répondent (HTTP 200)
- [ ] Création de demande fonctionne
- [ ] Récupération de demande fonctionne
- [ ] Formulaire JSP s'affiche
- [ ] Validation des champs fonctionne
- [ ] Documents manquants s'affichent

---

## Documentation de Référence

1. **DEV2_IMPLEMENTATION.md** - Architecture complète
2. **API_USAGE_GUIDE.md** - Guide d'utilisation des APIs
3. **ENDPOINTS_REFERENCE.md** - Référence des endpoints
4. **ARCHITECTURE_FILES.md** - Structure des fichiers

---

## Prochaines Étapes

### Phase 1 (Sprint 1):
- [ ] Intégrer avec formulaire JSP de DEV1
- [ ] Tester avec données réelles
- [ ] Valider la logique métier
- [ ] Déployer en développement

### Phase 2 (Sprint 2+):
- [ ] Ajouter upload de fichiers
- [ ] Ajouter authentification
- [ ] Ajouter notifications email
- [ ] Ajouter dashboard admin

---

## Support

En cas de problème:

1. Consulter les logs: `tail -f application.log`
2. Vérifier application.properties
3. Vérifier la configuration database
4. Consulter DEV2_IMPLEMENTATION.md
5. Consulter API_USAGE_GUIDE.md

---

## Statut

✅ **DEV2 - 100% Implémenté et Prêt à Tester**

Tous les fichiers sont créés et documentés.
Prêt pour l'intégration avec DEV1.

---

**Date**: 21 Avril 2024
**Version**: 1.0
**Status**: ✅ Ready to Deploy
