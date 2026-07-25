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
import com.manticore.utils.GraphAlgorithms;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для управлением графами пайплайнов
 *
 * @author Linempy
 * @since 24.07.2026
 */
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineMapper pipelineMapper;

    public PipelineResponse createPipeline(PipelineCreateRequest request) {
        UUID id = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(id, request.name());
        pipelineRepository.save(pipeline);
        return pipelineMapper.toResponse(pipeline);
    }

    public PipelineResponse addNode(UUID pipelineId, NodeCreateRequest request) {
        Pipeline pipeline = getPipelineOrThrow(pipelineId);
        synchronized (pipeline) {
            if (pipeline.hasNode(request.nodeId())) {
                throw new NodeAlreadyExistsException(request.nodeId());
            }

            pipeline.addNode(new Node(request.nodeId(), request.name()));
            pipelineRepository.save(pipeline);
            return pipelineMapper.toResponse(pipeline);
        }
    }

    public PipelineResponse addDependency(UUID pipelineId, DependencyCreateRequest request) {
        Pipeline pipeline = getPipelineOrThrow(pipelineId);
        synchronized (pipeline) {
            validateDependencyEndpoints(pipeline, pipelineId, request);

            Dependency dependency = pipelineMapper.toEntity(request);
            if (pipeline.hasDependency(dependency)) {
                return pipelineMapper.toResponse(pipeline);
            }

            if (GraphAlgorithms.wouldCreateCycle(pipeline.getNodeIds(), pipeline.getDependencies(), dependency)) {
                throw new InvalidDependencyException(
                        "Dependency " + request.from() + " -> " + request.to() + " would create a cycle"
                );
            }

            pipeline.addDependency(dependency);
            pipelineRepository.save(pipeline);
            return pipelineMapper.toResponse(pipeline);
        }
    }

    public PipelineResponse getPipeline(UUID pipelineId) {
        Pipeline pipeline = getPipelineOrThrow(pipelineId);
        synchronized (pipeline) {
            return pipelineMapper.toResponse(pipeline);
        }
    }

    public ExecutionOrderResponse getExecutionOrder(UUID pipelineId) {
        Pipeline pipeline = getPipelineOrThrow(pipelineId);
        synchronized (pipeline) {
            List<String> order = GraphAlgorithms.topologicalSort(pipeline.getNodeIds(), pipeline.getDependencies());
            return pipelineMapper.toExecutionOrderResponse(pipelineId, order);
        }
    }

    private Pipeline getPipelineOrThrow(UUID pipelineId) {
        return pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new PipelineNotFoundException(pipelineId));
    }

    private void validateDependencyEndpoints(Pipeline pipeline, UUID pipelineId, DependencyCreateRequest request) {
        if (request.from().equals(request.to())) {
            throw new InvalidDependencyException("Node cannot depend on itself");
        }
        if (!pipeline.hasNode(request.from())) {
            throw new NodeNotFoundException(pipelineId, request.from());
        }
        if (!pipeline.hasNode(request.to())) {
            throw new NodeNotFoundException(pipelineId, request.to());
        }
    }
}
