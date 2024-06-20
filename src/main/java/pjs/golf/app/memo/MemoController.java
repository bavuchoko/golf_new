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
    @GetMapping("/{fieldId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity getFieldList(
            @PathVariable("fieldId") Long fieldId,
            @CurrentUser Account account
            ) {
        List resources = memoService.getMemosByFieldAndUser(fieldId, account);
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
        List resources = memoService.createMemo(memoRequestDto, account);
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
        List resources = memoService.updateMemo(memoRequestDto, account);
        return new ResponseEntity(resources, HttpStatus.OK);
    }

    /**
     * 메모 삭제
     * */
    @DeleteMapping("{fieldId}/{round}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity deleteMemo(
            @PathVariable Long fieldId,
            @PathVariable int round,
            @CurrentUser Account account
    ) {
        List resources = memoService.deleteMemo(account, fieldId, round);
        return new ResponseEntity(resources, HttpStatus.OK);
    }
    
}
