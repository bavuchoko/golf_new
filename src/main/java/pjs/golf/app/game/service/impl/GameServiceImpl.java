package pjs.golf.app.game.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import pjs.golf.app.game.GameController;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.game.mapper.GameMapper;
import pjs.golf.app.game.repository.GameJpaRepository;
import pjs.golf.app.game.repository.querydsl.GameQuerydslSupport;
import pjs.golf.app.game.service.GameService;
import pjs.golf.app.member.entity.Member;
import pjs.golf.common.SearchDto;
import pjs.golf.common.exception.NoSuchDataException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameJpaRepository gameJpaRepository;
    private final GameQuerydslSupport gameQuerydslSupport;
    @Override
    public CollectionModel getGameList(SearchDto search, Pageable pageable, PagedResourcesAssembler<Game> assembler, Member member) {
        Page<Game> games = gameQuerydslSupport.getGameListBetweenDate(search, pageable, member, false);
    return  this.getPageResources(assembler, games, member);
    }

    @Override
    public GameResponseDto getGameInfo(String id) {
        Game game = gameJpaRepository.findById(Long.valueOf(id)).orElseThrow(()-> new NoSuchDataException("해당하는 데이터가 없습니다."));
        return GameMapper.Instance.toResponseDto(game);
    }

    @Override
    public GameResponseDto createGame(GameRequestDto gameRequestDto, Member member) {
        return null;
    }



    public CollectionModel getPageResources(PagedResourcesAssembler<Game> assembler, Page<Game> game, Member member) {
        return assembler.toModel(game, entity -> {
            EntityModel<GameResponseDto> entityModel = EntityModel.of(GameMapper.Instance.toResponseDto(entity))
                    .add(linkTo(GameController.class).slash(entity.getId()).withSelfRel());
            if (member != null && entity.getHost().equals(member)) {
                entityModel.add(linkTo(GameController.class).slash(entity.getId()).withRel("update"));
            }
            return entityModel.add(Link.of("/docs/asciidoc/index.html#create-game-api").withRel("profile"));
        });
    }
}
