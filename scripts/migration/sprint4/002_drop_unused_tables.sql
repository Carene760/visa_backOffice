-- Sprint 4: Drop unused tables and columns after code cleanup
-- Entities removed: SousTypeDemande, RechercheAntecedent
-- Models cleaned: Demande (removed demandeSource, estBaseGeneree)
-- 
-- PREREQUISITES: Ensure no application code references these anymore
-- Code changes: 
--   - Removed SousTypeDemande entity, repository, service
--   - Removed RechercheAntecedent entity, repository, and persistence service
--   - Removed demandeSource and estBaseGeneree from Demande model
-- 
-- SAFETY: Use ROLLBACK_000_full_sprint4.sql if you need to undo

BEGIN;

-- 1) Drop recherche_antecendent table (no longer used by application)
--    This table was used to store antecedent search history.
--    RechercheAntecedentsService (read-only DTO) remains for searches.
DROP TABLE IF EXISTS recherche_antecendent CASCADE;

-- 2) Drop sous_type_demande table (no longer used)
--    The entity was removed from codebase and never populated.
DROP TABLE IF EXISTS sous_type_demande CASCADE;

-- 3) Remove FK column id_demande_source from demande
--    Chaining logic simplified: no longer persisted in DB.
ALTER TABLE demande
    DROP COLUMN IF EXISTS id_demande_source;

-- 4) Remove est_base_generee column (replaced by business logic)
--    Generated bases now identified by NOUVEAU_TITRE type + SANS_DONNEES_ANTERIEURES flag.
ALTER TABLE demande
    DROP COLUMN IF EXISTS est_base_generee;

-- 5) Verify cleanup by checking remaining columns
-- SELECT column_name FROM information_schema.columns 
-- WHERE table_name='demande' ORDER BY ordinal_position;

COMMIT;

-- SUCCESS: All legacy columns and tables removed.
-- If errors occur, check:
--   1. No foreign key constraints still reference these columns/tables
--   2. Application code no longer uses these (verify compile passed)
--   3. No active queries using these tables

