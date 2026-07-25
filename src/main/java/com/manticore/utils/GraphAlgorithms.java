package com.manticore.utils;

import com.manticore.model.Dependency;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Реализация алгоритмов топологической сортировки и DFS
 *
 * @author Linempy
 * @since 24.07.2026
 */
public final class GraphAlgorithms {

    private GraphAlgorithms() {
    }

    public static boolean wouldCreateCycle(Set<String> nodes, Set<Dependency> dependencies, Dependency newDependency) {
        Map<String, Set<String>> adjacency = buildAdjacency(nodes, dependencies);
        return hasPath(newDependency.to(), newDependency.from(), adjacency);
    }

    public static List<String> topologicalSort(Set<String> nodes, Set<Dependency> dependencies) {
        Map<String, Integer> inDegree = new LinkedHashMap<>();
        Map<String, Set<String>> adjacency = new LinkedHashMap<>();

        for (String node : nodes) {
            inDegree.putIfAbsent(node, 0);
            adjacency.putIfAbsent(node, new LinkedHashSet<>());
        }

        for (Dependency dependency : dependencies) {
            adjacency.computeIfAbsent(dependency.from(), key -> new LinkedHashSet<>()).add(dependency.to());
            inDegree.merge(dependency.to(), 1, Integer::sum);
        }

        Deque<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<String> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.remove();
            order.add(current);

            for (String neighbor : adjacency.getOrDefault(current, Set.of())) {
                int updatedInDegree = inDegree.merge(neighbor, -1, Integer::sum);
                if (updatedInDegree == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (order.size() != nodes.size()) {
            throw new IllegalStateException("Graph contains a cycle");
        }

        return Collections.unmodifiableList(order);
    }

    private static Map<String, Set<String>> buildAdjacency(Set<String> nodes, Set<Dependency> dependencies) {
        Map<String, Set<String>> adjacency = new LinkedHashMap<>();
        for (String node : nodes) {
            adjacency.putIfAbsent(node, new LinkedHashSet<>());
        }
        for (Dependency dependency : dependencies) {
            adjacency.computeIfAbsent(dependency.from(), key -> new LinkedHashSet<>()).add(dependency.to());
        }
        return adjacency;
    }

    private static boolean hasPath(String start, String target, Map<String, Set<String>> adjacency) {
        Deque<String> stack = new ArrayDeque<>();
        Set<String> visited = new LinkedHashSet<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (!visited.add(current)) {
                continue;
            }
            if (current.equals(target)) {
                return true;
            }

            for (String neighbor : adjacency.getOrDefault(current, Set.of())) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }
        return false;
    }
}
