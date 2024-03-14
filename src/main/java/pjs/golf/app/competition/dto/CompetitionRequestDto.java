package pjs.golf.app.competition.dto;

import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionRequestDto {
    private Long id;
    private Account host;
    private LocalDateTime playDate;
    private Fields field;
    private List<Account> players;
    private List<Game> games;
}
