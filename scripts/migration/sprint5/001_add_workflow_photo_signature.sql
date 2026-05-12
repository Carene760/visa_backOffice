-- Sprint 5 Migration: Workflow photo/signature + statuts intermédiaires

-- =====================================================
-- 1. Ajouter les colonnes de capture au demandeur
-- =====================================================
ALTER TABLE demandeur ADD COLUMN IF NOT EXISTS photo_webcam BYTEA;
ALTER TABLE demandeur ADD COLUMN IF NOT EXISTS signature_souris BYTEA;
ALTER TABLE demandeur ADD COLUMN IF NOT EXISTS photo_terminee BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE demandeur ADD COLUMN IF NOT EXISTS signature_terminee BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE demandeur SET photo_terminee = FALSE WHERE photo_terminee IS NULL;
UPDATE demandeur SET signature_terminee = FALSE WHERE signature_terminee IS NULL;

-- =====================================================
-- 2. Ajouter les statuts du workflow sprint 5
-- =====================================================
INSERT INTO statut_demande (libelle)
SELECT 'DOSSIER_CREE'
WHERE NOT EXISTS (SELECT 1 FROM statut_demande WHERE libelle = 'DOSSIER_CREE');

INSERT INTO statut_demande (libelle)
SELECT 'PHOTO_SIGNATURE_TERMINE'
WHERE NOT EXISTS (SELECT 1 FROM statut_demande WHERE libelle = 'PHOTO_SIGNATURE_TERMINE');

INSERT INTO statut_demande (libelle)
SELECT 'SCAN_TERMINE'
WHERE NOT EXISTS (SELECT 1 FROM statut_demande WHERE libelle = 'SCAN_TERMINE');

-- =====================================================
-- 3. Conserver l'historique du workflow
-- =====================================================
ALTER TABLE historique_statut_demande
    ALTER COLUMN date_changement SET DEFAULT CURRENT_TIMESTAMP;