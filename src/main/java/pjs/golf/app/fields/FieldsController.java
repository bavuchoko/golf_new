package pjs.golf.app.fields;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.fields.service.FieldsService;
import pjs.golf.app.account.entity.Account;
import pjs.golf.common.CurrentUser;
import pjs.golf.common.SearchDto;
import pjs.golf.common.WebCommon;
import pjs.golf.common.exception.NoSuchDataException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/field", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class FieldsController {

    private final FieldsService fieldService;

    /**
     * 필드 목록 조회
     * */
    @GetMapping
    public ResponseEntity getFieldList(
            Pageable pageable,
            @RequestParam(required = false) String searchTxt,
            PagedResourcesAssembler<Fields> assembler
    ){
        SearchDto search = SearchDto.builder().SearchTxt(searchTxt).build();
        CollectionModel resources = fieldService.getFieldList(search,pageable, assembler);
        return new ResponseEntity(resources, HttpStatus.OK);
    }

    /**
     * 필드 상세
     * */
    @GetMapping("/{id}")
    public ResponseEntity viewField(
            @PathVariable Long id,
            @CurrentUser Account account){

        try {
            EntityModel resource = fieldService.getFieldInfo(id, account);
            return new ResponseEntity(resource, HttpStatus.OK);
        } catch (NoSuchDataException e) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 필드 등록
     * @return 200 | 400
     * @param fieldsDto FieldDto
     * */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity creatField(
            @RequestBody FieldsRequestDto fieldsDto,
            Errors errors,
            @CurrentUser Account account){

        if (errors.hasErrors()) {
            return WebCommon.badRequest(errors, this.getClass());
        }
        try{
            EntityModel resource = fieldService.createField(fieldsDto, account);
            return new ResponseEntity(resource, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 필드 수정
     * */
    @PutMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity updatetField(
            @RequestBody FieldsRequestDto fieldsDto,
            Errors errors,
            @CurrentUser Account account){

        if (errors.hasErrors()) {
            return WebCommon.badRequest(errors, this.getClass());
        }

        try {
            EntityModel resource = fieldService.updateFields(fieldsDto, account);
            return new ResponseEntity(resource, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * 필드 삭제
     * */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity deleteField(
            @PathVariable Long id,
            @CurrentUser Account account){

        try {
            fieldService.removeFields(id, account);
            return new ResponseEntity(HttpStatus.OK);
        }catch (NoSuchDataException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }

}
