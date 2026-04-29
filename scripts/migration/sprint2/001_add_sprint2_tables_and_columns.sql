-- Sprint 2 Migration: Ajouter tables et colonnes pour gestion des demandes duplicata/transfert

-- =====================================================
-- 1. Créer table sous_type_demande
-- =====================================================
CREATE TABLE IF NOT EXISTS sous_type_demande (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);

-- =====================================================
-- 2. Créer table recherche_antecendent
-- =====================================================
CREATE TABLE IF NOT EXISTS recherche_antecendent (
    id SERIAL PRIMARY KEY,
    id_demande INT NOT NULL,
    critere_recherche VARCHAR(50) NOT NULL,
    valeur_recherchee VARCHAR(100) NOT NULL,
    trouve BOOLEAN DEFAULT FALSE,
    date_recherche TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE,
    CONSTRAINT critere_search_check CHECK (critere_recherche IN ('NUMERO_VISA', 'PASSEPORT', 'CARTE'))
);

-- =====================================================
-- 3. Créer table carte_resident
-- =====================================================
CREATE TABLE IF NOT EXISTS carte_resident (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(100) NOT NULL UNIQUE,
    date_entree DATE NOT NULL,
    lieu_entree VARCHAR(150),
    date_expiration DATE,
    id_demande INT NOT NULL,
    id_passeport INT NOT NULL,
    date_emission TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE,
    FOREIGN KEY (id_passeport) REFERENCES passeport(id) ON DELETE CASCADE,
    CHECK (date_expiration >= date_entree)
);

-- =====================================================
-- 4. Créer table decision_document
-- =====================================================
CREATE TABLE IF NOT EXISTS decision_document (
    id SERIAL PRIMARY KEY,
    id_demande INT NOT NULL,
    type_decision VARCHAR(50) NOT NULL,
    criteres_appliques TEXT,
    decision_automatique BOOLEAN DEFAULT FALSE,
    date_decision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    raison_rejet VARCHAR(500),
    FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE,
    CONSTRAINT type_decision_check CHECK (type_decision IN ('AUTO_APPROVAL_DUPLICATA', 'MANUAL_REVIEW', 'REFUSED'))
);

-- =====================================================
-- 5. Ajouter colonnes à table demande
-- =====================================================
ALTER TABLE demande ADD COLUMN IF NOT EXISTS id_demande_source INT;
ALTER TABLE demande ADD COLUMN IF NOT EXISTS id_sous_type_demande INT;
ALTER TABLE demande ADD COLUMN IF NOT EXISTS avec_donnees_anterieures BOOLEAN DEFAULT FALSE;
ALTER TABLE demande ADD COLUMN IF NOT EXISTS est_base_generee BOOLEAN DEFAULT FALSE;

-- Ajouter contraintes de clés étrangères pour les nouvelles colonnes
ALTER TABLE demande ADD CONSTRAINT IF NOT EXISTS fk_demande_source 
    FOREIGN KEY (id_demande_source) REFERENCES demande(id) ON DELETE SET NULL;

ALTER TABLE demande ADD CONSTRAINT IF NOT EXISTS fk_sous_type_demande
    FOREIGN KEY (id_sous_type_demande) REFERENCES sous_type_demande(id) ON DELETE SET NULL;

-- =====================================================
-- 6. Insérer les sous-types de demande
-- =====================================================
INSERT INTO sous_type_demande (libelle) VALUES 
    ('TRANSFERT_VISA'),
    ('DUPLICATA_CARTE'),
    ('LES_DEUX')
ON CONFLICT (libelle) DO NOTHING;

-- =====================================================
-- 7. Ajouter les nouveaux statuts de demande
-- =====================================================
INSERT INTO statut_demande (libelle) VALUES 
    ('SCAN_TERMINE'),
    ('VISA_APPROUVEE'),
    ('REFUSE')
ON CONFLICT (libelle) DO NOTHING;

-- =====================================================
-- 8. Ajouter les nouveaux types de demande
-- =====================================================
INSERT INTO type_demande (libelle) VALUES 
    ('NOUVEAU_TITRE'),
    ('DUPLICATA')
ON CONFLICT (libelle) DO NOTHING;

-- =====================================================
-- 9. Ajouter les nouveaux types d'événement
-- =====================================================
INSERT INTO type_evenement (code) VALUES 
    ('RECHERCHE_ANTECEDENTS'),
    ('AUTO_DECISION_SANS_ANTERIEURS'),
    ('CREATION_DEMANDE_BASE')
ON CONFLICT (code) DO NOTHING;

-- =====================================================
-- 10. Créer index pour améliorer les performances
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_demande_source ON demande(id_demande_source);
CREATE INDEX IF NOT EXISTS idx_sous_type_demande ON demande(id_sous_type_demande);
CREATE INDEX IF NOT EXISTS idx_recherche_antecendent_demande ON recherche_antecendent(id_demande);
CREATE INDEX IF NOT EXISTS idx_recherche_antecendent_critere ON recherche_antecendent(critere_recherche);
CREATE INDEX IF NOT EXISTS idx_carte_resident_demande ON carte_resident(id_demande);
CREATE INDEX IF NOT EXISTS idx_decision_document_demande ON decision_document(id_demande);
