-- Sprint 5 Migration: Table document_signature pour photo et signature

-- =====================================================
-- 1. Créer table document_signature
-- =====================================================
CREATE TABLE IF NOT EXISTS document_signature (
    id SERIAL PRIMARY KEY,
    id_demandeur INT NOT NULL,
    id_demande INT NOT NULL,
    type_document VARCHAR(50) NOT NULL CHECK (type_document IN ('PHOTO_WEBCAM', 'SIGNATURE_SOURIS')),
    contenu BYTEA NOT NULL,
    nom_fichier VARCHAR(255),
    type_mime VARCHAR(50),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_demandeur) REFERENCES demandeur(id) ON DELETE CASCADE,
    FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE,
    CONSTRAINT uk_demandeur_demande_type UNIQUE (id_demandeur, id_demande, type_document)
);

-- =====================================================
-- 2. Créer index pour performances
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_document_signature_demandeur ON document_signature(id_demandeur);
CREATE INDEX IF NOT EXISTS idx_document_signature_demande ON document_signature(id_demande);
CREATE INDEX IF NOT EXISTS idx_document_signature_type ON document_signature(type_document);
CREATE INDEX IF NOT EXISTS idx_document_signature_date ON document_signature(date_creation);

-- =====================================================
-- 3. Supprimer les colonnes de l'ancienne approche (optionnel, si migration de données)
-- =====================================================
-- Vous pouvez migrer les données existantes avant suppression si nécessaire
-- ALTER TABLE demandeur DROP COLUMN IF EXISTS photo_webcam;
-- ALTER TABLE demandeur DROP COLUMN IF EXISTS signature_souris;
ALTER TABLE demandeur DROP COLUMN IF EXISTS photo_terminee;
ALTER TABLE demandeur DROP COLUMN IF EXISTS signature_terminee;
