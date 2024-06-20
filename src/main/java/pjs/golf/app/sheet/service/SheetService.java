package pjs.golf.app.sheet.service;

import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.sheet.dto.SheetRequestDto;

public interface SheetService {
    void updateScore(SheetRequestDto sheetRequestDto, Game game, Account account);

    void progressRound(Long accountId, Long gameId);
    void progressRound(Long accountId, Long gameId, int round);
}
