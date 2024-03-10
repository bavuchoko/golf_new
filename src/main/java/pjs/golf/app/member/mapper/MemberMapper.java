package pjs.golf.app.member.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pjs.golf.app.member.dto.MemberRequestDto;
import pjs.golf.app.member.dto.MemberResponseDto;
import pjs.golf.app.member.entity.Member;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {})
public interface MemberMapper {

    MemberMapper Instance = Mappers.getMapper(MemberMapper.class);

    Member toEntity(MemberRequestDto memberRequestDto);

    @Named("memberResponse")
    @Mapping(source = "roles", target = "roles", ignore = true)
    @Mapping(source = "password", target = "password", ignore = true)
    MemberResponseDto MemberResponse(Member member);
}