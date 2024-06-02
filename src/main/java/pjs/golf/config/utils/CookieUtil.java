package pjs.golf.config.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final Logger log = LoggerFactory.getLogger(CookieUtil.class);
    public Cookie createCookie(String cookieName, String value){
        Cookie cookie = new Cookie(cookieName,value);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            log.info("cookie name= {}, cookie value = {}", cookie.getName(), cookie.getValue());
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }


    public Cookie deleteCookie(HttpServletRequest req ,String cookieName) {
        Cookie cookie = getCookie(req,"refreshToken");
        cookie.setMaxAge(0);
        return cookie;
    }

}