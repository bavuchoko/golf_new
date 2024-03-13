package pjs.golf.app.memo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.memo.entity.Memo;

import java.util.List;

public interface MemoJpaRepository extends JpaRepository<Memo, Long> {
    List findByFieldAndMember(Fields fields, Member member);
}
