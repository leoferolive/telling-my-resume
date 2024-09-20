package com.tellingmyresume.interceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bucket;

class RateLimitingInterceptorTest {
	
	@InjectMocks
	private RateLimitingInterceptor rateLimitingInterceptor;
	
	@Mock
	private HandlerInterceptor mockInterceptor;
	
	@BeforeEach
	void setUp() {
		org.mockito.MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void testPreHandleRateLimitExceeded() throws Exception {
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    MockHttpServletResponse response = new MockHttpServletResponse();

	    // Forçamos o bucket a retornar false para simular a limitação excedida
	    Bucket bucket = mock(Bucket.class);
	    when(bucket.tryConsume(1)).thenReturn(false);
	    
	    // Usamos o método auxiliar para injetar o bucket simulado
	    rateLimitingInterceptor.setBucketForTesting("127.0.0.1", bucket);

	    // Simula a requisição com o IP '127.0.0.1'
	    request.setRemoteAddr("127.0.0.1");

	    boolean result = rateLimitingInterceptor.preHandle(request, response, new Object());

	    assertEquals(429, response.getStatus());
	    assertEquals(false, result);
	    assertEquals("Too Many Requests.", response.getContentAsString());
	}
	
	
	@Test
	void testPreHandleWithinLimit() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		when(mockInterceptor.preHandle(request, response, new Object())).thenReturn(true);
		
		boolean result = rateLimitingInterceptor.preHandle(request, response, new Object());
		
        assertEquals(200, response.getStatus());
        assertEquals(true, result);
	}
	
}
