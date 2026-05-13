package com.superkos.app.repository;

import com.superkos.app.model.PencariHunian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PencariHunianRepository extends JpaRepository<PencariHunian, Integer> {
}
