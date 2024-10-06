package pjs.golf.config.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.logging.LogRecord;

@Slf4j
@Component
public class CustomFilter implements Filter {



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 작업이 필요하면 여기에 작성합니다.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 요청 URL을 로그로 남김
        log.info("Incoming request: " + requestURI);

        // 다음 필터 또는 서블릿으로 요청을 전달
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 필터가 종료될 때 정리 작업이 필요하면 여기에 작성합니다.
    }

}
