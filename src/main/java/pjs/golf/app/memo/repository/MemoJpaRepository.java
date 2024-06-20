package pjs.golf.app.memo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.memo.entity.Memo;

import java.util.List;

public interface MemoJpaRepository extends JpaRepository<Memo, Long> {
    List findByFieldAndAccount(Fields fields, Account account);
    List findMemoByAccountAndField(Account account, Fields field);
    Memo findMemoByAccountAndFieldAndRound(Account account, Fields field, int round);
}
