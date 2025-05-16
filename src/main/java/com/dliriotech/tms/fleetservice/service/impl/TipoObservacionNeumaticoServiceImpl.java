package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.TipoObservacionNeumaticoResponse;
import com.dliriotech.tms.fleetservice.entity.TipoObservacionNeumatico;
import com.dliriotech.tms.fleetservice.repository.TipoObservacionNeumaticoRepository;
import com.dliriotech.tms.fleetservice.service.TipoObservacionNeumaticoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class TipoObservacionNeumaticoServiceImpl implements TipoObservacionNeumaticoService {

    private final TipoObservacionNeumaticoRepository tipoObservacionNeumaticoRepository;

    @Override
    public Flux<TipoObservacionNeumaticoResponse> getAllTipoObservacionNeumatico() {
        return tipoObservacionNeumaticoRepository.findAll()
                .map(this::mapToDto)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> log.info("Iniciando consulta de tipos de observación neumático"))
                .doOnComplete(() -> log.info("Consulta de tipos de observación neumático completada"))
                .doOnError(error -> log.error("Error al obtener tipos de observación neumático", error))
                .onErrorResume(throwable -> {
                    if (throwable instanceof RuntimeException) {
                        return Flux.error(throwable);
                    }
                    return Flux.error(new RuntimeException("Error al obtener tipos de observación neumático"));
                    //TODO: Manejar el error de manera adecuada
                })
                .switchIfEmpty(Flux.defer(() -> {
                    log.warn("No se encontraron tipos de observación neumático");
                    return Flux.empty();
                }));
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