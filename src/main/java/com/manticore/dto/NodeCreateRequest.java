package com.manticore.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на добавление узла в пайплайн.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public record NodeCreateRequest(
        @NotBlank(message = "must not be blank")
        String nodeId,

        @NotBlank(message = "must not be blank")
        String name
) {
}
