package com.teamlead.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.Decision;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, Integer> {

    Optional<Decision> findByLibelle(String libelle);
}