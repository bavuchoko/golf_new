package pjs.golf.app.fields.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import pjs.golf.app.fields.entity.Fields;

public interface FieldsJpaRepository extends JpaRepository<Fields, Long> {
}
