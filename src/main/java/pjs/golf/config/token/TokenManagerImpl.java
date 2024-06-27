package pjs.golf.config.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pjs.golf.app.account.dto.AccountAdapter;
import pjs.golf.app.account.repository.AccountJpaRepository;
import pjs.golf.common.WebCommon;
import pjs.golf.config.utils.CookieUtil;
import pjs.golf.config.utils.RedisUtil;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenManagerImpl implements TokenManager, InitializingBean {

    private final Logger log = LoggerFactory.getLogger(TokenManagerImpl.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long accessTokenValidityTime;

    @Value("${spring.jwt.token-validity-one-min}")
    private long globalTimeOneMin;

    @Autowired
    RedisUtil redisUtil;

    private Key key;

    @Autowired
    CookieUtil cookieUtil;

    @Autowired
    AccountJpaRepository accountRepository;


    public TokenManagerImpl(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token-validity-one-min}") long tokenValidityOneMin) {
        this.secret = secret;
        // 2시간
        this.accessTokenValidityTime = tokenValidityOneMin * 1 ;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    @Override
    public String createToken(Authentication authentication, TokenType tokenType) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Long id = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getId();
        String userName = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getUsername();
        String name = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getName();
        String birth   = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getBirth();
        String gender   = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getGender().toString();


        //갱신토큰 1주일
        long remainingMilliseconds = getRemainingMilliseconds();

        //액세스토큰 유효기간 15분
        long now = (new Date()).getTime();
        Date validity =  tokenType == TokenType.ACCESS_TOKEN ?  new Date(now + this.accessTokenValidityTime) : new Date(now + remainingMilliseconds);

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", id);
        payloads.put("name", name);
        payloads.put("username", userName);
        payloads.put("birth", birth);
        payloads.put("gender", gender);
        payloads.put(AUTHORITIES_KEY, authorities);


        return Jwts.builder()
                .setSubject(authentication.getName())
                .setClaims(payloads)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .compact();
    }

    private long getRemainingMilliseconds() {
        //1주일
        return this.accessTokenValidityTime * 60 * 24 * 7;
    }

    @Override
    public Authentication getAuthenticationFromRefreshToken(HttpServletRequest request) {
        String refreshTokenInCookie = cookieUtil.getCookie(request, TokenType.REFRESH_TOKEN.getValue()).getValue();
        String clientIP = WebCommon.getClientIp(request);
        if (validateToken(refreshTokenInCookie)) {
            //todo ip로 체크했더니 와이파이 <-> LTE 로 전환시 로그아웃 되어버림, 공유기 환경 대응어려움.
//            String storedIP = redisUtil.getData(refreshTokenInCookie);
//            if(clientIP.equals(storedIP))
                return getAuthentication(refreshTokenInCookie);
        }
        return null;
    }


    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        String username = claims.get("username", String.class);


        UserDetails userDetails=  new AccountAdapter(accountRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException(username)));

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    @Override
    public String getStoredRefreshToken(HttpServletRequest request) {
        return cookieUtil.getCookie(request, TokenType.REFRESH_TOKEN.getValue()).getValue();
    }

    @Override
    public void addRefreshTokenToResponse(String refreshToken, HttpServletResponse response) {
        long now = (new Date()).getTime();
        int maxAge = (int)((now + getRemainingMilliseconds()) / 1000);
        ResponseCookie cookie = ResponseCookie.from(TokenType.REFRESH_TOKEN.getValue(), refreshToken)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res) {
        if(cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue()) != null){
            redisUtil.deleteData(getStoredRefreshToken(req));
        }
        cookieUtil.removeCookie(res, TokenType.REFRESH_TOKEN.getValue());
    }


    public String getId(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("id").toString();
    }
    public String getUsername(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username").toString();
    }
    public String getEmail(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email").toString();
    }
    public String getName(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("name").toString();
    }


    public String getDepartment(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("department").toString();
    }
    public String getCompany(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("company").toString();
    }

    @Override
    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw e;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw e;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw e;
        }

    }

}