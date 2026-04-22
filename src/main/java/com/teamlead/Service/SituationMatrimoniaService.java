package com.teamlead.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamlead.Model.SituationMatrimoniale;
import com.teamlead.Repository.SituationMatrimoniaRepository;
import java.util.List;

@Service
public class SituationMatrimoniaService {

    @Autowired
    private SituationMatrimoniaRepository situationMatrimoniaRepository;

    public List<SituationMatrimoniale> findAll() {
        return situationMatrimoniaRepository.findAll();
    }

    public SituationMatrimoniale findById(Integer id) {
        return situationMatrimoniaRepository.findById(id).orElse(null);
    }
}
