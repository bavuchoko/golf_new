package pjs.golf.app.memo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pjs.golf.app.memo.dto.MemoRequestDto;
import pjs.golf.app.memo.entity.Memo;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {})
public interface MemoMapper {

    MemoMapper Instance = Mappers.getMapper(MemoMapper.class);

    Memo toEntity(MemoRequestDto memoRequestDto);
}
