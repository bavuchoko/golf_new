package pjs.golf.app.sheet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pjs.golf.app.sheet.dto.SheetRequestDto;
import pjs.golf.app.sheet.entity.Sheet;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {})
public interface SheetMapper {

    SheetMapper Instance = Mappers.getMapper(SheetMapper.class);

    Sheet toEntity(SheetRequestDto sheetRequestDto);
}
