package com.superkos.app.repository;

import com.superkos.app.model.PemilikProperti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PemilikPropertiRepository extends JpaRepository<PemilikProperti, Integer> {
}
