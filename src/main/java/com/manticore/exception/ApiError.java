package com.manticore.exception;

import java.time.Instant;

/**
 * Стандартный класс для ответа на ошибку.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message
) {
}
