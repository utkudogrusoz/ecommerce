package com.utkudogrusoz.ecommerce.Dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {

}
