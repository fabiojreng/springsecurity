package junior.java.springsecurity.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
    
}
