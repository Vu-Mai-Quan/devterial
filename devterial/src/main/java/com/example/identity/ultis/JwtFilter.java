package com.example.identity.ultis;

import com.example.identity.model.User;
import com.example.identity.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.micrometer.common.lang.NonNull;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_LENGTH = 7;

    UserDetailsService userDetailsService;
    JwtService jwtService;
    ObjectMapper mapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
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
        }catch (ExpiredJwtException | MalformedJwtException |
                UnsupportedJwtException |
                IllegalArgumentException ex) {
            sendErrorResponse(response,request, String.format("Token không hợp lệ hoặc đã hết hạn: %s",ex.getMessage()));
        }
    }

    private boolean isValidAuthHeader(String header) {
        return header != null && header.startsWith(BEARER_PREFIX);
    }

    private boolean isValidToken(String jwt) {
        return jwtService.isInssuerValid(jwt) && !jwtService.isBlackList(jwt);
    }

    private void authenticateUser(String jwt, HttpServletRequest request) {
        String username = jwtService.extractUserName(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User userDetails = (User) userDetailsService.loadUserByUsername(username);
            // Lấy thông tin user và roles từ DB, không cần đọc roles từ JWT
            if (jwtService.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // Sử dụng roles từ DB
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
        response.put("timestamp", new Date(System.currentTimeMillis()));
        response.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        response.put("message", message);
        response.put("error", "Xác thực không thành công");
        response.put("path", rq.getRequestURI());

        rp.getWriter().write(mapper.writeValueAsString(response));
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        final   Pair<String, Set<String>> byPassTokens =
                Pair.of("**/public/**", Set.of("GET","POST","PUT","DELETE"));
        AntPathMatcher matcher = new AntPathMatcher();
        String servletPath = request.getRequestURL().toString();
        String method = request.getMethod();
        return matcher.match(byPassTokens.getLeft(), servletPath)&&
                byPassTokens.getRight().stream().anyMatch(m -> m.equalsIgnoreCase(method));
    }
}