package pjs.golf.app.memo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.memo.dto.MemoRequestDto;
import pjs.golf.app.memo.service.MemoService;
import pjs.golf.common.CurrentUser;
import pjs.golf.common.SearchDto;

import java.util.List;

@RestController
@RequestMapping(value = "/api/memo", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    /**
     * 메모 목록조회
     * */
    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity getFieldList(
            @PathVariable Long id,
            @CurrentUser Member member
            ) {
        List resources = memoService.getMemosByFieldAndUser(id, member);
        return new ResponseEntity(resources, HttpStatus.OK);
    }


    /**
     * 메모 등록
     * */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity createMemo(
            @RequestBody MemoRequestDto memoRequestDto,
            @CurrentUser Member member
    ) {
        List resources = memoService.createOrUpdateMemo(memoRequestDto, member);
        return new ResponseEntity(resources, HttpStatus.OK);
    }
    
    /**
     * 메모 수정
     * */
    @PutMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity updateMemo(
            @RequestBody MemoRequestDto memoRequestDto,
            @CurrentUser Member member
    ) {
        List resources = memoService.createOrUpdateMemo(memoRequestDto, member);
        return new ResponseEntity(resources, HttpStatus.OK);
    }

    /**
     * 메모 삭제
     * */
    @DeleteMapping("{memoId}/{fieldId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity deleteMemo(
            @PathVariable Long memoId,
            @PathVariable Long fieldId,
            @CurrentUser Member member
    ) {
        List resources = memoService.deleteMemo(memoId, fieldId, member);
        return new ResponseEntity(resources, HttpStatus.OK);
    }
    
}
