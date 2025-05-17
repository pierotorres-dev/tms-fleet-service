package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.TipoObservacionNeumaticoResponse;
import com.dliriotech.tms.fleetservice.entity.TipoObservacionNeumatico;
import com.dliriotech.tms.fleetservice.exception.CatalogOperationException;
import com.dliriotech.tms.fleetservice.infrastructure.cache.ReactiveRedisCacheService;
import com.dliriotech.tms.fleetservice.repository.TipoObservacionNeumaticoRepository;
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

    private final TipoObservacionNeumaticoRepository tipoObservacionNeumaticoRepository;
    private final ReactiveRedisCacheService cacheService;

    @Value("${app.cache.prefixes.tipo-observacion-neumatico}")
    private String cacheKey;

    @Override
    public Flux<TipoObservacionNeumaticoResponse> getAllTipoObservacionNeumatico() {
        TypeReference<List<TipoObservacionNeumaticoResponse>> typeRef =
                new TypeReference<>() {};

        return cacheService.getCachedCollection(
                        cacheKey,
                        tipoObservacionNeumaticoRepository.findAll().map(this::mapToDto),
                        typeRef
                )
                .doOnError(error -> log.error("Error al obtener tipos de observación neumático", error))
                .onErrorResume(e -> Flux.error(new CatalogOperationException("tipos de observacion de neumático")));
    }

    private TipoObservacionNeumaticoResponse mapToDto(TipoObservacionNeumatico entity) {
        return TipoObservacionNeumaticoResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .activo(entity.getActivo())
                .build();
    }
}