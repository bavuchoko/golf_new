package pjs.golf.app.memo.service;

import pjs.golf.app.member.entity.Member;
import pjs.golf.app.memo.dto.MemoRequestDto;

import java.util.List;

public interface MemoService {
    List getMemosByFieldAndUser(Long id, Member member);


    List createOrUpdateMemo(MemoRequestDto memoRequestDto, Member member);

    List deleteMemo(Long memoId, Long fieldId, Member member);
}
