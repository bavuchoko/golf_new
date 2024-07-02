package pjs.golf.app.memo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoRequestDto {
    private Fields field;
    private Account account;
    private int hole;
    private int course;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
