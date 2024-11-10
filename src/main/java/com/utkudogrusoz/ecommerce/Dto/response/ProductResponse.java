package com.utkudogrusoz.ecommerce.Dto.response;

public record ProductResponse(
        Long id,
        String name,
        String desc,
        double price
) {
}
