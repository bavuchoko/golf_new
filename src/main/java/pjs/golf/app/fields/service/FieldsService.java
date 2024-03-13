package pjs.golf.app.fields.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.member.entity.Member;
import pjs.golf.common.SearchDto;

public interface FieldsService {
    CollectionModel getFieldList(SearchDto search, Pageable pageable, PagedResourcesAssembler<Fields> assembler);

    EntityModel createField(FieldsRequestDto fields, Member member);

    EntityModel getFieldInfo(Long id, Member member);
}
