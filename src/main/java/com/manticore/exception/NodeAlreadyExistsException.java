package com.manticore.exception;

/**
 * Исключение, которое сигнализирует о попытке добавить дублирующийся узел.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public class NodeAlreadyExistsException extends RuntimeException {

    public NodeAlreadyExistsException(String nodeId) {
        super("Node already exists: " + nodeId);
    }
}
