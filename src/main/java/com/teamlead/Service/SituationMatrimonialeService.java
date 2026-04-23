package com.teamlead.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamlead.Model.SituationMatrimoniale;
import com.teamlead.Repository.SituationMatrimonialeRepository;
import java.util.List;

@Service
public class SituationMatrimonialeService {

    @Autowired
    private SituationMatrimonialeRepository situationMatrimonialeRepository;

    public List<SituationMatrimoniale> findAll() {
        return situationMatrimonialeRepository.findAll();
    }

    public SituationMatrimoniale findById(Integer id) {
        return situationMatrimonialeRepository.findById(id).orElse(null);
    }
}
