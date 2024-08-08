package pjs.golf.app.game.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import pjs.golf.app.fields.dto.FieldsResponseDto;
import pjs.golf.app.account.dto.AccountResponseDto;
import pjs.golf.app.sheet.dto.SheetResponseDto;

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
    private int hole;
    private int course;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playDate;
    private FieldsResponseDto field;
    private List<AccountResponseDto> players;
    private List<SheetResponseDto> sheets;
    private GameStatus status;
}
