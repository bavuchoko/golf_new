package pjs.golf.app.sheet.service;

import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.sheet.dto.SheetRequestDto;

import java.util.List;

public interface SheetService {
    List updateScore(SheetRequestDto sheetRequestDto, Game game, Account account);

    List nextRound(Account account, Game game, int round);
}
