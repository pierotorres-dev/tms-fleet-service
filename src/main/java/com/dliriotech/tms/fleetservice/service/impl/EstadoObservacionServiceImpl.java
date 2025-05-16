package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.EstadoObservacionResponse;
import com.dliriotech.tms.fleetservice.entity.EstadoObservacion;
import com.dliriotech.tms.fleetservice.repository.EstadoObservacionRepository;
import com.dliriotech.tms.fleetservice.service.EstadoObservacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstadoObservacionServiceImpl implements EstadoObservacionService {

    private final EstadoObservacionRepository estadoObservacionRepository;

    @Override
    public Flux<EstadoObservacionResponse> getAllEstadoObservacion() {
        return estadoObservacionRepository.findAll()
                .map(this::mapToDto)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> log.info("Iniciando consulta de estados de observación"))
                .doOnComplete(() -> log.info("Consulta de estados de observación completada"))
                .doOnError(error -> log.error("Error al obtener estados de observación", error))
                .onErrorResume(throwable -> {
                    if (throwable instanceof RuntimeException) {
                        return Flux.error(throwable);
                    }
                    return Flux.error(new RuntimeException("Error al obtener estados de observación"));
                    //TODO: Manejar el error de manera adecuada
                })
                .switchIfEmpty(Flux.defer(() -> {
                    log.warn("No se encontraron estados de observación");
                    return Flux.empty();
                }));
    }

    private EstadoObservacionResponse mapToDto(EstadoObservacion entity) {
        return EstadoObservacionResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
