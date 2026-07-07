package com.academianet.demo.dto;

import java.time.Instant;

public record UserActivityResponse(
        String id,
        String type,
        String detail,
        Instant timestamp
) {}
