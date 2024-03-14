package pjs.golf.app.fields.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;
import pjs.golf.common.SearchDto;

public interface FieldsService {
    CollectionModel getFieldList(SearchDto search, Pageable pageable, PagedResourcesAssembler<Fields> assembler);

    EntityModel createField(FieldsRequestDto fields, Account account);

    EntityModel getFieldInfo(Long id, Account account);

    EntityModel updateFields(FieldsRequestDto fieldsDto, Account account);

    void removeFields(Long id, Account account);
}
