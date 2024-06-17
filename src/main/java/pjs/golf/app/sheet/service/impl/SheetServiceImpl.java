package pjs.golf.app.sheet.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.sheet.dto.SheetRequestDto;
import pjs.golf.app.sheet.entity.Sheet;
import pjs.golf.app.sheet.repository.SheetJpaRepository;
import pjs.golf.app.sheet.service.SheetService;
import pjs.golf.common.exception.NoSuchDataException;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class SheetServiceImpl implements SheetService {

    private final SheetJpaRepository sheetJpaRepository;


    @Override
    @Transactional
    public void updateScore(SheetRequestDto sheetRequestDto, Game game, Account account) {
            Sheet sheet = sheetJpaRepository.findById(sheetRequestDto.getId())
                    .orElseThrow(() -> new NoSuchDataException(""));
        //요청된 점수가 동일하면 굳이 업데이트 할 필요가 없음
        if(sheet.getHit() != sheetRequestDto.getHit()) {
            if (account.equals(game.getHost())) {
                sheet.updateHit(sheetRequestDto.getHit());
            }
        }
    }

    @Override
    public List nextRound(Account account, Game game, int round) {
        List<Account> players = game.getPlayers();
        if(game.getHost().equals(account)) {
            players.forEach(e -> {
                Sheet sheet = Sheet.builder()
                        .game(game)
                        .round(round)
                        .player(e)
                        .hit(0).build();
                sheetJpaRepository.save(sheet);
            });
        }
        return sheetJpaRepository.findAllByGame(game);
    }


}
