package pjs.golf.app.sheet.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.game.service.GameService;
import pjs.golf.app.member.entity.Member;
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
    public List updateScore(SheetRequestDto sheetRequestDto, Game game, Member member) {

        if(member.equals(game.getHost())) {
            Sheet sheet = sheetJpaRepository.findByGameAndPlayerAndRound(game, sheetRequestDto.getPlayer(), sheetRequestDto.getRound())
                            .orElseThrow(()-> new NoSuchDataException(""));
            sheet.updateScore(sheetRequestDto.getScore());
            sheetJpaRepository.save(sheet);
        }
        return sheetJpaRepository.findAllByGame(game);
    }

    @Override
    public List nextRound(Member member, Game game, int round) {
        List<Member> players = game.getPlayers();
        if(game.getHost().equals(member)) {
            players.forEach(e -> {
                Sheet sheet = Sheet.builder()
                        .game(game)
                        .round(round)
                        .player(e)
                        .score(0).build();
                sheetJpaRepository.save(sheet);
            });
        }
        return sheetJpaRepository.findAllByGame(game);
    }


}
