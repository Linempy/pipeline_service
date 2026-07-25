package com.manticore.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на создание пайплайна.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public record PipelineCreateRequest(
        @NotBlank(message = "must not be blank")
        String name
) {
}
