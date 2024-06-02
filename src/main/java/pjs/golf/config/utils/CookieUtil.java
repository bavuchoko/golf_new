package pjs.golf.config.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import pjs.golf.config.token.TokenType;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final Logger log = LoggerFactory.getLogger(CookieUtil.class);


    public ResponseCookie getCookie(HttpServletRequest req, String cookieName){
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


}