package pjs.golf.app.sheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pjs.golf.app.sheet.entity.Sheet;

public interface SheepJpaRepository extends JpaRepository<Sheet, Long> {
}
