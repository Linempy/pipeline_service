package com.manticore.exception;

import java.util.UUID;

/**
 * Исключение, которое сигнализирует о том, что пайплайн не может быть найден по его идентификатору.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public class PipelineNotFoundException extends RuntimeException {

    public PipelineNotFoundException(UUID pipelineId) {
        super("Pipeline not found: " + pipelineId);
    }
}
