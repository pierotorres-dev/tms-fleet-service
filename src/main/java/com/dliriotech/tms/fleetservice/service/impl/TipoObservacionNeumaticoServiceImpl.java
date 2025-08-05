package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.TipoObservacionResponse;
import com.dliriotech.tms.fleetservice.entity.TipoObservacion;
import com.dliriotech.tms.fleetservice.exception.CatalogOperationException;
import com.dliriotech.tms.fleetservice.infrastructure.cache.ReactiveRedisCacheService;
import com.dliriotech.tms.fleetservice.repository.TipoObservacionRepository;
import com.dliriotech.tms.fleetservice.service.TipoObservacionNeumaticoService;
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
public class TipoObservacionNeumaticoServiceImpl implements TipoObservacionNeumaticoService {

    private final TipoObservacionRepository tipoObservacionRepository;
    private final ReactiveRedisCacheService cacheService;

    @Value("${app.cache.prefixes.tipo-observacion-neumatico}")
    private String cacheKeyPrefix;

    @Override
    public Flux<TipoObservacionResponse> getAllTipoObservacionNeumatico() {
        TypeReference<List<TipoObservacionResponse>> typeRef = new TypeReference<>() {};
        String cacheKey = cacheKeyPrefix + ":NEUMATICO";

        return cacheService.getCachedCollection(
                        cacheKey,
                        tipoObservacionRepository.findAllByAmbito("NEUMATICO").map(this::mapToDto),
                        typeRef
                )
                .doOnError(error -> log.error("Error al obtener tipos de observación neumático", error))
                .onErrorResume(e -> Flux.error(new CatalogOperationException("tipos de observacion de neumático")));
    }

    @Override
    public Flux<TipoObservacionResponse> getAllTipoObservacionEquipo() {
        TypeReference<List<TipoObservacionResponse>> typeRef = new TypeReference<>() {};
        String cacheKey = cacheKeyPrefix + ":EQUIPO";

        return cacheService.getCachedCollection(
                        cacheKey,
                        tipoObservacionRepository.findAllByAmbito("EQUIPO").map(this::mapToDto),
                        typeRef
                )
                .doOnError(error -> log.error("Error al obtener tipos de observación equipo", error))
                .onErrorResume(e -> Flux.error(new CatalogOperationException("tipos de observacion de equipo")));
    }

    private TipoObservacionResponse mapToDto(TipoObservacion entity) {
        return TipoObservacionResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .ambito(entity.getAmbito())
                .descripcion(entity.getDescripcion())
                .activo(entity.getActivo())
                .build();
    }
}