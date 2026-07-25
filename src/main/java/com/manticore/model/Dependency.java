package com.manticore.model;

/**
 * Сущность для направленной зависимости между двумя узлами.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public record Dependency(String from, String to) {

    public Dependency {
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("Source node id must not be blank");
        }
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Target node id must not be blank");
        }
    }
}
