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
        return typeDocumentRepository.findByIdTypeMotifIsNull();
    }

    public List<TypeDocument> findDocumentsByTypeMotif(Integer idTypeMotif) {
        return typeDocumentRepository.findByIdTypeMotif(idTypeMotif);
    }

    public List<TypeDocument> findObligatoires() {
        return typeDocumentRepository.findByObligatoire(true);
    }
}
