package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.SituationMatrimoniale;

@Repository
public interface SituationMatrimoniaRepository extends JpaRepository<SituationMatrimoniale, Integer> {
}
