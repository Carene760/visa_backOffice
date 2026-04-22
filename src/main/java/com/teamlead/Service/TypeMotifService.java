package com.teamlead.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamlead.Model.TypeMotif;
import com.teamlead.Repository.TypeMotifRepository;
import java.util.List;

@Service
public class TypeMotifService {

    @Autowired
    private TypeMotifRepository typeMotifRepository;

    public List<TypeMotif> findAll() {
        return typeMotifRepository.findAll();
    }

    public TypeMotif findById(Integer id) {
        return typeMotifRepository.findById(id).orElse(null);
    }
}
