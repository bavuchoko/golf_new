package pjs.golf.app.sheet.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pjs.golf.app.account.repository.AccountJpaRepository;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.game.repository.GameJpaRepository;
import pjs.golf.app.game.service.GameService;
import pjs.golf.app.sheet.dto.SheetRequestDto;
import pjs.golf.app.sheet.entity.Sheet;
import pjs.golf.app.sheet.repository.SheetJpaRepository;
import pjs.golf.app.sheet.service.SheetService;
import pjs.golf.common.exception.InCorrectStatusCustomException;
import pjs.golf.common.exception.NoSuchDataException;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class SheetServiceImpl implements SheetService {

    private final SheetJpaRepository sheetJpaRepository;
    private final GameJpaRepository gameJpaRepository;
    private final AccountJpaRepository accountJpaRepository;


    @Override
    @Transactional
    public void updateScore(SheetRequestDto sheetRequestDto, Game game, Account account) {
            Sheet sheet = sheetJpaRepository.findByGameAndPlayerAndRound(game, account, sheetRequestDto.getRound())
                    .orElseThrow(() -> new NoSuchDataException(""));
        //요청된 점수가 동일하면 굳이 업데이트 할 필요가 없음
        if(sheet.getHit() != sheetRequestDto.getHit()) {
            if (account.equals(game.getHost())) {
                sheet.updateHit(sheetRequestDto.getHit());
            }
        }
    }


    @Override
    public void progressRound(Long accountId, Long gameId, int round) {
        Game game = gameJpaRepository.findById(gameId).orElseThrow(()->new NoSuchDataException("해당경기 없음"));
        Account account = accountJpaRepository.findById(accountId).orElseThrow(()->new NoSuchDataException("해당유저 없음"));
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
    }
    @Override
    public void progressRound(Long accountId, Long gameId) {
        Game game = gameJpaRepository.findById(gameId).orElseThrow(()->new NoSuchDataException("해당경기 없음"));
        Account account = accountJpaRepository.findById(accountId).orElseThrow(()->new NoSuchDataException("해당유저 없음"));

        int nextRound = game.getRound()+1;

        List<Account> players = game.getPlayers();
        if(game.getHost().equals(account)) {
            players.forEach(e -> {
                Sheet sheet = Sheet.builder()
                        .game(game)
                        .round(nextRound)
                        .player(e)
                        .hit(0).build();
                sheetJpaRepository.save(sheet);
            });
        }
    }

}
