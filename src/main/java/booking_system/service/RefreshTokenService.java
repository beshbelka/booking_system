package booking_system.service;

import booking_system.entity.User;
import booking_system.exception.InvalidTokenException;
import booking_system.exception.TokenBlacklistedException;
import booking_system.exception.TokenIsNullException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

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

        String newAccessToken = jwtService.generateAccessToken(email, user.getRole());

        jwtService.addAccessTokenCookie(response, newAccessToken);

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
