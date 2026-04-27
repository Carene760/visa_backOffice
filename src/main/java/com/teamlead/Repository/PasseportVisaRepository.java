package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.PasseportVisa;

@Repository
public interface PasseportVisaRepository extends JpaRepository<PasseportVisa, Integer> {
}
