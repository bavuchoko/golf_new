package pjs.golf.app.memo.service;

import pjs.golf.app.account.entity.Account;
import pjs.golf.app.memo.dto.MemoRequestDto;

import java.util.List;

public interface MemoService {
    List getMemosByFieldAndUser(Long id, Account account);


    List createOrUpdateMemo(MemoRequestDto memoRequestDto, Account account);

    List deleteMemo(Long memoId, Long fieldId, Account account);
}
