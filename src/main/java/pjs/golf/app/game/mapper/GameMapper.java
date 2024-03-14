package pjs.golf.app.game.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.mapper.AccountMapper;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AccountMapper.class})
public interface GameMapper {

    GameMapper Instance = Mappers.getMapper(GameMapper.class);

    Game toEntity(GameRequestDto gameRequestDto);


    @Named("gameResponse")
    @Mapping(source = "host", target = "host", qualifiedByName = "accountResponse")
    GameResponseDto toResponseDto(Game game);


    @IterableMapping(qualifiedByName = "gameResponse")
    default Page<GameResponseDto> toResponseDtoPage(Page<Game> list) {
        return list.map(this::toResponseDto);
    }

    @IterableMapping(qualifiedByName = "gameResponse")
    List<GameResponseDto> toResponseDtoList(List<Game> list);
}
