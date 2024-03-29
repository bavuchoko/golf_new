package pjs.golf.app.sheet;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.game.service.GameService;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.sheet.dto.SheetRequestDto;
import pjs.golf.app.sheet.service.SheetService;
import pjs.golf.common.CurrentUser;
import pjs.golf.common.exception.NoSuchDataException;

import java.util.List;

@RestController
@RequestMapping(value = "/api/sheet", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class SheetController {

    private final GameService gameService;
    private final SheetService sheetService;

    /**
     * 점수입력
     * */
    @PutMapping("/score/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity updateScore(
            @RequestBody SheetRequestDto sheetRequestDto,
            @CurrentUser Account account){
        try {
            Game game = gameService.getGameInfo(sheetRequestDto.getGame().getId());
            List list = sheetService.updateScore(sheetRequestDto, game, account);
            return new ResponseEntity(list,HttpStatus.OK);
        } catch (NoSuchDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 다음 라운드
     * */
    @PostMapping("/nextround/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity nextRound(
            @PathVariable Long id,
            int round,
            @CurrentUser Account account){
        try {
            Game game = gameService.getGameInfo(id);
            List list = sheetService.nextRound(account, game, round);
            return new ResponseEntity(list,HttpStatus.OK);
        } catch (NoSuchDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
