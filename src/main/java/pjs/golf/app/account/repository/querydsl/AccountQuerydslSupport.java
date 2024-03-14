package pjs.golf.app.account.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import pjs.golf.app.account.entity.QAccount;
import pjs.golf.app.account.entity.Account;

import java.util.List;

@Repository
public class AccountQuerydslSupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;
    public AccountQuerydslSupport(JPAQueryFactory queryFactory) {
        super(Account.class);
        this.queryFactory = queryFactory;
    }

    QAccount account = QAccount.account;

    public List getTempUsersByUserNames(List names) {
        List<Account> query= queryFactory.selectFrom(account).where(
                        account.name.in(names).and(
                        account.username.like("temp%"))
                ).fetch();
        return query;
    }

}
