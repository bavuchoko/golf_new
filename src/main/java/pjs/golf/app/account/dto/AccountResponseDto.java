package pjs.golf.app.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pjs.golf.app.account.entity.Gender;
import pjs.golf.app.account.entity.AccountRole;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
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
