package pjs.golf.app.game.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.entity.Member;
import pjs.golf.common.SearchDto;

public interface GameService {
    CollectionModel getGameList(SearchDto search, Pageable pageable, PagedResourcesAssembler<Game> assembler, Member member);


    EntityModel getGameResource(Long id, Member member);

    EntityModel createGame(GameRequestDto gameRequestDto, Member member);

    EntityModel enrollGame(Long id, Member member);

    void expelPlayer(Long id, Member member, Member target);

    void startGame(Long id, Member member, int round) throws Exception;

    Game getGameInfo(Long id);

    EntityModel endGame(Long id, Member member);

    void removeGame(Long id, Member member);
}
