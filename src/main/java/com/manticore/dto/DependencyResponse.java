package com.manticore.dto;

/**
 * Представление зависимостей, возвращаемое API.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public record DependencyResponse(
        String from,
        String to
) {
}
