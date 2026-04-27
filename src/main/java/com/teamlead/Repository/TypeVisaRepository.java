package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.TypeVisa;

@Repository
public interface TypeVisaRepository extends JpaRepository<TypeVisa, Integer> {
    TypeVisa findByLibelle(String libelle);
}
