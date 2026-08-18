package booking_system.security;

import booking_system.exception.TokenExpiredException;
import booking_system.exception.UserNotFoundException;
import booking_system.service.JwtService;
import booking_system.service.TokenBlacklistService;
import booking_system.service.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = jwtService.extractTokenFromCookies(request);

        if (token == null) {
            log.error("token is null (JwtAuthenticationFilter)");
            filterChain.doFilter(request, response);
            return;
        }

        if (blacklistService.isBlackListed(token)) {
            log.info("token blacklisted");

            Cookie cookie = new Cookie("accessToken", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String email = jwtService.extractEmail(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        log.warn("JwtAuthenticationFilter: token invalid");
                    }
                } catch (UserNotFoundException e) {
                    log.warn("JwtAuthenticationFilter: user not found, {}", e.getMessage());
                    throw new UserNotFoundException();
                }
            }
        } catch (TokenExpiredException e) {
            log.error("JwtAuthenticationFilter: Токен не валидный, ошибка: {}", e.getMessage());
            throw new TokenExpiredException();
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (method.equals("GET")) {
            for (String publicPath : Path.PUBLIC_GET) {
                if (publicPath.endsWith("/**")) {
                    String prefix = publicPath.replace("/**", "");
                    if (path.startsWith(prefix)) return true;
                } else if (path.equals(publicPath)) return true;
            }
        }

        if (method.equals("POST")) {
            for (String publicPath : Path.PUBLIC_POST) {
                if (publicPath.endsWith("/**")) {
                    String prefix = publicPath.replace("/**", "");
                    if (path.startsWith(prefix)) return true;
                } else if (path.equals(publicPath)) return true;
            }
        }

        for (String publicPath : Path.PUBLIC) {
            if (publicPath.endsWith("/**")) {
                String prefix = publicPath.replace("/**", "");
                if (path.startsWith(prefix)) return true;
            } else if (path.equals(publicPath)) return true;
        }
        return false;
    }
}
