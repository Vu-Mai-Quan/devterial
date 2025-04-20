package com.example.identity.ultis;

import com.example.identity.model.User;
import com.example.identity.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.NonNull;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.internal.Pair;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_LENGTH = 7;

    UserDetailsService userDetailsService;
    JwtService jwtService;
    ObjectMapper mapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (!isValidAuthHeader(header)) {
            sendErrorResponse(response, request, "Token không hợp lệ"
			);
            return;
        }

        String jwt = header.substring(BEARER_LENGTH);
        if (!isValidToken(jwt)) {
            sendErrorResponse(response, request, "Token không hợp lệ hoặc đã bị hủy"
			);
            return;
        }

        authenticateUser(jwt, request);
        filterChain.doFilter(request, response);
    }

    private boolean isValidAuthHeader(String header) {
        return header != null && header.startsWith(BEARER_PREFIX);
    }

    private boolean isValidToken(String jwt) {
        return jwtService.isInssuValid(jwt) && !jwtService.isBlackList(jwt);
    }

    private void authenticateUser(String jwt, HttpServletRequest request) {
        String username = jwtService.extractUserName(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User userDetails = (User) userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private void sendErrorResponse(@NonNull HttpServletResponse rp, @Nonnull HttpServletRequest rq, String message) throws IOException {
        rp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        rp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        rp.setCharacterEncoding("UTF-8");

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", new Date());
        response.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        response.put("message", message);
        response.put("error", "Xác thực không thành công");
        response.put("path", rq.getRequestURI());

        rp.getWriter().write(mapper.writeValueAsString(response));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final List<Pair<String, String>> byPassTokens = List.of(
                Pair.of("/auth/public/**", "POST"),
                Pair.of("/product", "GET"),
                Pair.of("/product/**", "GET"),
                Pair.of("/user/register", "POST")
        );

        String servletPath = request.getServletPath();
        String method = request.getMethod();

        return byPassTokens.stream()
                .anyMatch(token -> new AntPathMatcher().match(token.getLeft(), servletPath)
                        && method.equalsIgnoreCase(token.getRight()));
    }
}