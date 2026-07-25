package com.manticore.model;

import lombok.Getter;

import java.util.Objects;

/**
 * Сущность узла пайнлайна. Уникальность узла в рамках пайплайна определяется его идентификатором
 *
 * @author Linempy
 * @since 24.07.2026
 */
@Getter
public final class Node {

    private final String id;
    private final String name;

    public Node(String id, String name) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Node id must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Node name must not be blank");
        }
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Node node && id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
