package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.TypeEvenement;

@Repository
public interface TypeEvenementRepository extends JpaRepository<TypeEvenement, Integer> {
    TypeEvenement findByCode(String code);
}
