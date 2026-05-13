package com.teamlead.Repository;

import com.teamlead.Model.DocumentSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentSignatureRepository extends JpaRepository<DocumentSignature, Integer> {

    /**
     * Récupère une signature/photo par demandeur, demande et type
     */
    Optional<DocumentSignature> findByDemandeurIdAndDemandeIdAndTypeDocument(
            Integer demandeurId, Integer demandeId, String typeDocument);

    /**
     * Récupère toutes les signatures/photos pour une demande
     */
    List<DocumentSignature> findByDemandeId(Integer demandeId);

    /**
     * Récupère toutes les signatures/photos pour un demandeur
     */
    List<DocumentSignature> findByDemandeurId(Integer demandeurId);

    /**
     * Vérifie si une photo webcam existe pour une demande
     */
    @Query("SELECT CASE WHEN COUNT(ds) > 0 THEN true ELSE false END " +
           "FROM DocumentSignature ds " +
           "WHERE ds.demande.id = :demandeId AND ds.typeDocument = 'PHOTO_WEBCAM'")
    boolean existsPhotoWebcam(@Param("demandeId") Integer demandeId);

    /**
     * Vérifie si une signature souris existe pour une demande
     */
    @Query("SELECT CASE WHEN COUNT(ds) > 0 THEN true ELSE false END " +
           "FROM DocumentSignature ds " +
           "WHERE ds.demande.id = :demandeId AND ds.typeDocument = 'SIGNATURE_SOURIS'")
    boolean existsSignatureSouris(@Param("demandeId") Integer demandeId);

    /**
     * Récupère la photo webcam pour une demande
     */
    @Query("SELECT ds FROM DocumentSignature ds " +
           "WHERE ds.demande.id = :demandeId AND ds.typeDocument = 'PHOTO_WEBCAM'")
    Optional<DocumentSignature> findPhotoWebcamByDemandeId(@Param("demandeId") Integer demandeId);

    /**
     * Récupère la signature souris pour une demande
     */
    @Query("SELECT ds FROM DocumentSignature ds " +
           "WHERE ds.demande.id = :demandeId AND ds.typeDocument = 'SIGNATURE_SOURIS'")
    Optional<DocumentSignature> findSignatureSourisByDemandeId(@Param("demandeId") Integer demandeId);

    /**
     * Supprime un document par demande ID et type
     */
    @Query("DELETE FROM DocumentSignature ds " +
           "WHERE ds.demande.id = :demandeId AND ds.typeDocument = :typeDocument")
    void deleteByDemandeIdAndTypeDocument(@Param("demandeId") Integer demandeId, @Param("typeDocument") String typeDocument);
}
