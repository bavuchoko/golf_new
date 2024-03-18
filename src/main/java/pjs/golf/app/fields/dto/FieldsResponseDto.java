package pjs.golf.app.fields.dto;

import lombok.*;
import pjs.golf.app.account.dto.AccountResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldsResponseDto {

    private Long id;
    private String name;
    private String address;
    private String addressDetail;
    private int courses;
    private AccountResponseDto register;
    private LocalDateTime createDate;
    private boolean isRemoved;
}
