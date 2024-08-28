package pjs.golf.app.fields.respository.querydsl;


import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.fields.entity.QFields;
import pjs.golf.common.SearchDto;
import pjs.golf.common.jpa.QuerydslCommonMethod;

import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static pjs.golf.app.fields.entity.QFields.fields;

@Repository
public class FieldsJpaQuerydslSupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManage;
    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     * @param queryFactory
     * @param entityManage
     */
    public FieldsJpaQuerydslSupport(JPAQueryFactory jpaQueryFactory, EntityManager entityManage) {
        super(Fields.class);
        this.queryFactory = jpaQueryFactory;
        this.entityManage = entityManage;
    }

    QFields fields = QFields.fields;

    @Transactional(readOnly = true)
    public Page<Fields> getFieldsListBySearCh(SearchDto search, Pageable pageable) {

        JPAQuery<Fields> query= queryFactory.selectFrom(fields).where(
                        constainSerachText(search).or(eqCity(search))
                )
                .orderBy(QuerydslCommonMethod.getOrderList(pageable.getSort(), Fields.class).stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                ;
        long totalCount = query.stream().count();
        List<Fields> result = getQuerydsl().applyPagination(pageable,query).fetch();
        return new PageImpl<>(result,pageable,totalCount);
    }




    private BooleanExpression constainSerachText(SearchDto search) {
        if (StringUtils.hasText(search.getSearchTxt()))
            return fields.name.contains(search.getSearchTxt());
        return Expressions.TRUE;
    }

    private BooleanExpression eqCity(SearchDto search) {
        if (StringUtils.hasText(search.getCity()))
            return fields.city.eq(search.getCity());
        return Expressions.TRUE;
    }


    /**
     * 가까운 경기장은 도시는 검색조건으로 받지 않음.
     * 검색어, 2km 이내, 가까운 순으로
     * */
    public Page<Fields> getNearFieldListResources(SearchDto search, Double latitude, Double longitude, Pageable pageable, PagedResourcesAssembler<Fields> assembler) {
        NumberTemplate<Double> distance = Expressions.numberTemplate(Double.class,
                "function('ST_Distance_Sphere', POINT({0}, {1}), POINT({2}, {3}))",
                fields.longitude,
                fields.latitude,
                longitude,
                latitude
        );

        BooleanExpression withinRadius = distance.loe(2000.0);

        JPAQuery<Fields> query = queryFactory.selectFrom(fields)
                .where(withinRadius)
                .where(constainSerachText(search))
                .orderBy(distance.asc())
                .limit(pageable.getPageSize());

        // 쿼리 실행 및 결과 반환
        List<Fields> result = getQuerydsl().applyPagination(pageable, query).fetch();
        long totalCount = query.fetchCount();

        return new PageImpl<>(result, pageable, totalCount);
    }
}
