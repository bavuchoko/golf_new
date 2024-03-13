package pjs.golf.app.game.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import pjs.golf.app.sheet.service.SheetService;
import pjs.golf.common.SearchDto;
import pjs.golf.common.exception.AlreadyExistSuchDataCustomException;
import pjs.golf.common.exception.InCorrectStatusCustomException;
import pjs.golf.common.exception.NoSuchDataException;
import pjs.golf.common.exception.PermissionLimitedCustomException;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final SheetService sheetService;

    @Override
    public CollectionModel getGameList(SearchDto search, Pageable pageable, PagedResourcesAssembler<Game> assembler, Member member) {
        Page<Game> games = gameQuerydslSupport.getGameListBetweenDate(search, pageable, member, false);
    return  this.getPageResources(assembler, games, member);
    }

    @Override
    public EntityModel getGameResource(Long id, Member member) {
        Game game = gameJpaRepository.findById(id).orElseThrow(()-> new NoSuchDataException("해당하는 데이터가 없습니다."));
        return getResource(game, member);
    }
    @Override
    public Game getGameInfo(Long id) {
        return gameJpaRepository.findById(id).orElseThrow(()-> new NoSuchDataException("해당하는 데이터가 없습니다."));
    }

    @Override
    public EntityModel endGame(Long id, Member member) {
        Game game = gameJpaRepository.findById(id).orElseThrow(()-> new NoSuchDataException("해당하는 데이터가 없습니다."));
        if(member.equals(game.getHost())){
            game.changeStatus(GameStatus.END);
        }else {
            throw new PermissionLimitedCustomException("권한이 없습니다.");
        }
        return getResource(game, member);
    }

    @Override
    @Transactional
    public void removeGame(Long id, Member member) {
        Game game = gameJpaRepository.findById(id).orElseThrow(()-> new NoSuchDataException("해당하는 데이터가 없습니다."));
        if(member.equals(game.getHost())){
            game.removeGame();
        }else {
            throw new PermissionLimitedCustomException("권한이 없습니다.");
        }
    }

    @Override
    public EntityModel createGame(GameRequestDto gameRequestDto, Member member) {
        List<Member> players = this.intiPlayer(gameRequestDto, member);
        gameRequestDto.setPlayers(players);

        gameRequestDto.setHost(member);
        gameRequestDto.setPlayDate(LocalDateTime.now());
        gameRequestDto.setStatus(GameStatus.OPEN);
        Game game = gameJpaRepository.save(GameMapper.Instance.toEntity(gameRequestDto));
        return getResource(game, member);
    }

    @Override
    public EntityModel enrollGame(Long id, Member member) {
        Game game= gameJpaRepository.findById(id).orElseThrow(()->new NoSuchDataException("") );
        if(game.getPlayers().size()<4 && game.getStatus().equals(GameStatus.OPEN)) {
            List players = game.getPlayers();
            players.stream().filter(player -> player.equals(member))
                    .findFirst()
                    .ifPresent(p -> {
                        throw new AlreadyExistSuchDataCustomException("이미 참가중입니다.");
                    });
            game.enrollGame(game, member);
        }else {
            throw new InCorrectStatusCustomException("요청을 처리할 수 없습니다.");
        }

        return  this.getResource(game);
    }


    @Override
    @Transactional
    public void expelPlayer(Long id, Member member, Member target) {
        Game entity = gameJpaRepository.findById(id).orElseThrow(
                ()-> new NoSuchDataException("")
        );
        if(GameStatus.OPEN != entity.getStatus()){
            throw new InCorrectStatusCustomException("statusNotAllowedException");
        }
        if(entity.getHost().equals(target)){
            throw new InCorrectStatusCustomException("hostExpelException");
        }
        if(entity.getHost().equals(member)) {
            entity.getPlayers().remove(target);
        }else{
            if(!member.equals(target))  throw new InCorrectStatusCustomException("selfExpelAllowedException");
            else entity.getPlayers().remove(target);
        }
    }

    @Override
    public void startGame(Long id, Member member, int round) throws Exception {
        Game gameEntity = gameJpaRepository.findById(id).orElseThrow(()->
                new NoSuchDataException("없는 데이터")
        );
        try {
            if(gameEntity.getPlayers().size()>1){
                if (gameEntity.getHost().equals(member)) {
                    gameEntity.changeStatus(GameStatus.PLAYING);
                    sheetService.nextRound(member, gameEntity, round);
                } else {
                    throw new PermissionLimitedCustomException("권한이 없습니다.");
                }
            }else{
                throw new InCorrectStatusCustomException("플레이어가 없습니다.");
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }

    private List<Member> intiPlayer(GameRequestDto gameRequestDto, Member member) {
        String[] names = gameRequestDto.getNames(); // ** host는 names에 포함되어 있으면 안된다.
        List<Member> players = new ArrayList<>();
        if(names.length>3){
            throw new IllegalArgumentException("최대 4명이 경기할 수 있습니다.");
        }
        if(names.length>0) {    // 경기 생성시 참가지 이름 입력한 경우

            // 1. 기존에 temp_이름 으로 존재하는 계정들
            List<Member> registeredAccounts = memberService.getTempUsersByUserNames(Arrays.asList(names));

            // 2. temp_이름 으로 계정등록 안된 이름들
            List<String> unRegisteredUsernames = Arrays.stream(names).filter(
                    name -> registeredAccounts.stream()
                            .noneMatch(account -> account.getName().equals(name))).collect(Collectors.toList());

            // 2-1.새롭게 temp_이름으로 계정 생성함
            List<Member> newAccount = memberService.createUserIfDosenExist(unRegisteredUsernames);

            // 3. 기존에 있거 새로 생성된 temp_이름 계청 합침.
            registeredAccounts.addAll(newAccount);

            // 4. 이름:계정 을 key:value 의 map으로 구성
            Map<String, Member> accountMap = registeredAccounts.stream()
                    .collect(Collectors.toMap(Member::getName, account -> account));

            // 5. 처음 입력한 name 배열의 순서대로 accountMap 에서 계정을 꺼냄
             players = Arrays.stream(names)
                    .map(accountMap::get).collect(Collectors.toList());

            players.add(0, member);
        }else{          //참가자 입력 없이 생성자 혼자 경기 생성한 경우
            players.add(member);
        }
        return players;

    }


    public EntityModel getResource(Game game, Member member) {
        GameResponseDto gameResponseDto = GameMapper.Instance.toResponseDto(game);
        WebMvcLinkBuilder selfLink = linkTo(GameController.class).slash(gameResponseDto.getId());
        EntityModel resource = EntityModel.of(gameResponseDto);
        if(member != null && gameResponseDto.getHost().equals(member)){
            resource.add(linkTo(GameController.class).slash("score").withRel("update"));
        }
        resource.add(selfLink.withRel("query-content"));

        return resource;
    }

    public EntityModel getResource(Game game) {
        GameResponseDto gameResponseDto = GameMapper.Instance.toResponseDto(game);
        WebMvcLinkBuilder selfLink = linkTo(GameController.class).slash(gameResponseDto.getId());
        EntityModel resource = EntityModel.of(gameResponseDto);
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
