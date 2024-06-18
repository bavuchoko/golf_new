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
import pjs.golf.common.exception.InCorrectStatusCustomException;
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
    public void nextRound(Account account, Game game) {
        List<Account> players = game.getPlayers();
        List<Sheet> sheets = game.getSheets();
        int currentRount = game.getRound();
        List currentRoundSheets = sheets.stream().map(e->e.getRound()==currentRount).toList();

        //todo 정책 : 모든 사람이 점수 입력 되어야만 다음 라운드로 진행할 수 있는지 vs 기권이나 중도 포기가 있을 수 있음.
        //todo 중간 라운드 부터 시작시 ex) 7 홀부터 시작시 7,8,9 다음 1,2,3... 해서 9홀을 채우는 로직 필요
//        if(currentRoundSheets.stream().anyMatch(e->((Sheet)e).getHit() <1))
//            throw new InCorrectStatusCustomException("점수가 입력되지 않은 사람이 있습니다.");

        if(game.getHost().equals(account)) {
            players.forEach(e -> {
                Sheet sheet = Sheet.builder()
                        .game(game)
                        .round(currentRount + 1)
                        .player(e)
                        .hit(0).build();
                sheetJpaRepository.save(sheet);
            });
        }
    }

    @Override
    public void startRound(Account account, Game game, int round) {
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

}
