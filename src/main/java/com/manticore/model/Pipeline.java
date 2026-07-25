package com.manticore.model;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сущность, хранящая узлы и их зависимости для одного пайплайна.
 *
 * @author Linempy
 * @since 24.07.2026
 */
public class Pipeline {

    @Getter
    private final UUID id;
    @Getter
    private final String name;
    private final Set<Node> nodes;
    private final Set<Dependency> dependencies;

    public Pipeline(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.nodes = new LinkedHashSet<>();
        this.dependencies = new LinkedHashSet<>();
    }

    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    public Set<String> getNodeIds() {
        return nodes.stream()
                .map(Node::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Dependency> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }

    public boolean hasNode(String nodeId) {
        return nodes.stream().anyMatch(node -> node.getId().equals(nodeId));
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addDependency(Dependency dependency) {
        dependencies.add(dependency);
    }

    public boolean hasDependency(Dependency dependency) {
        return dependencies.contains(dependency);
    }
}
