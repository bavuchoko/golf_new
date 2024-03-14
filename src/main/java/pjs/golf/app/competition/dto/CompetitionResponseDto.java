package pjs.golf.app.competition.dto;

import lombok.*;
import pjs.golf.app.fields.dto.FieldsResponseDto;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.account.dto.AccountResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionResponseDto {
    private Long id;
    private AccountResponseDto host;
    private LocalDateTime playDate;
    private FieldsResponseDto field;
    private List<AccountResponseDto> players;
    private List<GameResponseDto> games;
}
