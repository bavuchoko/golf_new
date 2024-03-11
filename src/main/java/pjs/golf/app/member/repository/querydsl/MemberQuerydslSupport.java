package pjs.golf.app.member.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.member.entity.QMember;

import java.util.List;

@Repository
public class MemberQuerydslSupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;
    public MemberQuerydslSupport(JPAQueryFactory queryFactory) {
        super(Member.class);
        this.queryFactory = queryFactory;
    }

    QMember member = QMember.member;

    public List getTempUsersByUserNames(List names) {
        List<Member> query= queryFactory.selectFrom(member).where(
                        member.name.in(names).and(
                        member.username.like("temp%"))
                ).fetch();
        return query;
    }

}
