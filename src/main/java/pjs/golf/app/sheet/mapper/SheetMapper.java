package pjs.golf.app.sheet.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pjs.golf.app.account.mapper.AccountMapper;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.sheet.dto.SheetRequestDto;
import pjs.golf.app.sheet.dto.SheetResponseDto;
import pjs.golf.app.sheet.entity.Sheet;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {AccountMapper.class})
public interface SheetMapper {

    SheetMapper Instance = Mappers.getMapper(SheetMapper.class);

    Sheet toEntity(SheetRequestDto sheetRequestDto);

    @Named("sheetResponse")
    @Mapping(source = "game", target = "game", qualifiedByName = "toResponseGameOfSheet")
    @Mapping(source = "player", target = "player", qualifiedByName = "accountResponse")
    SheetResponseDto toResponseDto(Sheet sheet);

    @Named("sheetResponseList")
    @IterableMapping(qualifiedByName = "sheetResponse")
    List<SheetResponseDto> toResponseList(List<Sheet> sheet);

    @Named("toResponseGameOfSheet")
    @Mapping(source = "host", target = "host", ignore = true)
    @Mapping(source = "field", target = "field", ignore = true)
    @Mapping(source = "players", target = "players", ignore = true)
    @Mapping(source = "sheets", target = "sheets", ignore = true)
    GameResponseDto toResponseGameOfSheet(Game game);
}
