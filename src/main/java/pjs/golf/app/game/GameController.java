package pjs.golf.app.game;


import io.netty.util.internal.StringUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameStatus;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.game.service.GameService;
import pjs.golf.app.account.entity.Account;
import pjs.golf.common.CurrentUser;
import pjs.golf.common.SearchDto;
import pjs.golf.common.WebCommon;
import pjs.golf.common.exception.AlreadyExistSuchDataCustomException;
import pjs.golf.common.exception.InCorrectStatusCustomException;
import pjs.golf.common.exception.NoSuchDataException;
import pjs.golf.common.exception.PermissionLimitedCustomException;
import pjs.golf.common.sse_connection.SseEmitterService;
import pjs.golf.config.token.TokenManagerImpl;
import pjs.golf.config.token.TokenType;
import pjs.golf.config.utils.CookieUtil;

@RestController
@RequestMapping(value = "/api/game", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final SseEmitterService sseEmitterService;
    private final AccountService accountService;
    private final Logger log = LoggerFactory.getLogger(GameController.class);

    /**
     * 목록조회
     * */
    @GetMapping
    public ResponseEntity getGameList(
            Pageable pageable,
            @CurrentUser Account account,
            @RequestParam(required = false, name = "startDate") String startDate,
            @RequestParam(required = false,  name = "endDate") String endDate,
            PagedResourcesAssembler<Game> assembler
    ) {
        SearchDto search = SearchDto.builder()
                .startDate((WebCommon.localDateToLocalDateTime(startDate,"startDate")))
                .endDate((WebCommon.localDateToLocalDateTime(endDate,"endDate")))
                .build();
        CollectionModel resources =  gameService.getGameListResources(search, pageable, assembler, account);
        return new ResponseEntity(resources, HttpStatus.OK);
    }

    /**
     * 단일조회
     * */
    @GetMapping( value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getGame(
            @PathVariable("id") Long id,
            @CurrentUser Account account,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            String jwt = request.getHeader("Authorization");
            String accessToken =null;
            if(account == null && StringUtils.hasText(jwt))
                accessToken= accountService.reIssueToken(request, response);

            EntityModel game = gameService.getGameResource(id, account);

            SseEmitter subscribe = sseEmitterService.subscribe(id, account, game);
            return new ResponseEntity(subscribe, HttpStatus.OK);
        } catch (NoSuchDataException e) {
            return new ResponseEntity(null, HttpStatus.NO_CONTENT);
        }

    }

    /**
     * 경기생성
     * */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity hostGame(
            @RequestBody GameRequestDto gameRequestDto,
            Errors errors,
            @CurrentUser Account account
            ) {

        if (errors.hasErrors()) {
            return WebCommon.badRequest(errors, this.getClass());
        }
        try {
            gameRequestDto.setStatus(GameStatus.OPEN);
            Game game = gameService.createGame(gameRequestDto, account);
            EntityModel resource = gameService.getResource(game);
            return new ResponseEntity(resource, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 빠른 경기생성
     * */
    @PostMapping("/quick")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity quickGame(
            @RequestBody GameRequestDto gameRequestDto,
            Errors errors,
            @CurrentUser Account account
    ) {

        if (errors.hasErrors()) {
            return WebCommon.badRequest(errors, this.getClass());
        }
        try {
            Game game = gameService.createGame(gameRequestDto, account);
            gameService.startGame(game.getId(), account, 1, 1);
            EntityModel resource = gameService.getResource(game);
            return new ResponseEntity(resource, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 경기삭제
     * */
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity deleteGame(
            @PathVariable("id") Long id,
            @CurrentUser Account account
    ) {
        gameService.removeGame(id, account);
        return ResponseEntity.ok().build();
    }

    /**
     * 참가하기
     */
    @PutMapping("/enroll/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity joinGame(
            @PathVariable("id") Long id,
            @CurrentUser Account account) {
        try {
            EntityModel resource = gameService.enrollGame(id, account);
            return ResponseEntity.ok().body(resource);
        } catch (AlreadyExistSuchDataCustomException | InCorrectStatusCustomException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 경기퇴장
     */
    @PutMapping("/expel/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity expelPlayer(
            @PathVariable("id") Long id,
            @CurrentUser Account account,
            @RequestBody Account target) {
        if (account == null) {
            return ResponseEntity.badRequest().body("로그인이 필요 합니다.");
        }
        try {
            gameService.expelPlayer(id, account, target);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 경기시작
     */
    @PutMapping("/play/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity startGame(
            @PathVariable("id") Long id,
            @RequestParam("startHole") int startHole,
            @CurrentUser Account account
    ) {
        try {
            gameService.startGame(id, account, 1, startHole);
            EntityModel resource = gameService.getGameResource(id, account);
            sseEmitterService.broadcast(id, resource);
            return new  ResponseEntity(resource, HttpStatus.OK);
        } catch (PermissionLimitedCustomException | InCorrectStatusCustomException | NoSuchDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 경기종료
     */
    @PutMapping("/end/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity ebdGame(
            @PathVariable("id") Long id,
            @CurrentUser Account account
    ) {
        try {
            EntityModel resource = gameService.endGame(id, account);
            return new ResponseEntity(resource,HttpStatus.OK);
        } catch (PermissionLimitedCustomException | NoSuchDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
