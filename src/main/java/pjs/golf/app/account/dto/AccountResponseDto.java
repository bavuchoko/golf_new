package pjs.golf.app.account.dto;


import lombok.*;
import pjs.golf.app.account.entity.Gender;
import pjs.golf.app.account.entity.AccountRole;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDto {

    private Integer id;
    private String username;
    private String birth;
    private String password;
    private String name;
    private Gender gender;
    private String portrait;
    private LocalDateTime joinDate;
    private Set<AccountRole> roles;
}
