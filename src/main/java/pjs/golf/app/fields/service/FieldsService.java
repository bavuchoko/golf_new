package pjs.golf.app.fields.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;
import pjs.golf.common.SearchDto;

import java.util.List;

public interface FieldsService {
    CollectionModel getFieldListResources(SearchDto search, Pageable pageable, PagedResourcesAssembler<Fields> assembler);
    List getFieldList();

    EntityModel createField(FieldsRequestDto fields, Account account);

    EntityModel getFieldInfo(Long id, Account account);
    Fields getField(Long id);

    EntityModel updateFields(FieldsRequestDto fieldsDto, Account account);

    void removeFields(Long id, Account account);
}
