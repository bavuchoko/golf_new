package pjs.golf.config.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.logging.LogRecord;

import static pjs.golf.common.WebCommon.getClientIp;

@Slf4j
@Component
public class CustomFilter implements Filter {



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 클라이언트 IP 주소 가져오기
        String clientIp = getClientIp(httpRequest);

        log.info("Incoming request from IP: " + clientIp + " to endpoint: " + requestURI);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
