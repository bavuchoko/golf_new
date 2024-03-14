package pjs.golf.app.memo.dto;

import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoRequestDto {
    private Long id;
    private String content;
    private Fields field;
    private int round;
    private Account account;
}
