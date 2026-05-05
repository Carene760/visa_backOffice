-- Sprint 4 migration: introduce sans_donnees_anterieures while keeping legacy columns for compatibility

ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS sans_donnees_anterieures BOOLEAN DEFAULT FALSE;

UPDATE demande
SET sans_donnees_anterieures = COALESCE(avec_donnees_anterieures, FALSE)
WHERE sans_donnees_anterieures IS NULL;

COMMENT ON COLUMN demande.sans_donnees_anterieures IS
    'True when the demand is created without prior data and must follow the validated flow.';
