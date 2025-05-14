package io.eventuate.examples.springauthorizationserver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // Create a wrapper to capture the request body
    ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
    // Create a wrapper to capture the response body
    ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

    try {
      filterChain.doFilter(requestWrapper, responseWrapper);
    } finally {
      // Log request
      String requestBody = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
      log.trace("REQUEST: {} {} - Body: {}", request.getMethod(), request.getRequestURI(), requestBody);

      // Log response
      String responseBody = new String(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());
      log.trace("RESPONSE: Status: {} - Body: {}", responseWrapper.getStatus(), responseBody);

      // Copy content to the original response
      responseWrapper.copyBodyToResponse();
    }
  }
}
