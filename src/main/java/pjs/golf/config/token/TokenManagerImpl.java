package pjs.golf.config.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pjs.golf.app.account.dto.AccountAdapter;
import pjs.golf.app.account.repository.AccountJpaRepository;
import pjs.golf.config.utils.CookieUtil;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenManagerImpl implements TokenManager, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenManagerImpl.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long accessTokenValidityTime;

    @Value("${spring.jwt.token-validity-one-min}")
    private long globalTimeOneMin;

    private Key key;

    @Autowired
    CookieUtil cookieUtil;

    @Autowired
    AccountJpaRepository accountRepository;


    public TokenManagerImpl(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token-validity-one-min}") long tokenValidityOneMin) {
        this.secret = secret;
        // 30분
        this.accessTokenValidityTime = tokenValidityOneMin * 15 ;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.printf("secret");
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


        long remainingMilliseconds = getRemainingMilliseconds();

        //액세스토큰 유효기간 15분
        long now = (new Date()).getTime();
        Date validity =  tokenType == TokenType.ACCESS_TOKEN ?  new Date(now + this.accessTokenValidityTime) : new Date(now + remainingMilliseconds);

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", id);
        payloads.put("username", userName);
        payloads.put(AUTHORITIES_KEY, authorities);


        return Jwts.builder()
                .setSubject(authentication.getName())
                .setClaims(payloads)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .compact();
    }

    private static long getRemainingMilliseconds() {
        // 갱신토큰 유효기간 오늘 자정까지
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime midnight = LocalDateTime.of(nowTime.toLocalDate(), LocalTime.MAX);
        Duration remainingTime = Duration.between(nowTime, midnight);
        long remainingMilliseconds = remainingTime.toMillis();
        return remainingMilliseconds;
    }

    @Override
    public Authentication getAuthenticationFromRefreshToken(HttpServletRequest request) {
        String refreshTokenInCookie = cookieUtil.getCookie(request, TokenType.REFRESH_TOKEN.getValue()).getValue();
        if (validateToken(refreshTokenInCookie)) {
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
        Cookie refreshTokenCookie = cookieUtil.createCookie(TokenType.REFRESH_TOKEN.getValue(), refreshToken);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);
        long now = (new Date()).getTime();
        refreshTokenCookie.setMaxAge((int)(now + getRemainingMilliseconds()));
        response.addCookie(refreshTokenCookie);
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res) {
        if(cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue()) != null){
            res.addCookie(cookieUtil.deleteCookie(req, TokenType.REFRESH_TOKEN.getValue()));
        }
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
            logger.info("잘못된 JWT 서명입니다.");
            throw e;
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
            throw e;
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
            throw e;
        }

    }

}