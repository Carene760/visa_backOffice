package com.teamlead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamlead.Model.JournalActivite;

@Repository
public interface JournalActiviteRepository extends JpaRepository<JournalActivite, Integer> {
}
