package pjs.golf.app.sheet.service;

import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.sheet.dto.SheetRequestDto;

import java.util.List;

public interface SheetService {
    void updateScore(SheetRequestDto sheetRequestDto, Game game, Account account);

    void startRound(Account account, Game game, int round);
    void nextRound(Account account, Game game);
}
