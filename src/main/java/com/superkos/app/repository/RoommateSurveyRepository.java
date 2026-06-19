package com.superkos.app.repository;

import com.superkos.app.model.RoommateSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// #nadia(Roommate Survey)
@Repository
public interface RoommateSurveyRepository extends JpaRepository<RoommateSurvey, Integer> {
}
// #/nadia(Roommate Survey)
