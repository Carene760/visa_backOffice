package com.teamlead.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamlead.Model.Nationalite;
import com.teamlead.Repository.NationaliteRepository;

@Service
public class NationaliteService {

    @Autowired
    private NationaliteRepository nationaliteRepository;

    public List<Nationalite> findAll() {
        return nationaliteRepository.findAll();
    }

    public Nationalite findById(Integer id) {
        return nationaliteRepository.findById(id).orElse(null);
    }
}
