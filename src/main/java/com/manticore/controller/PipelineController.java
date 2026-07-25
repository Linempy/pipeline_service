package com.manticore.controller;

import com.manticore.dto.DependencyCreateRequest;
import com.manticore.dto.ExecutionOrderResponse;
import com.manticore.dto.NodeCreateRequest;
import com.manticore.dto.PipelineCreateRequest;
import com.manticore.dto.PipelineResponse;
import com.manticore.service.PipelineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST-контроллер для управления графами пайплайнов
 * Предоставляет endpoints для создания, получения графа пайплайна,
 * а также добавление узлов и зависимостей между ними
 *
 * @author Linempy
 * @since 24.07.2026
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pipelines")
public class PipelineController {

    private final PipelineService pipelineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PipelineResponse> createPipeline(@Valid @RequestBody PipelineCreateRequest request) {
        PipelineResponse response = pipelineService.createPipeline(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{pipelineId}/nodes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PipelineResponse> addNode(@PathVariable UUID pipelineId, @Valid @RequestBody NodeCreateRequest request) {
        PipelineResponse response = pipelineService.addNode(pipelineId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{pipelineId}/edges")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PipelineResponse> addDependency(@PathVariable UUID pipelineId, @Valid @RequestBody DependencyCreateRequest request) {
        PipelineResponse response = pipelineService.addDependency(pipelineId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{pipelineId}")
    public ResponseEntity<PipelineResponse> getPipeline(@PathVariable UUID pipelineId) {
        PipelineResponse response = pipelineService.getPipeline(pipelineId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pipelineId}/execution-order")
    public ResponseEntity<ExecutionOrderResponse> getExecutionOrder(@PathVariable UUID pipelineId) {
        ExecutionOrderResponse response = pipelineService.getExecutionOrder(pipelineId);
        return ResponseEntity.ok(response);
    }
}
