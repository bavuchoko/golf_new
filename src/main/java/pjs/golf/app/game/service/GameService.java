package pjs.golf.app.game.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.entity.Member;
import pjs.golf.common.SearchDto;

import java.util.List;

public interface GameService {
    CollectionModel getGameList(SearchDto search, Pageable pageable, PagedResourcesAssembler<Game> assembler, Member member);


    GameResponseDto getGameInfo(String id);

    GameResponseDto createGame(GameRequestDto gameRequestDto, Member member);
}
