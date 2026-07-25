package com.manticore.mapper;

import com.manticore.dto.DependencyCreateRequest;
import com.manticore.dto.DependencyResponse;
import com.manticore.dto.ExecutionOrderResponse;
import com.manticore.dto.NodeResponse;
import com.manticore.dto.PipelineResponse;
import com.manticore.model.Dependency;
import com.manticore.model.Node;
import com.manticore.model.Pipeline;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Маппер для сопоставления Entity с DTO
 *
 * @author Linempy
 * @since 24.07.2026
 */
@Mapper(componentModel = "spring")
public interface PipelineMapper {

    NodeResponse toNodeResponse(Node node);

    DependencyResponse toResponse(Dependency dependency);

    PipelineResponse toResponse(Pipeline pipeline);

    default Dependency toEntity(DependencyCreateRequest request) {
        return new Dependency(request.from(), request.to());
    }

    default ExecutionOrderResponse toExecutionOrderResponse(UUID pipelineId, List<String> order) {
        return new ExecutionOrderResponse(pipelineId, new ArrayList<>(order));
    }
}
