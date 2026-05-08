-- Sprint 4 Migration: Infrastructure QR Code et API publique

-- =====================================================
-- 1. Ajouter colonnes QR Code à table demande
-- =====================================================
ALTER TABLE demande ADD COLUMN IF NOT EXISTS qr_code_url VARCHAR(500);
ALTER TABLE demande ADD COLUMN IF NOT EXISTS qr_code_data BYTEA;
ALTER TABLE demande ADD COLUMN IF NOT EXISTS qr_code_generated BOOLEAN DEFAULT FALSE;
ALTER TABLE demande ADD COLUMN IF NOT EXISTS date_generation_qrcode TIMESTAMP;
ALTER TABLE demande ADD COLUMN IF NOT EXISTS tracking_token VARCHAR(100) UNIQUE;

-- =====================================================
-- 2. Créer index pour optimiser les requêtes
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_demande_tracking_token ON demande(tracking_token);
CREATE INDEX IF NOT EXISTS idx_demande_qr_generated ON demande(qr_code_generated);
CREATE INDEX IF NOT EXISTS idx_demande_demandeur_date ON demande(id_demandeur, date_demande DESC);

-- =====================================================
-- 3. Vérifier que tous les statuts nécessaires existent
-- =====================================================
INSERT INTO statut_demande (libelle) VALUES 
    ('DOSSIER_CREER'),
    ('SCAN_TERMINE'),
    ('APPROBATION_AUTO_SANS_ANTERIEURS'),
    ('EN_REVISION_MANUELLE'),
    ('APPROUVE'),
    ('REFUSE'),
    ('DUPLICATA_APPROUVE'),
    ('TRANSFERT_APPROUVE')
ON CONFLICT (libelle) DO NOTHING;
