package pjs.golf.app.game;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.game.service.GameService;
import pjs.golf.app.member.entity.Member;
import pjs.golf.common.CurrentUser;
import pjs.golf.common.SearchDto;
import pjs.golf.common.WebCommon;
import pjs.golf.common.exception.InCorrectStatusCustomException;
import pjs.golf.common.exception.NoSuchDataException;
import pjs.golf.common.exception.PermissionLimitedCustomException;

@RestController
@RequestMapping(value = "/api/game", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;


    /**
     * 목록조회
     * */
    @GetMapping
    public ResponseEntity getGameList(
            Pageable pageable,
            @CurrentUser Member member,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            PagedResourcesAssembler<Game> assembler
    ) {
        SearchDto search = SearchDto.builder()
                .startDate((WebCommon.localDateToLocalDateTime(startDate,"startDate")))
                .endDate((WebCommon.localDateToLocalDateTime(endDate,"endDate")))
                .build();
        CollectionModel resources =  gameService.getGameList(search, pageable, assembler, member);
        return new ResponseEntity(resources, HttpStatus.OK);
    }

    /**
     * 단일조회
     * */
    @GetMapping("{id}")
    public ResponseEntity getGame(@PathVariable Long id, @CurrentUser Member member) {
        try {
            EntityModel game = gameService.getGameResource(id, member);
            return new ResponseEntity(game, HttpStatus.OK);
        } catch (NoSuchDataException e) {
            return new ResponseEntity(e, HttpStatus.NO_CONTENT);
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
            @CurrentUser Member member
            ) {

        if (errors.hasErrors()) {
            return WebCommon.badRequest(errors, this.getClass());
        }
        EntityModel resource = gameService.createGame(gameRequestDto, member);
        return new ResponseEntity(resource, HttpStatus.OK);
    }

    /**
     * 경기삭제
     * */
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity deleteGame() {
        return null;
    }

    /**
     * 참가하기
     */
    @PutMapping("/enroll/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity joinGame(
            @PathVariable Long id,
            @CurrentUser Member member) {
        try {
            EntityModel resource = gameService.enrollGame(id, member);

            return ResponseEntity.ok().body(resource); // 200
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 경기퇴장
     */
    @PutMapping("/expel/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity expelPlayer(
            @PathVariable Long id,
            @CurrentUser Member member,
            @RequestBody Member target) {
        if (member == null) {
            return ResponseEntity.badRequest().body("로그인이 필요 합니다.");
        }
        try {

            EntityModel resource = gameService.getGameResource(id, member);
            gameService.expelPlayer(id, member, target);
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
            @PathVariable Long id,
            @CurrentUser Member member
    ) {
        try {
            gameService.startGame(id, member, 1);

            EntityModel resource = gameService.getGameResource(id, member);

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
            @PathVariable Long id,
            @CurrentUser Member member
    ) {
        try {
            EntityModel resource = gameService.endGame(id, member);
            return new ResponseEntity(resource,HttpStatus.OK);
        } catch (PermissionLimitedCustomException | NoSuchDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
