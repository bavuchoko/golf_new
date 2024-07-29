package pjs.golf.config.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import pjs.golf.config.token.TokenType;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final Logger log = LoggerFactory.getLogger(CookieUtil.class);


    public ResponseCookie getCookie(HttpServletRequest req, String cookieName){
        log.info("Request Headers: {}", Collections.list(req.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, req::getHeader)));
        log.info("Request Cookies: {}", Arrays.toString(req.getCookies()));
        Cookie[] cookies = req.getCookies();
        if(cookies==null){
            log.info("cookie is null");
            return null;
        }
        for(Cookie cookie : cookies){
            log.info("cookie name= {}, cookie value = {}", cookie.getName(), cookie.getValue());

            if(cookie.getName().equals(cookieName)) {
                ResponseCookie responseCookie = ResponseCookie.from(cookie.getName(), cookie.getValue()).domain(cookie.getDomain())
                        .path(cookie.getPath())
                        .maxAge(cookie.getMaxAge())
                        .httpOnly(cookie.isHttpOnly())
                        .secure(cookie.getSecure())
                        .build();
                return responseCookie;
            }
        }
        return null;
    }

    public void addCookie(HttpServletResponse response, String cookieName, String value){
//        ResponseCookie newCookie = ResponseCookie.from(cookieName, value)
//                .path("/")
//                .sameSite("None")
//                .httpOnly(true)
//                .secure(true)
//                .maxAge(1 * 24 * 60 * 60) //1일
//                .build();
//        response.addHeader("Set-Cookie", newCookie.toString());

        Cookie newCookie = new Cookie(cookieName, value);
        newCookie.setPath("/");
        newCookie.setHttpOnly(true);
        newCookie.setSecure(true);
        newCookie.setMaxAge(1 * 24 * 60 * 60); // 1일

        // 쿠키를 추가
        response.addCookie(newCookie);

        // SameSite 속성을 추가하기 위해 직접 헤더를 설정
        String headerValue = String.format("%s; %s", newCookie.toString(), "SameSite=None");
        response.setHeader("Set-Cookie", headerValue);
    }

    public void removeCookie(HttpServletResponse response, String cookieName){
            ResponseCookie cookie = ResponseCookie.from(TokenType.REFRESH_TOKEN.getValue(), "")
                    .path("/")
                    .sameSite("None")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(0) // 쿠키를 즉시 만료시킵니다.
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
        }

}

