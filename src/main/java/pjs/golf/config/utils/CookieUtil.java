package pjs.golf.config.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public Cookie createCookie(String cookieName, String value){
        Cookie cookie = new Cookie(cookieName,value);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }


    public Cookie deleteCookie(HttpServletRequest req ,String cookieName) {
        Cookie cookie = getCookie(req,"refreshToken");
        cookie.setMaxAge(0); // 쿠키의 만료일을 과거로 설정하여 삭제
        cookie.setPath("/"); // 쿠키의 경로 설정 (경로에 해당하는 모든 URL에서 쿠키 삭제)
        return cookie;
    }

}