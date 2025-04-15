package com.example.identity.ultis;

import com.example.identity.model.User;
import com.example.identity.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtFilter extends OncePerRequestFilter {

	UserDetailsService userDetailsService;
	static Logger logger = LoggerFactory.getLogger(JwtFilter.class);
	JwtService jwtService;
	ObjectMapper mapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String header = request.getHeader("Authorization");
		String jwt = null, username = null;
		try {
			if (header == null || !header.startsWith("Bearer ")) {
				sendErrorResponser(response, request, "Token không hợp lệ", HttpServletResponse.SC_UNAUTHORIZED,
						"Xác thực không thành công");
				return;
			}
			jwt = header.substring(7);
			username = jwtService.extractUserName(jwt);
			if(!jwtService.isInssuValid(jwt)){
				sendErrorResponser(response, request, "Token không rõ nguồn gốc", HttpServletResponse.SC_UNAUTHORIZED,
						"Xác thực không thành công");
				return;
            }
			 if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				User userDetails = (User) userDetailsService.loadUserByUsername(username);
				if (jwtService.validateToken(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					filterChain.doFilter(request, response);
				}
			}
		} catch (ServletException | IOException e) {
			sendErrorResponser(response, request, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED,
					"Network Error!");
		} catch (ExpiredJwtException e) {
			sendErrorResponser(response, request, "Token đã hết hạn", HttpServletResponse.SC_UNAUTHORIZED,
					e.getMessage());
		}catch (MalformedJwtException | UnsupportedJwtException e) {
			sendErrorResponser(response, request, "Token không đúng định dạng", HttpServletResponse.SC_UNAUTHORIZED,
					e.getMessage());
		}catch (SignatureException e) {
			sendErrorResponser(response, request, "Token không hợp lệ", HttpServletResponse.SC_UNAUTHORIZED,
					e.getMessage());
		}

	}


//	private boolean isByPassToken(HttpServletRequest request) {
//		final AntPathMatcher antPathMatcher = new AntPathMatcher();
//		final List<Pair<String, String>> byPassTokens = Arrays.asList(Pair.of("/auth/login", "POST"),
////				Pair.of("/user/get-user", "GET"),
//				Pair.of("/product", "GET"), Pair.of("/product/**", "GET"), Pair.of("/user/register", "POST"));
//		String servletPath = request.getServletPath();
//		String method = request.getMethod();
//		for (Pair<String, String> token : byPassTokens) {
//			String pattern = token.getLeft();
//			String allowedMethod = token.getRight();
//			if (antPathMatcher.match(pattern, servletPath) && method.equalsIgnoreCase(allowedMethod)) {
//				return true;
//			}
//		}
//		return false;
//	}

	private void sendErrorResponser(@NonNull HttpServletResponse rp, @Nonnull HttpServletRequest rq, String message,
			int code, String error) throws IOException {
		try {
			rp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			rp.setContentType(MediaType.APPLICATION_JSON_VALUE);
			rp.setCharacterEncoding("UTF-8");
			Properties prop = new Properties();
			prop.put("timestamp", new Date());
			prop.put("code", code);
			prop.put("message", message);
			prop.put("error", error);
			prop.put("path", rq.getRequestURI());
			String json = mapper.writeValueAsString(prop);
			rp.getWriter().write(json);
		} catch (IOException e) {
			throw new IOException("Lỗi chuyển đổi json");
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		final AntPathMatcher antPathMatcher = new AntPathMatcher();
		final List<Pair<String, String>> byPassTokens = Arrays.asList(Pair.of("/auth/login", "POST"),
//				Pair.of("/user/get-user", "GET"),
				Pair.of("/product", "GET"), Pair.of("/product/**", "GET"), Pair.of("/user/register", "POST"));
		String servletPath = request.getServletPath();
		String method = request.getMethod();
		for (Pair<String, String> token : byPassTokens) {
			String pattern = token.getLeft();
			String allowedMethod = token.getRight();
			if (antPathMatcher.match(pattern, servletPath) && method.equalsIgnoreCase(allowedMethod)) {
				return true;
			}
		}
		return false;
	}

}
