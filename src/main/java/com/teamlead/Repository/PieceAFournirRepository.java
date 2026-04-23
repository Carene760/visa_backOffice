package com.teamlead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.PieceAFournir;

@Repository
public interface PieceAFournirRepository extends JpaRepository<PieceAFournir, Integer> {
    /**
     * Récupère toutes les pièces à fournir pour une demande
     */
    List<PieceAFournir> findByDemandeId(Integer demandeId);
    
    /**
     * Récupère les pièces à fournir obligatoires pour une demande
     */
    List<PieceAFournir> findByDemandeIdAndTypeDocumentObligatoireTrue(Integer demandeId);
    
    /**
     * Compte le nombre de pièces obligatoires présentes pour une demande
     */
    Long countByDemandeIdAndTypeDocumentObligatoireTrueAndPresentTrue(Integer demandeId);
}
