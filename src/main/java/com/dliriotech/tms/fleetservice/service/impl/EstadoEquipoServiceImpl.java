package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.EstadoEquipoResponse;
import com.dliriotech.tms.fleetservice.entity.EstadoEquipo;
import com.dliriotech.tms.fleetservice.repository.EstadoEquipoRepository;
import com.dliriotech.tms.fleetservice.service.EstadoEquipoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstadoEquipoServiceImpl implements EstadoEquipoService {

    private final EstadoEquipoRepository estadoEquipoRepository;

    @Override
    public Flux<EstadoEquipoResponse> getAllEstadoEquipo() {
        return estadoEquipoRepository.findAll()
                .map(this::mapToDto)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> log.info("Iniciando consulta de estados de equipo"))
                .doOnComplete(() -> log.info("Consulta de estados de equipo completada"))
                .doOnError(error -> log.error("Error al obtener estados de equipo", error))
                .onErrorResume(throwable -> {
                    if (throwable instanceof RuntimeException) {
                        return Flux.error(throwable);
                    }
                    return Flux.error(new RuntimeException("Error al obtener estados de equipo"));
                    //TODO: Manejar el error de manera adecuada
                })
                .switchIfEmpty(Flux.defer(() -> {
                    log.warn("No se encontraron estados de equipo");
                    return Flux.empty();
                }));
    }

    private EstadoEquipoResponse mapToDto(EstadoEquipo entity) {
        return EstadoEquipoResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
