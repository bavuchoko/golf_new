package pjs.golf.app.competition.dto;

import lombok.*;
import pjs.golf.app.fields.dto.FieldsResponseDto;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.member.dto.MemberResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionResponseDto {
    private Long id;
    private MemberResponseDto host;
    private LocalDateTime playDate;
    private FieldsResponseDto field;
    private List<MemberResponseDto> players;
    private List<GameResponseDto> games;
}