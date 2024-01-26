package com.example.auth.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LogFilter implements Filter {
    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        log.info("start request: {} {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI()
        );

        ContentCachingRequestWrapper requestWrapper
                = new ContentCachingRequestWrapper(httpServletRequest);

        log.info(new String(
                requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8));

        // doFilter를 호출하지 않을 경우 다음 필터가 실행되지 않으며
        // -> 요청이 끝까지 전달되지 않는다.
        // --- 이 위는 요청 처리 전
        chain.doFilter(requestWrapper, response);
        // --- 이 아래는 요청 처리 후

        log.info(new String(
                requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8));

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        log.info("send response: {}", httpServletResponse.getStatus());
    }
}
