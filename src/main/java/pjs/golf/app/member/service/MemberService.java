package pjs.golf.app.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import pjs.golf.app.member.dto.MemberRequestDto;
import pjs.golf.common.SearchDto;
import pjs.golf.app.member.dto.MemberResponseDto;
import pjs.golf.app.member.entity.Member;

import java.util.List;


public interface MemberService extends UserDetailsService {

    Member createMember(Member member);

    String authorize(MemberRequestDto memberRequestDto, HttpServletResponse response, HttpServletRequest request);

    void logout(HttpServletRequest req, HttpServletResponse res);

    String reIssueToken(HttpServletRequest request);

    List<Member> getTempUsersByUserNames(List<Object> list);

    List createUserIfDosenExist(List<String> name);
}
