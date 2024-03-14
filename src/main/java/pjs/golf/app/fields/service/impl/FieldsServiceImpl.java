package pjs.golf.app.fields.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjs.golf.app.fields.FieldsController;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.fields.mapper.FieldsMapper;
import pjs.golf.app.fields.respository.FieldsJpaRepository;
import pjs.golf.app.fields.respository.querydsl.FieldsJpaQuerydslSupport;
import pjs.golf.app.fields.service.FieldsService;
import pjs.golf.app.account.entity.Account;
import pjs.golf.common.SearchDto;
import pjs.golf.common.exception.NoSuchDataException;
import pjs.golf.common.exception.PermissionLimitedCustomException;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Service
@RequiredArgsConstructor
public class FieldsServiceImpl implements FieldsService {

    private final FieldsJpaRepository fieldsJpaRepository;
    private final FieldsJpaQuerydslSupport fieldsJpaQuerydslSupport;
    @Override
    public CollectionModel getFieldList(SearchDto city, Pageable pageable, PagedResourcesAssembler<Fields> assembler) {
        Page<Fields> fields = fieldsJpaQuerydslSupport.getFieldsListBySearCh(city,pageable);
        return this.getResources(fields, assembler);
    }

    @Override
    public EntityModel getFieldInfo(Long id, Account account) {
        Fields fields = fieldsJpaRepository.findById(id)
                .orElseThrow(()->new NoSuchDataException(""));
        return this.getResource(fields, account);
    }

    @Override
    @Transactional
    public EntityModel updateFields(FieldsRequestDto fieldsDto, Account account) {
        Fields fields = fieldsJpaRepository.findById(fieldsDto.getId()).orElseThrow(()->new NoSuchDataException(""));
        if (fields.getRegister().equals(account)) {
            fields= FieldsMapper.Instance.toEntity(fieldsDto);
        }else{
            throw new PermissionLimitedCustomException("권한이 없습니다.");
        }
        return this.getResource(fields, account);
    }

    @Override
    public void removeFields(Long id, Account account) {
        Fields fields = fieldsJpaRepository.findById(id).orElseThrow(()->new NoSuchDataException(""));
        if (fields.getRegister().equals(account)) {
           fields.removeField();
        }else{
            throw new PermissionLimitedCustomException("권한이 없습니다.");
        }
    }

    @Override
    public EntityModel createField(FieldsRequestDto fieldsDto, Account account) {
        fieldsDto.setRegister(account);
        fieldsDto.setCreateDate(LocalDateTime.now());


        Fields fields = fieldsJpaRepository.save(FieldsMapper.Instance.toEntity(fieldsDto));

        return this.getResource(fields, account);
    }


    private EntityModel getResource(Fields fields, Account account) {
        WebMvcLinkBuilder selfLink = linkTo(FieldsController.class).slash(fields.getId());
        EntityModel resource = EntityModel.of(fields);
        resource.add(selfLink.withRel("self"));
        if (fields.getRegister().equals(account)) {
            resource.add(selfLink.withRel("update-content"));
        }
        resource.add(Link.of("/docs/asciidoc/api.html#").withRel("profile"));
        return  resource;
    }
    private CollectionModel getResources(Page<Fields> fields, PagedResourcesAssembler<Fields> assembler) {
        var pageResources = assembler.toModel(fields, entity ->
                EntityModel.of(FieldsMapper.Instance.toDto(entity))
                        .add(linkTo(FieldsController.class).withRel("query-content"))
                        .add(linkTo(FieldsController.class).withSelfRel())
        );
        pageResources.add(Link.of("/docs/asciidoc/index.html#create-field-api").withRel("profile"));
        return pageResources;
    }
}
