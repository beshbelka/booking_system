package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.entity.User;
import booking_system.exception.BaseException;
import booking_system.exception.InvalidTokenException;
import booking_system.exception.TokenBlacklistedException;
import booking_system.exception.TokenIsNullException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final BlacklistService blacklistService;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;

    public void refresh(String refreshToken, HttpServletResponse response, HttpServletRequest request){

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenIsNullException();
        }
        if (blacklistService.isBlackListed(refreshToken)) {
            throw new TokenBlacklistedException();
        }
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new InvalidTokenException();
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userService.findByEmail(email);

        blacklistService.blackList(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(email, user.getRole());
        String newRefreshToken = jwtService.generateRefreshToken(email);

        jwtService.addAccessTokenCookie(response, newAccessToken);
        jwtService.addRefreshTokenCookie(response, newRefreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }
}
