package pjs.golf.app.account.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import pjs.golf.app.account.dto.AccountAdapter;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.account.entity.AccountRole;
import pjs.golf.app.account.entity.Gender;
import pjs.golf.app.account.mapper.AccountMapper;
import pjs.golf.app.account.repository.AccountJpaRepository;
import pjs.golf.app.account.repository.querydsl.AccountQuerydslSupport;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.common.WebCommon;
import pjs.golf.common.exception.AlreadyExistSuchDataCustomException;
import pjs.golf.config.token.TokenManager;
import pjs.golf.config.token.TokenType;
import pjs.golf.config.utils.CookieUtil;
import pjs.golf.config.utils.RedisUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);


    private final PasswordEncoder passwordEncoder;
    private final AccountJpaRepository accountJpaRepository;
    private final AccountQuerydslSupport accountQuerydslSupport;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenManager tokenManager;

    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    @Override
    public Account createAccount(AccountRequestDto accountRequestDto) {
        accountJpaRepository.findByUsername(accountRequestDto.getUsername()).ifPresent( e->{
                throw new AlreadyExistSuchDataCustomException("이미 존재하는 아이디 입니다.");});
        accountRequestDto.setRoles(Set.of(AccountRole.USER));
        accountRequestDto.setJoinDate(LocalDateTime.now());
        getGender(accountRequestDto);
        Account account = AccountMapper.Instance.toEntity(accountRequestDto);
        account.overwritePassword(passwordEncoder.encode(account.getPassword()));
        return this.accountJpaRepository.save(account);
    }

    private void getGender(AccountRequestDto accountRequestDto) {
        Character lastBirth = accountRequestDto.getBirth().charAt( accountRequestDto.getBirth().length() - 1);
        if('1'== lastBirth){
            accountRequestDto.setGender(Gender.MALE);
        }else if('2'== lastBirth){
            accountRequestDto.setGender(Gender.FEMALE);
        }
    }

    @Override
    public String authorize(AccountRequestDto account, HttpServletResponse response, HttpServletRequest request) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(account.getUsername(),  account.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenManager.createToken(authentication, TokenType.ACCESS_TOKEN);
        String refreshToken = tokenManager.createToken(authentication, TokenType.REFRESH_TOKEN);
        tokenManager.addRefreshTokenToResponse(refreshToken, response);
        log.info("authorize call");
        redisUtil.setData(refreshToken, WebCommon.getClientIp(request));
        return accessToken;
    }

    @Override
    public String reIssueToken(HttpServletRequest request, HttpServletResponse response) {
        String storedRefreshToken = tokenManager.getStoredRefreshToken(request);
        if(StringUtils.hasText(storedRefreshToken) && tokenManager.validateToken(storedRefreshToken)) {
            //refresh토큰으로 부터 인증객체 생성
            Authentication authentication = tokenManager.getAuthenticationFromRefreshToken(request);

            //갱신토큰을 갱신함
            String refreshToken = tokenManager.createToken(authentication, TokenType.REFRESH_TOKEN);
            tokenManager.addRefreshTokenToResponse(refreshToken, response);

            return tokenManager.createToken(authentication, TokenType.ACCESS_TOKEN);
        }return null;
    }

    @Override
    public void deleteAccount(Account account) {
        accountJpaRepository.delete(account);
    }

    @Override
    public List getUserList() {
        return accountJpaRepository.findAll();
    }

    @Override
    public Page<Account> getUserListPage(Pageable pageable) {
        return accountJpaRepository.findAll(pageable);
    }


    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res) {
        /***
         * todo
         * 현재 갱신 토큰만 삭제하고 있고
         * 이미 발급된 엑세스 토큰은 여전히 유효하므로 해당 토큰을 무효화 하기 위해 유효기간 0짜리로 재발급 하는 로직이 필요함.
         */
        if( cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue()) != null){
            String refreshTokenInCookie = tokenManager.getStoredRefreshToken(req);;
            log.info("refreshToken = {}", refreshTokenInCookie);
            redisUtil.deleteData(refreshTokenInCookie);
        }
        tokenManager.logout(req, res);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new AccountAdapter(accountJpaRepository.findByUsernameWithRoles(username)
                .orElseThrow(()->new UsernameNotFoundException(username)));
    }

    @Override
    public List getTempUsersByUserNames(List names) {
        //id가 temp_이름  인 사람들
        return accountQuerydslSupport.getTempUsersByUserNames(names);
    }

    @Override
    public List createUserIfDosenExist(List<String> names) {
        List tempUsers = names.stream()
                .map(e -> AccountMapper.Instance.toEntity(AccountRequestDto.builder()
                        .name(e)
                        .password(this.passwordEncoder.encode("temp_xxaareddfef"))
                        .username("temp_"+e)
                        .birth("6001011")
                        .gender(Gender.MALE)
                        .joinDate(LocalDateTime.now())
                        .roles(Set.of(AccountRole.USER))
                        .build())).collect(Collectors.toList());
        accountJpaRepository.saveAll(tempUsers);
        return accountQuerydslSupport.getTempUsersByUserNames(names);
    }

}
