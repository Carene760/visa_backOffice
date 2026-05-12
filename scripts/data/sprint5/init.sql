-- Sprint 5 seed data: workflow dossier / photo-signature / scan

INSERT INTO statut_demande (libelle)
SELECT 'DOSSIER_CREE'
WHERE NOT EXISTS (SELECT 1 FROM statut_demande WHERE libelle = 'DOSSIER_CREE');

INSERT INTO statut_demande (libelle)
SELECT 'PHOTO_SIGNATURE_TERMINE'
WHERE NOT EXISTS (SELECT 1 FROM statut_demande WHERE libelle = 'PHOTO_SIGNATURE_TERMINE');

INSERT INTO statut_demande (libelle)
SELECT 'SCAN_TERMINE'
WHERE NOT EXISTS (SELECT 1 FROM statut_demande WHERE libelle = 'SCAN_TERMINE');