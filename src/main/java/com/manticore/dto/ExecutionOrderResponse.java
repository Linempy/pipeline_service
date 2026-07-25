package com.manticore.dto;

import java.util.List;
import java.util.UUID;

/**
 * Топологический порядок расположения узлов в пайплайне.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public record ExecutionOrderResponse(
        UUID pipelineId,
        List<String> order
) {
}
