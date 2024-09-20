package com.tellingmyresume.interceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {
	
	private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
	
	
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    private Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress = request.getRemoteAddr(); // Usando o IP para identificar o cliente
        Bucket bucket = resolveBucket(ipAddress);

        if (bucket.tryConsume(1)) {
            return true; // Permite a requisição
        } else {
            // Retorna 429 Too Many Requests
            response.setStatus(429);
            response.getWriter().write("Too Many Requests.");
            return false; // Bloqueia a requisição
        }
    }
	
    // Método auxiliar para testes
    void setBucketForTesting(String ipAddress, Bucket bucket) {
        cache.put(ipAddress, bucket);
    }
    
}
