-- Sprint 4 - QR Code Infrastructure
-- Ajouter les colonnes nécessaires pour la gestion des QR Codes

-- Ajouter colonne pour les données QR Code
ALTER TABLE demande ADD COLUMN qr_code_data TEXT NULL;

-- Ajouter colonne pour l'URL de suivi
ALTER TABLE demande ADD COLUMN qr_code_url VARCHAR(500) NULL;

-- Ajouter flag pour indiquer si QR Code a été généré
ALTER TABLE demande ADD COLUMN qr_code_generated BOOLEAN DEFAULT FALSE;

-- Ajouter date de génération du QR Code
ALTER TABLE demande ADD COLUMN date_generation_qr_code TIMESTAMP NULL;

-- Ajouter token de suivi unique
ALTER TABLE demande ADD COLUMN tracking_token VARCHAR(100) UNIQUE NULL;

-- Créer des index pour optimiser les recherches
CREATE INDEX idx_demande_numero ON demande(id);
CREATE INDEX idx_demandeur_passeport ON demandeur(numero_passeport) WHERE numero_passeport IS NOT NULL;
CREATE INDEX idx_tracking_token ON demande(tracking_token) WHERE tracking_token IS NOT NULL;

-- Log de migration
INSERT INTO journal_activite (description, date_creation) 
VALUES ('Sprint 4: QR Code Infrastructure ajoutée', NOW());
