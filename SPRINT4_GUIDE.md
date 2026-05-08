# 🚀 Sprint 4 - Suivi de Demande avec QR Code

## 📋 Vue d'ensemble

Le Sprint 4 ajoute:
- **Frontend Vue.js** pour rechercher et afficher les demandes
- **API REST** pour l'accès aux demandes
- **Génération de QR Code** pour chaque demande
- **Suivi publique** via QR Code

---

## 🏗️ Architecture

```
Frontend (Vue.js) ← → Backend (Spring Boot) ← → PostgreSQL
http://localhost:8085/index.html   /api/*
```

---

## 📂 Fichiers créés/modifiés

### Frontend
```
✅ frontend/index.html                    - Application Vue.js complète
```

### Backend
```
✅ src/main/java/com/teamlead/
   ├── Controller/
   │   └── ApiController.java             - Endpoints REST
   ├── Service/
   │   └── DemandeSearchService.java      - Logique de recherche
   ├── Repository/
   │   ├── DemandeRepository.java         - Mis à jour
   │   └── PasseportRepository.java       - Mis à jour
   ├── DTO/
   │   ├── DemandeSearchRequestDTO.java
   │   ├── DemandeDetailDTO.java
   │   ├── DemandeSearchResponseDTO.java
   │   ├── HistoriqueStatutDTO.java
   │   └── QRCodeDTO.java
   └── config/
       └── WebConfig.java                 - Configuration CORS
```

### Configuration
```
✅ src/main/resources/application.properties  - Mis à jour
✅ scripts/migration/sprint4/000_add_qrcode_infrastructure.sql
```

---

## 🚀 Démarrage

### 1. Base de données
```bash
psql -U postgres -d visa -f scripts/migration/sprint4/000_add_qrcode_infrastructure.sql
```

### 2. Compiler & démarrer
```bash
cd e:\GitHub\visa_backOffice
mvn spring-boot:run
```

### 3. Accéder au frontend
```
http://localhost:8085/index.html
```

---

## 📡 API Endpoints

### Recherche (PUBLIC)
```http
POST /api/search
Content-Type: application/json

{
  "searchCriteria": "123"  // Numéro demande ou passeport
}
```

**Réponse:**
```json
{
  "demandeEnEvidence": { ... },
  "autresDemandesRelatives": [ ... ],
  "typeRecherche": "NUMERO_DEMANDE",
  "totalDemandesFound": 5,
  "nombreDemandesApprouvees": 2,
  "nombreDemandesRefusees": 1,
  "nombreDemandesEnCours": 2
}
```

### Générer QR Code
```http
POST /api/demandes/{id}/qrcode
```

### Health Check
```http
GET /api/health
```

---

## 🎨 Fonctionnalités Frontend

### Formulaire unique de recherche
- Entrez numéro demande OU passeport
- Auto-détection du type
- Recherche instantanée avec Enter

### Résultats - Numéro de demande
1. **Résumé** des statistiques
2. **Demande en évidence** (banneau couleur)
   - Infos complètes
   - Historique des statuts (timeline)
   - QR Code (si généré)
3. **Autres demandes** du demandeur

### Résultats - Numéro de passeport
1. **Résumé** des statistiques
2. **Toutes les demandes** du demandeur
   - Pour chaque: infos + historique + QR Code
   - Ordre chronologique décroissant

### Affichage de l'historique
- Timeline chronologique
- Badge coloré par statut
- Date/heure de chaque changement

### QR Code
- Image PNG scannable
- Contient URL de suivi
- Générable/régénérable

---

## 🧪 Test rapide

### Test 1: Recherche par numéro de demande
```
1. http://localhost:8085/index.html
2. Entrer: 1
3. Cliquer Rechercher
4. Vérifier: Demande affichée + historique + autres demandes
```

### Test 2: Recherche par numéro de passeport
```
1. Entrer: AB123456
2. Cliquer Rechercher
3. Vérifier: Toutes les demandes du demandeur listées
```

### Test 3: API via curl
```bash
curl -X POST http://localhost:8085/api/search \
  -H "Content-Type: application/json" \
  -d '{"searchCriteria":"1"}'
```

---

## 📊 Structure des données

### Recherche par numéro de demande
```
Réponse
├── demandeEnEvidence (Demande recherchée)
├── autresDemandesRelatives (Autres demandes du demandeur)
└── Statistiques
```

### Recherche par numéro de passeport
```
Réponse
├── demandeEnEvidence: null
├── autresDemandesRelatives (Toutes les demandes)
└── Statistiques
```

---

## 🔐 Sécurité (Sprint 4)

- CORS ouvert pour dev local (`*`)
- À restreindre en production
- QR Code avec token unique (UUID)
- Endpoint public de suivi sécurisé

---

## 🐛 Troubleshooting

### Erreur "Demande non trouvée"
- Vérifier que le numéro existe en BD
- Vérifier les logs du backend

### CORS Error
- Vérifier WebConfig.java
- Vérifier ApiController @CrossOrigin

### QR Code ne s'affiche pas
- Vérifier les logs backend
- Régénérer le QR Code

### Frontend ne charge pas
- Vérifier http://localhost:8085/index.html
- Vérifier les logs (F12 Console)

---

## 📈 Prochaines étapes

**Sprint 5:**
- Versioning d'API
- Pagination des résultats
- Filtrage avancé
- Authentification pour endpoints sensibles
- Déploiement en production

---

**Sprint 4 ✅**
