package pjs.golf.app.member.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pjs.golf.app.member.entity.Gender;
import pjs.golf.app.member.entity.MemberRole;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {

    private Integer id;
    @NotBlank(message = "아이디는 필수값입니다.")
    private String username;
    private String birth;
    @NotBlank(message = "비밀번호는 필수값입니다.")
    private String password;
    @NotBlank(message = "이름은 필수값입니다.")
    private String name;
    private Gender gender;
    private String portrait;
    private LocalDateTime joinDate;
    private Set<MemberRole> roles;

}
