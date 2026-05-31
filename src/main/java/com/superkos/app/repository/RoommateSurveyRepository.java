package com.superkos.app.repository;

import com.superkos.app.model.RoommateSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoommateSurveyRepository extends JpaRepository<RoommateSurvey, Integer> {
}
