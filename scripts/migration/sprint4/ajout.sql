CREATE TABLE decision(
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL
);
ALTER TABLE decision_document ADD COLUMN decision INT;
ALTER TABLE decision_document ADD CONSTRAINT fk_decision FOREIGN KEY (decision) REFERENCES decision(id);