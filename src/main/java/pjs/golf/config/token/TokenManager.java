package pjs.golf.config.token;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface TokenManager {

    String createToken(Authentication authentication, TokenType tokenType);
    Authentication getAuthenticationFromRefreshToken(HttpServletRequest request);
    boolean validateToken(String token);
    String getId(String accessToken);
    String getUsername(String accessToken);
    String getName(String accessToken);
    String getDepartment(String accessToken);
    String getCompany(String accessToken);
    String getEmail(String accessToken);
    Authentication getAuthentication(String token);

    String getStoredRefreshToken(HttpServletRequest request);
    void addRefreshTokenToResponse(String refreshToken, HttpServletResponse response);

    void logout(HttpServletRequest req, HttpServletResponse res);


}