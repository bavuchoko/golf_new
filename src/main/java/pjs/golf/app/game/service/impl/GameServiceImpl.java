package pjs.golf.app.game.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import pjs.golf.app.game.GameController;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.game.dto.GameStatus;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.game.mapper.GameMapper;
import pjs.golf.app.game.repository.GameJpaRepository;
import pjs.golf.app.game.repository.querydsl.GameQuerydslSupport;
import pjs.golf.app.game.service.GameService;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.member.service.MemberService;
import pjs.golf.common.SearchDto;
import pjs.golf.common.exception.NoSuchDataException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameJpaRepository gameJpaRepository;
    private final GameQuerydslSupport gameQuerydslSupport;
    private final MemberService memberService;

    @Override
    public CollectionModel getGameList(SearchDto search, Pageable pageable, PagedResourcesAssembler<Game> assembler, Member member) {
        Page<Game> games = gameQuerydslSupport.getGameListBetweenDate(search, pageable, member, false);
    return  this.getPageResources(assembler, games, member);
    }

    @Override
    public EntityModel getGameInfo(Long id, Member member) {
        Game game = gameJpaRepository.findById(id).orElseThrow(()-> new NoSuchDataException("해당하는 데이터가 없습니다."));
        return getResource(game, member);
    }

    @Override
    public EntityModel createGame(GameRequestDto gameRequestDto, Member member) {
        List<Member> players = this.intiPlayer(gameRequestDto, member);
        gameRequestDto.setHost(member);
        gameRequestDto.setPlayDate(LocalDateTime.now());
        gameRequestDto.setStatus(GameStatus.OPEN);
        gameRequestDto.setPlayers(players);
        Game game = gameJpaRepository.save(GameMapper.Instance.toEntity(gameRequestDto));
        return getResource(game, member);
    }

    private List<Member> intiPlayer(GameRequestDto gameRequestDto, Member member) {
        String[] names = gameRequestDto.getNames();
        //기존에 temp_이름 으로 존재하는 Account 엔티티들
        List<Member> registeredAccounts = memberService.getTempUsersByUserNames(Arrays.asList(names));

        //등록되어 있지 않은 이름 -> temp_이름 으로 Account 생성해주어야 할 이름들
        List<String> unRegisteredUsernames =  Arrays.stream(names).filter(
                name -> registeredAccounts.stream()
                        .noneMatch(account -> account.getName().equals(name))).collect(Collectors.toList());

        //unRegisteredUsernames 로 새로 생성해준 Account 들
        List<Member> newAccount = memberService.createUserIfDosenExist(unRegisteredUsernames);

        //등록 & 생성 된 account 들 합침.
        registeredAccounts.addAll(newAccount);
        //클라이언트가 입력한 이름 순서대로 정렬을 편하게 하기 위해 registeredAccounts 리스트를 이름을 기준으로 매핑한 맵 생성
        Map<String, Member> accountMap = registeredAccounts.stream()
                .collect(Collectors.toMap(Member::getName, account -> account));

        //처음 입력한 순서대로 정렬해줌. :: Account 객체이며 클라이언트가 입력한 순서대로 정렬된 리스트
        List<Member> players = Arrays.stream(names)
                .map(accountMap::get).collect(Collectors.toList());

        //등록자 자동 참가 => 입력한 이름중 첫번째 사람을 제외하지 않으면
        if(players.size() < 4){
            players = players.subList(1,players.size());
            players.add(0, member);
        }else if(players.size() >= 4){
            players = players.subList(1,4);
            players.add(0, member);
        }
        return players;
    }


    public EntityModel getResource(Game game, Member member) {
        GameResponseDto gameResponseDto = GameMapper.Instance.toResponseDto(game);
        WebMvcLinkBuilder selfLink = linkTo(GameController.class).slash(gameResponseDto.getId());
        EntityModel resource = EntityModel.of(gameResponseDto);
        if(gameResponseDto.getHost().equals(member)){
            resource.add(linkTo(GameController.class).slash("score").withRel("update"));
        }
        resource.add(selfLink.withRel("query-content"));

        return resource;
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
