package pjs.golf.app.memo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.memo.dto.MemoRequestDto;
import pjs.golf.app.memo.mapper.MemoMapper;
import pjs.golf.app.memo.repository.MemoJpaRepository;
import pjs.golf.app.memo.service.MemoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MemoJpaRepository memoJpaRepository;
    @Override
    public List getMemosByFieldAndUser(Long id, Account account) {
        Fields fields = Fields.builder().id(id).build();
        return memoJpaRepository.findByFieldAndAccount(fields, account);
    }

    @Override
    public List createOrUpdateMemo(MemoRequestDto memoRequestDto, Account account) {
        memoJpaRepository.save(MemoMapper.Instance.toEntity(memoRequestDto));
        return memoJpaRepository.findByFieldAndAccount(memoRequestDto.getField(), account);
    }

    @Override
    public List deleteMemo(Long memoId,Long fieldId, Account account) {

        Fields fields = Fields.builder().id(fieldId).build();
        memoJpaRepository.deleteById(memoId);
        return memoJpaRepository.findByFieldAndAccount(fields, account);
    }
}
