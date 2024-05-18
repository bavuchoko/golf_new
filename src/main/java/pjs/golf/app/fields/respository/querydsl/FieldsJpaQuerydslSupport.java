package pjs.golf.app.fields.respository.querydsl;


import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.fields.entity.QFields;
import pjs.golf.common.SearchDto;
import pjs.golf.common.jpa.QuerydslCommonMethod;

import java.util.List;

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
                        containCityOrName(search)
                )
                .orderBy(QuerydslCommonMethod.getOrderList(pageable.getSort(), Fields.class).stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                ;
        long totalCount = query.stream().count();
        List<Fields> result = getQuerydsl().applyPagination(pageable,query).fetch();
        return new PageImpl<>(result,pageable,totalCount);
    }




    private BooleanExpression containCityOrName(SearchDto search) {
        if (StringUtils.hasText(search.getSearchTxt()))
            return fields.name.contains(search.getSearchTxt());
        return null;
    }

}
