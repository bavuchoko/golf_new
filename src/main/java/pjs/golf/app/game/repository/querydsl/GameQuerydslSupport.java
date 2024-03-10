package pjs.golf.app.game.repository.querydsl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.game.entity.QGame;
import pjs.golf.app.member.entity.Member;
import pjs.golf.common.SearchDto;
import pjs.golf.common.jpa.QuerydslCommonMethod;

import java.util.List;

@Service
public class GameQuerydslSupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;
    public GameQuerydslSupport(JPAQueryFactory queryFactory) {
        super(Game.class);
        this.queryFactory = queryFactory;
    }

    QGame game = QGame.game;



    public Page<Game> getGameListBetweenDate(SearchDto search, Pageable pageable, Member member, boolean isRemoved) {

        JPAQuery<Game> query= queryFactory.selectFrom(game).where(
                        getBySearchOption(search, member, isRemoved)
                )
                .orderBy(QuerydslCommonMethod.getOrderList(pageable.getSort(), Game.class).stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                ;
        List<Game> result = getQuerydsl().applyPagination(pageable,query).fetch();
        return new PageImpl<>(result,pageable, result.size());
    }

    private BooleanBuilder getBySearchOption(SearchDto searchDto, Member member, Boolean isRemoved) {
        BooleanBuilder builder = new BooleanBuilder();
        return builder

                .and(QuerydslCommonMethod.nullSafeBuilder(() ->{
                    if(isRemoved ==null) return null;
                    return game.isRemoved.eq(isRemoved);
                }));

    }
}
