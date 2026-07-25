package com.manticore.service;

import com.manticore.dto.DependencyCreateRequest;
import com.manticore.dto.ExecutionOrderResponse;
import com.manticore.dto.NodeCreateRequest;
import com.manticore.dto.PipelineCreateRequest;
import com.manticore.dto.PipelineResponse;
import com.manticore.exception.InvalidDependencyException;
import com.manticore.exception.NodeAlreadyExistsException;
import com.manticore.exception.NodeNotFoundException;
import com.manticore.exception.PipelineNotFoundException;
import com.manticore.mapper.PipelineMapper;
import com.manticore.model.Dependency;
import com.manticore.model.Node;
import com.manticore.model.Pipeline;
import com.manticore.repository.PipelineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Тестовый класс для проверки функциональности сервиса пайплайнов
 *
 * @author Linempy
 * @since 24.07.2026
 */
@DisplayName("Тестирование PipelineService")
@ExtendWith(MockitoExtension.class)
class PipelineServiceTest {

    @Mock
    private PipelineRepository pipelineRepository;

    @Spy
    private PipelineMapper pipelineMapper = Mappers.getMapper(PipelineMapper.class);

    @InjectMocks
    private PipelineService pipelineService;

    @Test
    void createPipeline_savesPipelineAndReturnsResponse() {
        PipelineCreateRequest request = new PipelineCreateRequest("etl");

        PipelineResponse response = pipelineService.createPipeline(request);

        assertNotNull(response);
        assertEquals("etl", response.name());
        assertNotNull(response.id());

        verify(pipelineRepository, times(1)).save(any(Pipeline.class));
    }

    @Test
    void addNode_whenPipelineExists_addsNodeAndSaves() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        PipelineResponse response = pipelineService.addNode(
                pipelineId,
                new NodeCreateRequest("input", "Input Node")
        );

        assertTrue(response.nodes().stream()
                .anyMatch(n -> n.id().equals("input") && n.name().equals("Input Node")));

        verify(pipelineRepository).findById(pipelineId);
        verify(pipelineRepository).save(pipeline);
    }

    @Test
    void addNode_whenPipelineNotFound_throwsException() {
        UUID pipelineId = UUID.randomUUID();
        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.empty());

        assertThrows(
                PipelineNotFoundException.class,
                () -> pipelineService.addNode(pipelineId, new NodeCreateRequest("input", "Input Node"))
        );

        verify(pipelineRepository, never()).save(any());
    }

    @Test
    void addNode_whenNodeAlreadyExists_throwsException() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");
        pipeline.addNode(new Node("input", "Input Node"));

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        assertThrows(
                NodeAlreadyExistsException.class,
                () -> pipelineService.addNode(pipelineId, new NodeCreateRequest("input", "Input Node"))
        );

        verify(pipelineRepository, never()).save(any());
    }

    @Test
    void addDependency_whenNodesExist_addsDependencyAndSaves() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");
        pipeline.addNode(new Node("a", "Node A"));
        pipeline.addNode(new Node("b", "Node B"));

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        PipelineResponse response = pipelineService.addDependency(
                pipelineId,
                new DependencyCreateRequest("a", "b")
        );

        assertTrue(response.dependencies().stream()
                .anyMatch(d -> d.from().equals("a") && d.to().equals("b")));

        verify(pipelineRepository).save(pipeline);
    }

    @Test
    void addDependency_rejectsSelfDependency() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");
        pipeline.addNode(new Node("a", "Node A"));

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        assertThrows(
                InvalidDependencyException.class,
                () -> pipelineService.addDependency(
                        pipelineId,
                        new DependencyCreateRequest("a", "a")
                )
        );

        verify(pipelineRepository, never()).save(any());
    }

    @Test
    void addDependency_rejectsCycle() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");
        pipeline.addNode(new Node("a", "Node A"));
        pipeline.addNode(new Node("b", "Node B"));
        pipeline.addDependency(new Dependency("a", "b"));

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        assertThrows(
                InvalidDependencyException.class,
                () -> pipelineService.addDependency(
                        pipelineId,
                        new DependencyCreateRequest("b", "a")
                )
        );

        verify(pipelineRepository, never()).save(any());
    }

    @Test
    void addDependency_rejectsUnknownSourceNode() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");
        pipeline.addNode(new Node("input", "Input Node"));

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        assertThrows(
                NodeNotFoundException.class,
                () -> pipelineService.addDependency(
                        pipelineId,
                        new DependencyCreateRequest("unknown", "input")
                )
        );

        verify(pipelineRepository, never()).save(any());
    }

    @Test
    void addDependency_rejectsUnknownTargetNode() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");
        pipeline.addNode(new Node("input", "Input Node"));

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        assertThrows(
                NodeNotFoundException.class,
                () -> pipelineService.addDependency(
                        pipelineId,
                        new DependencyCreateRequest("input", "unknown")
                )
        );

        verify(pipelineRepository, never()).save(any());
    }

    @Test
    void addDependency_whenDependencyAlreadyExists_returnsCurrentState() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");
        pipeline.addNode(new Node("a", "Node A"));
        pipeline.addNode(new Node("b", "Node B"));
        pipeline.addDependency(new Dependency("a", "b"));

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        PipelineResponse response = pipelineService.addDependency(
                pipelineId,
                new DependencyCreateRequest("a", "b")
        );

        assertTrue(response.dependencies().stream()
                .anyMatch(d -> d.from().equals("a") && d.to().equals("b")));

        verify(pipelineRepository, never()).save(any());
    }

    @Test
    void getPipeline_whenExists_returnsResponse() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");
        pipeline.addNode(new Node("input", "Input Node"));

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        PipelineResponse response = pipelineService.getPipeline(pipelineId);

        assertNotNull(response);
        assertEquals(pipelineId, response.id());
        assertEquals("demo", response.name());

        assertTrue(response.nodes().stream()
                .anyMatch(n -> n.id().equals("input")));
    }

    @Test
    void getPipeline_whenNotFound_throwsException() {
        UUID pipelineId = UUID.randomUUID();
        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.empty());

        assertThrows(
                PipelineNotFoundException.class,
                () -> pipelineService.getPipeline(pipelineId)
        );
    }

    @Test
    void getExecutionOrder_whenExists_returnsOrder() {
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(pipelineId, "demo");
        pipeline.addNode(new Node("input", "Input Node"));
        pipeline.addNode(new Node("filter", "Filter Node"));
        pipeline.addNode(new Node("output", "Output Node"));
        pipeline.addDependency(new Dependency("input", "filter"));
        pipeline.addDependency(new Dependency("filter", "output"));

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        ExecutionOrderResponse response = pipelineService.getExecutionOrder(pipelineId);

        assertNotNull(response);
        assertEquals(pipelineId, response.pipelineId());
        assertEquals(3, response.order().size());
        assertEquals("input", response.order().get(0));
        assertEquals("filter", response.order().get(1));
        assertEquals("output", response.order().get(2));
    }

    @Test
    void getExecutionOrder_whenPipelineNotFound_throwsException() {
        UUID pipelineId = UUID.randomUUID();
        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.empty());

        assertThrows(
                PipelineNotFoundException.class,
                () -> pipelineService.getExecutionOrder(pipelineId)
        );
    }
}