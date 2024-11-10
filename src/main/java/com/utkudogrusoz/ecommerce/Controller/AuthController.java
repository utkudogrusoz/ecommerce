package com.utkudogrusoz.ecommerce.Controller;

import com.utkudogrusoz.ecommerce.Core.Service.JwtService;
import com.utkudogrusoz.ecommerce.Dto.request.LoginRequest;
import com.utkudogrusoz.ecommerce.Dto.request.RefreshTokenRequest;
import com.utkudogrusoz.ecommerce.Dto.response.LoginResponse;
import com.utkudogrusoz.ecommerce.Model.UserModel;
import com.utkudogrusoz.ecommerce.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final JwtService jwtService;

    private final AuthService authenticationService;

    public AuthController(JwtService jwtService, AuthService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) throws Exception {
        String refreshToken = refreshTokenRequest.refreshToken();

        String username = jwtService.extractUsername(refreshToken);

        UserModel userModel = authenticationService.refreshAccessToken(username);

        if (jwtService.isRefreshTokenValid(refreshToken, userModel)) {
            // Geçerli refresh token ise yeni access token oluşturuyoruz
            String newAccessToken = jwtService.generateAccessToken(userModel);
            String newRefreshToken = jwtService.generateRefreshToken(userModel);
            return ResponseEntity.ok(new LoginResponse(newAccessToken, newRefreshToken));
        } else {
            throw new Exception();
        }

    }

    @PostMapping("/signup")
    public ResponseEntity register(@RequestBody LoginRequest registerUserDto) {

        try {
            UserModel registeredUser = authenticationService.signup(registerUserDto);

            return new ResponseEntity<UserModel>(registeredUser, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<String>("Username already exists", HttpStatus.CONFLICT);

        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginUserDto) {
        UserModel authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtAccessToken = jwtService.generateAccessToken(authenticatedUser);
        String jwtRefreshToken = jwtService.generateRefreshToken(authenticatedUser);


        LoginResponse loginResponse = new LoginResponse(jwtAccessToken, jwtRefreshToken);

        return ResponseEntity.ok(loginResponse);
    }
}