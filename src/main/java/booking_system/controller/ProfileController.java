package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.PasswordChangeRequest;
import booking_system.DTO.ProfileEditRequest;
import booking_system.service.JwtService;
import booking_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/edit")
    public ResponseEntity editProfile(
            @Valid @RequestBody ProfileEditRequest data,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String token = jwtService.extractTokenFromCookies(request);
            ApiResponse apiResponse = userService.editProfile(data, token);
            if (apiResponse.getData() != null && apiResponse.getData() instanceof Map) {
                Map<String, Object> responseData = (Map<String, Object>) apiResponse.getData();
                if (responseData.containsKey("accessToken")) {
                    String newToken = (String) responseData.get("accessToken");
                    jwtService.addTokenCookie(response, newToken);
                }
            }
            log.debug("edit ok");
            return ResponseEntity.ok(ApiResponse.success(apiResponse, "edit ok"));
        } catch (Exception e) {
            log.debug("edit error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    @PutMapping("/password")
    public ResponseEntity changePassword(
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest,
            HttpServletRequest request) {
        try {
            String oldPass = passwordChangeRequest.oldPassword();
            String newPass = passwordChangeRequest.newPassword();
            String token = jwtService.extractTokenFromCookies(request);
            ApiResponse apiResponse = userService.changePassword(token, oldPass, newPass);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(apiResponse, "changePassword ok"));
            }
            throw new Exception(apiResponse.getMessage());
        } catch (Exception e) {
            log.debug("changePassword error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }
}