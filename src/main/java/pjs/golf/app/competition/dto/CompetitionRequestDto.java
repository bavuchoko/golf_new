package pjs.golf.app.competition.dto;

import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionRequestDto {
    private Long id;
    private Member host;
    private LocalDateTime playDate;
    private Fields field;
    private List<Member> players;
    private List<Game> games;
}
