package booking_system.service;

import booking_system.enums.USER_ROLE;
import booking_system.exception.EmailIsNullException;
import booking_system.exception.InvalidTokenException;
import booking_system.exception.TokenExpiredException;
import booking_system.exception.TokenIsNullException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt-secret}")
    private String secretKey;

    @Value("${jwt-expiration}")
    private int jwtExpiration;

    @Value("${jwt-refresh-expiration}")
    private int refreshExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        } catch (IllegalArgumentException e) {
            throw new TokenIsNullException();
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractEmail(String token) {
        if (token == null || token.isEmpty()) {
            throw new TokenIsNullException();
        }
        String email = extractAllClaims(token).getSubject();
        if (email == null || email.isEmpty()) {
            throw new EmailIsNullException();
        }
        return email;
    }

    public String generateAccessToken(String email, USER_ROLE role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "access");
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("tokenId", UUID.randomUUID().toString());
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "access".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            String email = extractEmail(token);
            return !isTokenExpired(token) && email != null && isAccessToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("type"))
                    && claims.getExpiration().after(new Date())
                    && claims.getSubject() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private String extractAccessTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public String extractAccessTokenFromCookies(HttpServletRequest request) {
        return extractAccessTokenFromCookies(request, "accessToken");
    }

    public String extractRefreshTokenFromCookies(HttpServletRequest request) {
        return extractAccessTokenFromCookies(request, "refreshToken");
    }

    private void addTokenCookie(HttpServletResponse response, String token, String cookieName, int maxAge) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        addTokenCookie(response, token, "accessToken", jwtExpiration / 1000);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        addTokenCookie(response, token, "refreshToken", refreshExpiration / 1000);
    }

    public void clearTokenCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public void clearAccessTokenCookie(HttpServletResponse response) {
        clearTokenCookie(response, "accessToken");
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        clearTokenCookie(response, "refreshToken");
    }

}
