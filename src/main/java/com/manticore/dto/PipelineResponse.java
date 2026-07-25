package com.manticore.dto;

import java.util.List;
import java.util.UUID;

/**
 * Полное представление пайплайна, возвращаемое API.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public record PipelineResponse(
        UUID id,
        String name,
        List<NodeResponse> nodes,
        List<DependencyResponse> dependencies
) {
}
