package com.manticore.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на создание зависимости между двумя узлами.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public record DependencyCreateRequest(
        @NotBlank(message = "must not be blank")
        String from,

        @NotBlank(message = "must not be blank")
        String to
) {
}
