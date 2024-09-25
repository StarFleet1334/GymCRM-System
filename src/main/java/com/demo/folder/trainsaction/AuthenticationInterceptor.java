package com.demo.folder.trainsaction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String uri = request.getRequestURI();
    if (uri.contains("/register") || uri.contains("/login")) {
      return true;
    }

    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("authenticatedUser") == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
      return false;
    }

    return true;
  }
}
