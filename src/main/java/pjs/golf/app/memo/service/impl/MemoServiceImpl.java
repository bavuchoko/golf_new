package pjs.golf.app.memo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import pjs.golf.app.fields.FieldsController;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.fields.mapper.FieldsMapper;
import pjs.golf.app.game.GameController;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.game.mapper.GameMapper;
import pjs.golf.app.memo.dto.MemoRequestDto;
import pjs.golf.app.memo.entity.Memo;
import pjs.golf.app.memo.mapper.MemoMapper;
import pjs.golf.app.memo.repository.MemoJpaRepository;
import pjs.golf.app.memo.service.MemoService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MemoJpaRepository memoJpaRepository;
    @Override
    public List getMemosByFieldAndUser(Long id, Account account) {
        Fields fields = Fields.builder().id(id).build();
        return getResources(memoJpaRepository.findByFieldAndAccount(fields, account));
    }

    @Override
    public List createOrUpdateMemo(MemoRequestDto memoRequestDto, Account account) {
        memoRequestDto.setAccount(account);
        memoJpaRepository.save(MemoMapper.Instance.toEntity(memoRequestDto));
        return getResources(memoJpaRepository.findByFieldAndAccount(memoRequestDto.getField(), account));
    }

    @Override
    public List deleteMemo(Long memoId,Long fieldId, Account account) {

        Fields fields = Fields.builder().id(fieldId).build();
        memoJpaRepository.deleteById(memoId);
        return getResources(memoJpaRepository.findByFieldAndAccount(fields, account));
    }


    private List getResources(List<Memo> list) {
       return MemoMapper.Instance.toResponseDtoList(list);
    }
}
