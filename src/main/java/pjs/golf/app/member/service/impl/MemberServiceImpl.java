package pjs.golf.app.member.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pjs.golf.app.member.dto.MemberRequestDto;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.member.entity.MemberRole;
import pjs.golf.app.member.repository.MemberJpaRepository;
import pjs.golf.app.member.service.MemberService;
import pjs.golf.config.token.TokenManager;
import pjs.golf.config.token.TokenType;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberJpaRepository memberJpaRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenManager tokenManager;

    @Override
    public Member createMember(Member member) {
        member.overwritePassword(passwordEncoder.encode(member.getPassword()));
        return this.memberJpaRepository.save(member);
    }

    @Override
    public String authorize(MemberRequestDto account, HttpServletResponse response, HttpServletRequest request) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(account.getUsername(),  account.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenManager.createToken(authentication, TokenType.ACCESS_TOKEN);
        String refreshToken = tokenManager.createToken(authentication, TokenType.REFRESH_TOKEN);
        tokenManager.addRefreshTokenToResponse(refreshToken, response);

        return accessToken;
    }

    @Override
    public String reIssueToken(HttpServletRequest request) {
        String storedRefreshToken = tokenManager.getStoredRefreshToken(request);
        if(StringUtils.hasText(storedRefreshToken) && tokenManager.validateToken(storedRefreshToken)) {
            //refresh토큰으로 부터 인증객체 생성
            Authentication authentication = tokenManager.getAuthenticationFromRefreshToken(request);
            return tokenManager.createToken(authentication, TokenType.ACCESS_TOKEN);
        }return null;
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res) {
        /***
         * todo
         * 현재 갱신 토큰만 삭제하고 있고
         * 이미 발급된 엑세스 토큰은 여전히 유효하므로 해당 토큰을 무효화 하기 위해 유효기간 0짜리로 재발급 하는 로직이 필요함.
         */
        tokenManager.logout(req, res);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
