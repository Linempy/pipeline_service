package com.manticore.repository;

import com.manticore.model.Pipeline;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс для работы с хранилищем пайплайнов
 *
 * @author Linempy
 * @since 24.07.2026
 */
public interface PipelineRepository {

    Pipeline save(Pipeline pipeline);

    Optional<Pipeline> findById(UUID id);

    boolean existsById(UUID id);
}
