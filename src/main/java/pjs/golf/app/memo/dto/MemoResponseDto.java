package pjs.golf.app.memo.dto;

import lombok.*;
import pjs.golf.app.fields.dto.FieldsResponseDto;
import pjs.golf.app.account.dto.AccountResponseDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoResponseDto {
    private Long id;
    private String content;
    private FieldsResponseDto field;
    private int round;
    private AccountResponseDto account;
}
