package com.manticore.utils;

import com.manticore.model.Dependency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестовый класс для проверки функциональности алгоритмов для графов
 *
 * @author Linempy
 * @since 24.07.2026
 */
@DisplayName("Тестирование GraphAlgorithms")
class GraphAlgorithmsTest {

    @Test
    void topologicalSort_returnsValidOrderForExampleGraph() {
        Set<String> nodes = Set.of("input", "filter", "enrich", "output");
        Set<Dependency> dependencies = Set.of(
                new Dependency("input", "filter"),
                new Dependency("input", "enrich"),
                new Dependency("filter", "output"),
                new Dependency("enrich", "output")
        );

        List<String> order = GraphAlgorithms.topologicalSort(nodes, dependencies);

        assertEquals(4, order.size());
        assertTrue(order.indexOf("input") < order.indexOf("filter"));
        assertTrue(order.indexOf("input") < order.indexOf("enrich"));
        assertTrue(order.indexOf("filter") < order.indexOf("output"));
        assertTrue(order.indexOf("enrich") < order.indexOf("output"));
    }

    @Test
    void topologicalSort_returnsSingleNodeForGraphWithoutDependencies() {
        Set<String> nodes = Set.of("only-node");

        List<String> order = GraphAlgorithms.topologicalSort(nodes, Set.of());

        assertEquals(List.of("only-node"), order);
    }

    @Test
    void wouldCreateCycle_detectsSelfLoop() {
        Set<String> nodes = Set.of("a", "b");
        Dependency selfLoop = new Dependency("a", "a");

        assertTrue(GraphAlgorithms.wouldCreateCycle(nodes, Set.of(), selfLoop));
    }

    @Test
    void wouldCreateCycle_detectsCycleWhenAddingClosingEdge() {
        Set<String> nodes = Set.of("a", "b", "c");
        Set<Dependency> dependencies = Set.of(
                new Dependency("a", "b"),
                new Dependency("b", "c")
        );
        Dependency closingEdge = new Dependency("c", "a");

        assertTrue(GraphAlgorithms.wouldCreateCycle(nodes, dependencies, closingEdge));
    }

    @Test
    void wouldCreateCycle_allowsAcyclicEdge() {
        Set<String> nodes = new LinkedHashSet<>(List.of("input", "filter", "output"));
        Set<Dependency> dependencies = Set.of(new Dependency("input", "filter"));
        Dependency newEdge = new Dependency("filter", "output");

        assertFalse(GraphAlgorithms.wouldCreateCycle(nodes, dependencies, newEdge));
    }

    @Test
    void topologicalSort_preservesInsertionOrderForIndependentNodes() {
        Set<String> nodes = new LinkedHashSet<>(List.of("first", "second", "third"));

        assertEquals(List.of("first", "second", "third"), GraphAlgorithms.topologicalSort(nodes, Set.of()));
    }
}
