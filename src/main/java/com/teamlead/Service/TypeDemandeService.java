package com.teamlead.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamlead.Model.TypeDemande;
import com.teamlead.Repository.TypeDemandeRepository;
import java.util.List;

@Service
public class TypeDemandeService {

    @Autowired
    private TypeDemandeRepository typeDemandeRepository;

    public List<TypeDemande> findAll() {
        return typeDemandeRepository.findAll();
    }

    public TypeDemande findById(Integer id) {
        return typeDemandeRepository.findById(id).orElse(null);
    }
}
