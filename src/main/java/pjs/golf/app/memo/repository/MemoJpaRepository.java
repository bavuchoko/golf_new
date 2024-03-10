package pjs.golf.app.memo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pjs.golf.app.memo.entity.Memo;

public interface MemoJpaRepository extends JpaRepository<Memo, Long> {
}
