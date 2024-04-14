package pjs.golf.app.game.dto;

import lombok.*;
import pjs.golf.app.fields.dto.FieldsResponseDto;
import pjs.golf.app.account.dto.AccountResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDto {
    private Long id;
    private int round;
    private AccountResponseDto host;
    private int startHole;
    private LocalDateTime playDate;
    private FieldsResponseDto field;
    private List<AccountResponseDto> players;
    private List<GameResponseDto> games;
    private GameStatus status;
    private boolean isRemoved;
}
