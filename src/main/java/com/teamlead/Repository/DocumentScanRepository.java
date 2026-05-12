package com.teamlead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.DocumentScan;

@Repository
public interface DocumentScanRepository extends JpaRepository<DocumentScan, Integer> {
    
    @Query("SELECT ds FROM DocumentScan ds WHERE ds.pieceAFournir.id = :idPieceAFournir")
    List<DocumentScan> findByIdPieceAFournir(@Param("idPieceAFournir") Integer idPieceAFournir);
    
    @Query("SELECT ds FROM DocumentScan ds WHERE ds.pieceAFournir.demande.id = :idDemande ORDER BY ds.dateUpload DESC")
    List<DocumentScan> findByIdDemande(@Param("idDemande") Integer idDemande);
    
    @Query("SELECT COUNT(ds) FROM DocumentScan ds WHERE ds.pieceAFournir.id = :idPiece")
    Integer countByIdPieceAFournir(@Param("idPiece") Integer idPiece);

    @Query("SELECT COUNT(ds) FROM DocumentScan ds WHERE ds.demande.id = :idDemande")
    Integer countByIdDemande(@Param("idDemande") Integer idDemande);

    @Query("SELECT CASE WHEN COUNT(ds) > 0 THEN true ELSE false END FROM DocumentScan ds WHERE ds.pieceAFournir.id = :idPiece")
    boolean existsByIdPieceAFournir(@Param("idPiece") Integer idPiece);
}
