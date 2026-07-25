package com.manticore.repository;

import com.manticore.model.Pipeline;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Потокобезопасное in-memory хранилище для пайплайнов
 *
 * @author Linempy
 * @since 24.07.2026
 */
@Repository
public class InMemoryPipelineRepository implements PipelineRepository {

    private final ConcurrentHashMap<UUID, Pipeline> storage = new ConcurrentHashMap<>();

    @Override
    public Pipeline save(Pipeline pipeline) {
        storage.put(pipeline.getId(), pipeline);
        return pipeline;
    }

    @Override
    public Optional<Pipeline> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }
}
