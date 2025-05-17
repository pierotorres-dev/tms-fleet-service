package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.EstadoEquipoResponse;
import com.dliriotech.tms.fleetservice.entity.EstadoEquipo;
import com.dliriotech.tms.fleetservice.exception.CatalogOperationException;
import com.dliriotech.tms.fleetservice.infrastructure.cache.ReactiveRedisCacheService;
import com.dliriotech.tms.fleetservice.repository.EstadoEquipoRepository;
import com.dliriotech.tms.fleetservice.service.EstadoEquipoService;
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
public class EstadoEquipoServiceImpl implements EstadoEquipoService {

    private final EstadoEquipoRepository estadoEquipoRepository;
    private final ReactiveRedisCacheService cacheService;

    @Value("${app.cache.prefixes.estado-equipo}")
    private String cacheKey;

    @Override
    public Flux<EstadoEquipoResponse> getAllEstadoEquipo() {
        TypeReference<List<EstadoEquipoResponse>> typeRef =
                new TypeReference<>() {};

        return cacheService.getCachedCollection(
                        cacheKey,
                        estadoEquipoRepository.findAll().map(this::mapToDto),
                        typeRef
                )
                .doOnError(error -> log.error("Error al obtener estado de equipo", error))
                .onErrorResume(e -> Flux.error(new CatalogOperationException("estados de equipo")));
    }

    private EstadoEquipoResponse mapToDto(EstadoEquipo entity) {
        return EstadoEquipoResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}