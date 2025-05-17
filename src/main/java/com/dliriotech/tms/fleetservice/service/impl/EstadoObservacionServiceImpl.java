package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.EstadoObservacionResponse;
import com.dliriotech.tms.fleetservice.entity.EstadoObservacion;
import com.dliriotech.tms.fleetservice.infrastructure.cache.ReactiveRedisCacheService;
import com.dliriotech.tms.fleetservice.repository.EstadoObservacionRepository;
import com.dliriotech.tms.fleetservice.service.EstadoObservacionService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstadoObservacionServiceImpl implements EstadoObservacionService {

    private final EstadoObservacionRepository estadoObservacionRepository;
    private final ReactiveRedisCacheService cacheService;

    @Value("${app.cache.prefixes.estado-observacion}")
    private String cacheKey;

    @Override
    public Flux<EstadoObservacionResponse> getAllEstadoObservacion() {
        TypeReference<List<EstadoObservacionResponse>> typeRef =
                new TypeReference<>() {};

        return cacheService.getCachedCollection(
                        cacheKey,
                        estadoObservacionRepository.findAll().map(this::mapToDto),
                        typeRef
                ).doOnError(error -> log.error("Error al obtener tipos de observación neumático", error))
                .onErrorResume(e -> Flux.error(new RuntimeException(
                        "Error al obtener tipos de observación neumático", e)));
    }

    private EstadoObservacionResponse mapToDto(EstadoObservacion entity) {
        return EstadoObservacionResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}