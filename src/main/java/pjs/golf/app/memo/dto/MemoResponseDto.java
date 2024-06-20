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
    private int round;
    private FieldsResponseDto field;
    private AccountResponseDto account;
    private String content;
}
