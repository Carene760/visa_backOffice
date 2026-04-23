package com.teamlead.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamlead.Model.TypeDocument;
import com.teamlead.Repository.TypeDocumentRepository;
import java.util.List;

@Service
public class TypeDocumentService {

    @Autowired
    private TypeDocumentRepository typeDocumentRepository;

    public List<TypeDocument> findAll() {
        return typeDocumentRepository.findAll();
    }

    public List<TypeDocument> findDocumentsCommuns() {
        return typeDocumentRepository.findByTypeMotifIsNull();
    }

    public List<TypeDocument> findDocumentsByTypeMotif(Integer idTypeMotif) {
        return typeDocumentRepository.findByTypeMotif_Id(idTypeMotif);
    }

    public List<TypeDocument> findDocumentsSpecifiques() {
        return typeDocumentRepository.findByTypeMotifIsNotNull();
    }

    public List<TypeDocument> findObligatoires() {
        return typeDocumentRepository.findByObligatoire(true);
    }
}
