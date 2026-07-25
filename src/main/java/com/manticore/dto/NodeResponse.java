package com.manticore.dto;

/**
 * Представление узла, возвращаемое API.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public record NodeResponse(
        String id,
        String name
) {
}
