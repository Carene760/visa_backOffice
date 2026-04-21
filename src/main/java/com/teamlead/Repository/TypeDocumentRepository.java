package com.teamlead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.TypeDocument;

@Repository
public interface TypeDocumentRepository extends JpaRepository<TypeDocument, Integer> {
    
    /**
     * Récupère les documents obligatoires
     */
    List<TypeDocument> findByObligatoireTrue();
    
    /**
     * Récupère un document par son code
     */
    TypeDocument findByCode(String code);
    
    /**
     * Récupère un document par son libellé
     */
    TypeDocument findByLibelle(String libelle);
}
