package pjs.golf.app.memo;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.memo.dto.MemoRequestDto;
import pjs.golf.app.memo.service.MemoService;
import pjs.golf.common.CurrentUser;

import java.util.List;

@RestController
@RequestMapping(value = "/api/memo", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    /**
     * 메모 목록조회
     * */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity getFieldList(
            @PathVariable Long id,
            @CurrentUser Account account
            ) {
        List resources = memoService.getMemosByFieldAndUser(id, account);
        return new ResponseEntity(resources, HttpStatus.OK);
    }


    /**
     * 메모 등록
     * */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity createMemo(
            @RequestBody MemoRequestDto memoRequestDto,
            @CurrentUser Account account
    ) {
        List resources = memoService.createOrUpdateMemo(memoRequestDto, account);
        return new ResponseEntity(resources, HttpStatus.OK);
    }
    
    /**
     * 메모 수정
     * */
    @PutMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity updateMemo(
            @RequestBody MemoRequestDto memoRequestDto,
            @CurrentUser Account account
    ) {
        List resources = memoService.createOrUpdateMemo(memoRequestDto, account);
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
            @CurrentUser Account account
    ) {
        List resources = memoService.deleteMemo(memoId, fieldId, account);
        return new ResponseEntity(resources, HttpStatus.OK);
    }
    
}
