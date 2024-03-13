package pjs.golf.app.memo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.memo.dto.MemoRequestDto;
import pjs.golf.app.memo.entity.Memo;
import pjs.golf.app.memo.mapper.MemoMapper;
import pjs.golf.app.memo.repository.MemoJpaRepository;
import pjs.golf.app.memo.service.MemoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MemoJpaRepository memoJpaRepository;
    @Override
    public List getMemosByFieldAndUser(Long id, Member member) {
        Fields fields = Fields.builder().id(id).build();
        return memoJpaRepository.findByFieldAndMember(fields, member);
    }

    @Override
    public List createOrUpdateMemo(MemoRequestDto memoRequestDto, Member member) {
        memoJpaRepository.save(MemoMapper.Instance.toEntity(memoRequestDto));
        return memoJpaRepository.findByFieldAndMember(memoRequestDto.getField(), member);
    }

    @Override
    public List deleteMemo(Long memoId,Long fieldId, Member member) {

        Fields fields = Fields.builder().id(fieldId).build();
        memoJpaRepository.deleteById(memoId);
        return memoJpaRepository.findByFieldAndMember(fields, member);
    }
}
