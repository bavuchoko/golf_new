package pjs.golf.app.member.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pjs.golf.app.member.entity.Gender;
import pjs.golf.app.member.entity.MemberRole;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class MemberResponseDto {

    private Integer id;
    private String username;
    private String birth;
    private String password;
    private String name;
    private Gender gender;
    private String portrait;
    private LocalDateTime joinDate;
    private Set<MemberRole> roles;
}
