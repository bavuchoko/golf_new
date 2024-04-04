package pjs.golf.app.fields.dto;

import lombok.*;
import pjs.golf.app.account.entity.Account;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldsRequestDto {
    private Long id;
    private String name;
    private String address;
    private String addressDetail;
    private String city;
    private String latitude;
    private String longitude;
    private int courses;
    private Account register;
    private LocalDateTime createDate;
    private boolean isRemoved;
}
