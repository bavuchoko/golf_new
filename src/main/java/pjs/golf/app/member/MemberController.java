package pjs.golf.app.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import pjs.golf.app.member.dto.MemberRequestDto;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.member.entity.MemberRole;
import pjs.golf.app.member.mapper.MemberMapper;
import pjs.golf.app.member.service.MemberService;
import pjs.golf.config.filter.TokenFilter;

import java.util.Set;


@RestController
@RequestMapping(value = "/api/user", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity create(
            @Valid @RequestBody MemberRequestDto memberRequestDto
    ) {
        memberRequestDto.setRoles(Set.of(MemberRole.USER));
        Member userAccount = MemberMapper.Instance.toEntity(memberRequestDto);
        memberService.createMember(userAccount);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity authenticate(
            @RequestBody MemberRequestDto memberRequestDto,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            String accessToken = memberService.authorize(memberRequestDto,response, request);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
            return new ResponseEntity(accessToken, httpHeaders, HttpStatus.OK);
        }catch (BadCredentialsException e){
            return new ResponseEntity<>("fail to login",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/reissue")
    public ResponseEntity reissue(HttpServletRequest request) {

        try{
            String accessToken = memberService.reIssueToken(request);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);

            return new ResponseEntity(accessToken, httpHeaders, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity("fail to refresh token", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/logout")
    public void logout(HttpServletRequest req, HttpServletResponse res){
        memberService.logout(req, res);
    }
}
