-- type_motif
INSERT INTO type_motif (libelle) VALUES ('TRAVAILLEUR'), ('INVESTISSEUR');

-- statut_demande
INSERT INTO statut_demande (libelle) VALUES ('DOSSIER_CREE');

-- type_demande
INSERT INTO type_demande (libelle) VALUES ('NOUVEAU_TITRE'), ('DUPLICATA'), ('TRANSFERT_VISA');

-- type_document (pièces communes)
INSERT INTO type_document (libelle, id_type_motif, obligatoire) VALUES
  ('02 photos d identite', NULL, TRUE),
  ('Notice de renseignement', NULL, TRUE),
  ('Demande adressee au Ministere de l Interieur', NULL, TRUE),
  ('Photocopie certifiee du visa en cours de validite', NULL, TRUE),
  ('Photocopie certifiee de la premiere page du passeport', NULL, TRUE),
  ('Photocopie certifiee de la carte resident en cours de validite', NULL, FALSE),
  ('Certificat de residence a Madagascar', NULL, TRUE),
  ('Extrait de casier judiciaire moins de 3 mois', NULL, TRUE);

-- pièces spécifiques travailleur
INSERT INTO type_document (libelle, id_type_motif, obligatoire) VALUES
  ('Autorisation emploi delivree par le Ministere de la Fonction publique', 
    (SELECT id FROM type_motif WHERE libelle='TRAVAILLEUR'), TRUE),
  ('Attestation d emploi delivre par l employeur (Original)', 
    (SELECT id FROM type_motif WHERE libelle='TRAVAILLEUR'), TRUE);

-- pièces spécifiques investisseur
INSERT INTO type_document (libelle, id_type_motif, obligatoire) VALUES
  ('Statut de la Societe', (SELECT id FROM type_motif WHERE libelle='INVESTISSEUR'), TRUE),
  ('Extrait d inscription au registre de commerce', (SELECT id FROM type_motif WHERE libelle='INVESTISSEUR'), TRUE),
  ('Carte fiscale', (SELECT id FROM type_motif WHERE libelle='INVESTISSEUR'), TRUE);

insert into situation_matrimoniale (libelle) values ('CELIBATAIRE'), ('MARIE(E)'), ('DIVORCE(E)'), ('VEUF(VEUVE)');

insert into nationalite (libelle) values ('FRANCAIS(E)'), ('ANGLOPHONE'), ('CHINOIS(E)'), ('INDIEN(NE)'), ('AUTRE');

insert into type_visa(libelle) values ('TRANSFORMABLE');
insert into type_evenement(code) values ('CREATION DEMANDE');