CREATE DATABASE visa;
\c visa;

CREATE TABLE situation_matrimoniale ( -- siuation familiale
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255)
);

CREATE TABLE nationalite ( 
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255)
);

CREATE TABLE demandeur (
  id SERIAL PRIMARY KEY,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100),
  nom_naissance VARCHAR(100),
  email VARCHAR(150) UNIQUE,
  telephone VARCHAR(20) NOT NULL UNIQUE,
  id_nationalite INT NOT NULL,
  date_naissance DATE NOT NULL,
  lieu_naissance VARCHAR(150),
  id_situation_matrimoniale INT,
  adresse_madagascar VARCHAR(255) NOT NULL,
  date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_nationalite) REFERENCES nationalite(id),
  FOREIGN KEY (id_situation_matrimoniale) REFERENCES situation_matrimoniale(id)
);

CREATE INDEX idx_demandeur_nom ON demandeur(nom);

CREATE TABLE passeport (
  id SERIAL PRIMARY KEY,
  id_demandeur INT NOT NULL,
  numero VARCHAR(50) NOT NULL UNIQUE,
  date_delivrance DATE NOT NULL,
  date_expiration DATE NOT NULL,
  date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_demandeur) REFERENCES demandeur(id) ON DELETE CASCADE,
  CHECK (date_expiration > date_delivrance)
);

CREATE INDEX idx_passeport_demandeur ON passeport(id_demandeur);
CREATE INDEX idx_passeport_numero ON passeport(numero);

CREATE TABLE type_motif ( -- "TRAVAILLEUR", "INVESTISSEUR"
  id SERIAL PRIMARY KEY,
  libelle VARCHAR(100) NOT NULL UNIQUE 
);

-- statut d'une demande
CREATE TABLE statut_demande ( -- "ENREGISTREE"
  id SERIAL PRIMARY KEY,
  libelle VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE type_demande ( -- "NOUVELLE", "DUPLICATA"
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) UNIQUE
);

CREATE TABLE type_visa ( -- "TRANSFORMABLE", "LONG_SEJOUR_TRAVAILLEUR", "LONG_SEJOUR_INVESTISSEUR"
  id SERIAL PRIMARY KEY,
  libelle VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE type_document (
  id SERIAL PRIMARY KEY,
  code VARCHAR(100),
  libelle VARCHAR(100) NOT NULL UNIQUE,
  id_type_motif INT, -- NULL = obligatoire pour tous, sinon spécifique au motif
  obligatoire BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (id_type_motif) REFERENCES type_motif(id) ON DELETE CASCADE
);

CREATE TABLE demande ( -- demande de visa
  id SERIAL PRIMARY KEY,
  id_demandeur INT NOT NULL,
  id_type_motif INT NOT NULL,
  id_type_demande INT NOT NULL,
  id_statut_demande INT NOT NULL,
  date_demande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  date_traitement TIMESTAMP,
  date_expiration_demande DATE,
  date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_demandeur) REFERENCES demandeur(id) ON DELETE CASCADE,
  FOREIGN KEY (id_type_motif) REFERENCES type_motif(id),
  FOREIGN KEY (id_type_demande) REFERENCES type_demande(id),
   FOREIGN KEY (id_statut_demande) REFERENCES statut_demande(id)
);

CREATE INDEX idx_demande_demandeur ON demande(id_demandeur);

-- table visa generalisee: un seul stockage pour transformable et long sejour
-- rattachee a la demande, puis liee au passeport via passeport_visa
CREATE TABLE visa (
  id SERIAL PRIMARY KEY,
  reference VARCHAR(100) NOT NULL UNIQUE,
  id_type_visa INT NOT NULL,
  date_entree DATE NOT NULL,
  lieu_entree VARCHAR(150),
  date_expiration DATE NOT NULL,
  id_demande INT NOT NULL,
  date_emission TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_type_visa) REFERENCES type_visa(id),
  FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE,
  CHECK (date_expiration >= date_entree)
);


CREATE INDEX idx_reference ON visa(reference);
CREATE INDEX idx_visa_type ON visa(id_type_visa);

CREATE TABLE motif_transfert (  -- "perte", "expiration", "renouvellement"...
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100)
);


CREATE TABLE passeport_visa (
  id SERIAL PRIMARY KEY,
  id_passeport INT NOT NULL,
  id_visa INT NOT NULL,
  date_association DATE NOT NULL,
  date_transfert DATE, -- NULL si actuellement actif, rempli si transféré
  id_motif_transfert INT,
  date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_passeport) REFERENCES passeport(id) ON DELETE CASCADE,
  FOREIGN KEY (id_visa) REFERENCES visa(id) ON DELETE CASCADE,
  FOREIGN KEY (id_motif_transfert) REFERENCES motif_transfert(id)
);

CREATE INDEX idx_passeport ON passeport_visa(id_passeport);
CREATE INDEX idx_visa ON passeport_visa(id_visa);
CREATE UNIQUE INDEX uq_passeport_visa_visa_actif ON passeport_visa(id_visa) WHERE date_transfert IS NULL;

CREATE TABLE piece_a_fournir (
  id SERIAL PRIMARY KEY,
  id_demande INT NOT NULL,
  id_type_document INT NOT NULL,
  nom_fichier VARCHAR(255),
  present BOOLEAN DEFAULT FALSE,
  valide BOOLEAN DEFAULT FALSE,
  date_depot TIMESTAMP,
  date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE,
  FOREIGN KEY (id_type_document) REFERENCES type_document(id)
);

CREATE INDEX idx_piece_demande ON piece_a_fournir(id_demande);
CREATE INDEX idx_type ON piece_a_fournir(id_type_document);

CREATE TABLE historique_statut_demande (
  id SERIAL PRIMARY KEY,
  id_demande INT NOT NULL,
  id_statut INT NOT NULL,
  date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE,
  FOREIGN KEY (id_statut) REFERENCES statut_demande(id)
);

CREATE INDEX idx_demande ON historique_statut_demande(id_demande);
CREATE INDEX idx_date ON historique_statut_demande(date_changement);

-- Activite realise par un demandeur
CREATE TABLE type_evenement (
  id SERIAL PRIMARY KEY,
  code VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE journal_activite (
  id SERIAL PRIMARY KEY,
  id_demandeur INT NOT NULL,
  id_type_evenement INT NOT NULL, -- "CREATION_DEMANDE", "VALIDATION_PIECE", "TRANSFERT_VISA", etc.
  date_action TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_demandeur) REFERENCES demandeur(id),
  FOREIGN KEY (id_type_evenement) REFERENCES type_evenement(id)
);


-- conception à prevoir meme si
CREATE TABLE carte_resident (
  id SERIAL PRIMARY KEY,
  reference VARCHAR(100) NOT NULL UNIQUE,
  date_entree DATE NOT NULL,
  lieu_entree VARCHAR(150),
  date_expiration DATE,
  id_demande INT NOT NULL,
  id_passeport INT NOT NULL, 
  date_emission TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_demande) REFERENCES demande(id),
  FOREIGN KEY (id_passeport) REFERENCES passeport(id),
  CHECK (date_expiration >= date_entree)
);






