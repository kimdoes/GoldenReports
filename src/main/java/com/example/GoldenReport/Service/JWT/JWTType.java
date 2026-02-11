package com.example.GoldenReport.Service.JWT;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JWTType {
    ACCESS (3600),
    REFRESH (3600 * 24 * 3);

    private final long validTime;
}
