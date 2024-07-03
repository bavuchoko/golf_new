package pjs.golf.app.game.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;
import pjs.golf.common.SearchDto;

import java.util.List;

public interface GameService {
    CollectionModel getGameListResources(SearchDto search, Pageable pageable, PagedResourcesAssembler<Game> assembler, Account account);

    List getGameList();
    EntityModel getGameResource(Long id, Account account);

    Game createGame(GameRequestDto gameRequestDto, Account account);

    EntityModel enrollGame(Long id, Account account);

    EntityModel expelPlayer(Long id, Account account, Account target);

    void startGame(Long id, Account account,int course, int round, int hole) throws Exception;

    Game getGameInfo(Long id);

    void endGame(Long id, Account account);

    void removeGame(Long id, Account account);

    EntityModel getResource(Game game);

    void progressGame(Long gameId);
}
