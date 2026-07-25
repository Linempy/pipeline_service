package com.manticore.exception;

import java.util.UUID;

/**
 * Исключение, которое сигнализирует о том, что запрашиваемый узел не принадлежит текущему
 * пайплайну (при построении зависимости).
 *
 * @author Linempy
 * @since 24.07.2026
 */
public class NodeNotFoundException extends RuntimeException {

    public NodeNotFoundException(UUID pipelineId, String nodeId) {
        super("Node '" + nodeId + "' not found in pipeline " + pipelineId);
    }
}
