package pjs.golf.app.fields.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.member.dto.MemberResponseDto;
import pjs.golf.app.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

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
    private MemberResponseDto register;
    private LocalDateTime playDate;
}
