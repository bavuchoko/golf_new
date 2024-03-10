package pjs.golf.app.competition.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pjs.golf.app.competition.entitiy.Competition;

public interface CompetitionJpaRepository extends JpaRepository<Competition, Long> {
}
