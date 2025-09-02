package junior.java.springsecurity.controller;

import junior.java.springsecurity.controller.dto.LoginRequest;
import junior.java.springsecurity.controller.dto.LoginResponse;
import junior.java.springsecurity.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        var token = tokenService.generateToken(loginRequest);
        var expiresIn = tokenService.getExpiresIn();

        return ResponseEntity.ok(new LoginResponse(token, expiresIn));
    }
}