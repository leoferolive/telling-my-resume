package com.tellingmyresume.config;

import com.tellingmyresume.utils.CorrelationIdUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CorrelationIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = request.getHeader(CorrelationIdUtils.CORRELATION_ID_HEADER);
        
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = CorrelationIdUtils.generateCorrelationId();
        }
        
        CorrelationIdUtils.setCorrelationId(correlationId);
        MDC.put("correlationId", correlationId);
        response.setHeader(CorrelationIdUtils.CORRELATION_ID_HEADER, correlationId);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CorrelationIdUtils.clearCorrelationId();
        MDC.clear();
    }
}