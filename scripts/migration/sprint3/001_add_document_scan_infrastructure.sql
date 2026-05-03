-- Sprint 3 Migration: Infrastructure de scan et upload de documents

-- =====================================================
-- 1. Créer table document_scan
-- =====================================================
CREATE TABLE IF NOT EXISTS document_scan (
    id SERIAL PRIMARY KEY,
    id_piece_a_fournir INT NOT NULL,
    id_demande INT,
    chemin_fichier VARCHAR(500) NOT NULL,
    nom_fichier VARCHAR(255) NOT NULL,
    type_mime VARCHAR(50),
    taille_fichier INT,
    numero_page INT,
    date_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_piece_a_fournir) REFERENCES piece_a_fournir(id) ON DELETE CASCADE,
    FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE
);

-- =====================================================
-- 2. Ajouter colonnes à piece_a_fournir
-- =====================================================
ALTER TABLE piece_a_fournir ADD COLUMN IF NOT EXISTS scan_complete BOOLEAN DEFAULT FALSE;
ALTER TABLE piece_a_fournir ADD COLUMN IF NOT EXISTS date_scan_complete TIMESTAMP;

-- =====================================================
-- 3. Initialiser les colonnes existantes
-- =====================================================
UPDATE piece_a_fournir SET scan_complete = FALSE WHERE scan_complete IS NULL;

-- =====================================================
-- 4. Créer index pour performances
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_document_scan_piece ON document_scan(id_piece_a_fournir);
CREATE INDEX IF NOT EXISTS idx_document_scan_date ON document_scan(date_upload);
CREATE INDEX IF NOT EXISTS idx_piece_scan_complete ON piece_a_fournir(scan_complete);
CREATE INDEX IF NOT EXISTS idx_document_scan_demande ON document_scan(id_demande);

-- =====================================================
-- 5. Ajouter le statut 'SCAN_TERMINE' si absent
-- =====================================================
INSERT INTO statut_demande (libelle)
SELECT 'SCAN_TERMINE'
WHERE NOT EXISTS (SELECT 1 FROM statut_demande WHERE libelle = 'SCAN_TERMINE');
