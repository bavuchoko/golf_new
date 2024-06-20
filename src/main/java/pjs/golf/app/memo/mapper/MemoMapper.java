package pjs.golf.app.memo.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pjs.golf.app.account.mapper.AccountMapper;
import pjs.golf.app.fields.mapper.FieldsMapper;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.memo.dto.MemoRequestDto;
import pjs.golf.app.memo.dto.MemoResponseDto;
import pjs.golf.app.memo.entity.Memo;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {AccountMapper.class, FieldsMapper.class})
public interface MemoMapper {

    MemoMapper Instance = Mappers.getMapper(MemoMapper.class);

    Memo toEntity(MemoRequestDto memoRequestDto);


    @Named("toMemoDto")
    @Mapping(source = "account", target = "account", qualifiedByName = "accountResponse")
    @Mapping(source = "field", target = "field", qualifiedByName = "toFieldDto")
    MemoResponseDto toMemoDto(Memo memoRequestDto);


    @Named("toResponseDtoList")
    @IterableMapping(qualifiedByName = "toMemoDto")
    List<MemoResponseDto> toResponseDtoList(List<Memo> list);
}
