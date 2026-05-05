-- Sprint 4: Migrate CarteResident foreign key from Passeport to Visa (OPTION B: FULL IMMEDIATE)
-- 
-- OBJECTIVE: Change CarteResident relationship from:
--   carteResident.id_passeport -> passeport.id
-- To:
--   carteResident.id_visa -> visa.id
-- 
-- RATIONALE: Visa is the primary identity for authorization; Passeport is just a document.
-- 
-- CRITICAL PREREQUISITES (REQUIRED BEFORE RUNNING):
--   1. Full database backup (NON-NEGOTIABLE)
--   2. Application code updated: CarteResident.java has ONLY visa FK (no passeport FK)
--   3. No code references to carteResident.getPasseport() (verified via grep)
--   4. Deploy new code BEFORE running this migration
-- 
-- ROLLBACK: 
--   - Restore from backup (only safe option after this script runs)
--   - Manual rollback not recommended: use ROLLBACK_000_full_sprint4.sql if needed
-- 
-- RISK: HIGH (destructive) — this is a complete migration with no safety net.

BEGIN;

-- STEP 1: Drop old FK constraint
ALTER TABLE carte_resident 
    DROP CONSTRAINT IF EXISTS fk_carte_resident_id_passeport;

-- STEP 2: Add new column for FK to visa
ALTER TABLE carte_resident
    ADD COLUMN IF NOT EXISTS id_visa INTEGER;

-- STEP 3: Populate id_visa from existing Passeport -> Visa relationship
-- 
-- OPTION A: Direct FK (Visa.id_passeport -> Passeport.id)
-- Use if: Visa table has direct "id_passeport" column
UPDATE carte_resident cr
SET id_visa = v.id
FROM passeport p
JOIN visa v ON v.id_passeport = p.id
WHERE cr.id_passeport = p.id
  AND cr.id_visa IS NULL;

-- Verify rows updated
-- SELECT COUNT(*) as updated_from_direct_fk FROM carte_resident WHERE id_visa IS NOT NULL AND id_passeport IS NOT NULL;

-- STEP 4: Add FK constraint to visa (NOT NULL to enforce data completeness)
ALTER TABLE carte_resident
    ADD CONSTRAINT fk_carte_resident_id_visa 
        FOREIGN KEY (id_visa) REFERENCES visa(id) 
        ON DELETE RESTRICT  -- Prevent accidental visa deletions
        ON UPDATE CASCADE;

-- STEP 5: Create index for performance
CREATE INDEX IF NOT EXISTS idx_carte_resident_id_visa ON carte_resident(id_visa);

-- STEP 6: Verify data coverage before final drop
-- CRITICAL: Run this and check results before proceeding past STEP 7!
-- SELECT 
--   COUNT(*) as total,
--   COUNT(CASE WHEN id_visa IS NOT NULL THEN 1 END) as with_visa_fk,
--   COUNT(CASE WHEN id_visa IS NULL THEN 1 END) as without_visa_fk
-- FROM carte_resident;

-- STEP 7: Drop old Passeport column (IMMEDIATE, NO SAFETY WINDOW)
-- This is the point of no return — ensure backup exists and app deployed first
ALTER TABLE carte_resident 
    DROP COLUMN id_passeport;

-- STEP 8: Verify final state
-- SELECT 
--   column_name, 
--   is_nullable, 
--   constraint_name 
-- FROM information_schema.columns c
-- LEFT JOIN information_schema.table_constraints tc ON tc.table_name = c.table_name
-- WHERE c.table_name = 'carte_resident'
-- ORDER BY c.ordinal_position;

COMMIT;

-- SUCCESS: CarteResident now exclusively uses Visa FK.
-- Previous state cannot be recovered without restore from backup.
-- 
-- Post-migration checks (run these manually):
--   1. SELECT COUNT(*) FROM carte_resident; -- Should have data
--   2. SELECT COUNT(DISTINCT id_visa) FROM carte_resident; -- Should be high
--   3. App logs for any FK constraint errors
--   4. Load detail page for a demand with carte_resident — should show Visa info
