package com.teamlead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.TypeDocument;

@Repository
public interface TypeDocumentRepository extends JpaRepository<TypeDocument, Integer> {
    List<TypeDocument> findByObligatoire(Boolean obligatoire);
    List<TypeDocument> findByTypeMotifIsNull();
    List<TypeDocument> findByTypeMotif_Id(Integer idTypeMotif);
    List<TypeDocument> findByTypeMotifIsNotNull();
}
