-- ROLLBACK SCRIPT for Sprint 4 migrations
-- Use this if you need to revert the DROP operations.
-- Only run AFTER verifying you have backed up your data.

BEGIN;

-- Restore sous_type_demande table
CREATE TABLE IF NOT EXISTS sous_type_demande (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);

-- Restore recherche_antecendent table
CREATE TABLE IF NOT EXISTS recherche_antecendent (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER NOT NULL,
    critere_recherche VARCHAR(50) NOT NULL,
    valeur_recherchee VARCHAR(100) NOT NULL,
    trouve BOOLEAN NOT NULL DEFAULT FALSE,
    date_recherche TIMESTAMP,
    CONSTRAINT recherche_antecendent_id_demande_fkey FOREIGN KEY (id_demande) REFERENCES demande(id)
);

-- Restore columns in demande table
ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS id_sous_type_demande INTEGER,
    ADD COLUMN IF NOT EXISTS id_demande_source INTEGER,
    ADD COLUMN IF NOT EXISTS est_base_generee BOOLEAN DEFAULT FALSE;

ALTER TABLE carte_resident
    ADD COLUMN IF NOT EXISTS id_passeport INTEGER,
    ADD CONSTRAINT carte_resident_id_passeport_fkey FOREIGN KEY (id_passeport) REFERENCES visa(id);

-- Add foreign key constraints
ALTER TABLE demande
    ADD CONSTRAINT demande_id_sous_type_demande_fkey 
        FOREIGN KEY (id_sous_type_demande) REFERENCES sous_type_demande(id) ON DELETE SET NULL;

ALTER TABLE demande
    ADD CONSTRAINT demande_id_demande_source_fkey 
        FOREIGN KEY (id_demande_source) REFERENCES demande(id) ON DELETE SET NULL;

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_recherche_antecendent_id_demande ON recherche_antecendent(id_demande);
CREATE INDEX IF NOT EXISTS idx_demande_id_sous_type_demande ON demande(id_sous_type_demande);
CREATE INDEX IF NOT EXISTS idx_demande_id_demande_source ON demande(id_demande_source);

COMMIT;

-- If rollback fails:
-- 1. Check if tables/columns already exist: SELECT * FROM information_schema.tables WHERE table_name IN ('sous_type_demande', 'recherche_antecendent');
-- 2. If tables exist, you may need to manually restore data from your backup.
-- 3. Verify foreign key integrity after rollback: SELECT constraint_name FROM information_schema.table_constraints WHERE table_name='demande';
