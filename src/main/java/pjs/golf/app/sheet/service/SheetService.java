package pjs.golf.app.sheet.service;

import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.sheet.dto.SheetRequestDto;

import java.util.List;

public interface SheetService {
    List updateScore(SheetRequestDto sheetRequestDto, Game game, Member member);

    List nextRound(Member member, Game game, int round);
}
