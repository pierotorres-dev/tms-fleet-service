package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.EsquemaEquipoResponse;
import com.dliriotech.tms.fleetservice.entity.EsquemaEquipo;
import com.dliriotech.tms.fleetservice.exception.EquipoException;
import com.dliriotech.tms.fleetservice.repository.EsquemaEquipoRepository;
import com.dliriotech.tms.fleetservice.service.EsquemaEquipoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class EsquemaEquipoServiceImpl implements EsquemaEquipoService {

    private final EsquemaEquipoRepository esquemaEquipoRepository;

    @Override
    public Flux<EsquemaEquipoResponse> getEsquemasByTipoEquipoId(Integer tipoEquipoId) {
        return esquemaEquipoRepository.findByTipoEquipoId(tipoEquipoId)
                .map(this::mapEntityToResponse)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> log.info("Iniciando consulta de esquemas para tipo de equipo {}", tipoEquipoId))
                .doOnComplete(() -> log.info("Consulta de esquemas para tipo de equipo {} completada", tipoEquipoId))
                .doOnError(error -> log.error("Error al obtener esquemas para tipo de equipo {}: {}", 
                        tipoEquipoId, error.getMessage()))
                .onErrorResume(e -> Flux.error(new EquipoException(
                        "FLEET-ESQ-OPE-001", "Error al obtener esquemas del tipo de equipo " + tipoEquipoId)));
    }

    private EsquemaEquipoResponse mapEntityToResponse(EsquemaEquipo entity) {
        return EsquemaEquipoResponse.builder()
                .id(entity.getId())
                .nombreEsquema(entity.getNombreEsquema())
                .totalPosiciones(entity.getTotalPosiciones())
                .build();
    }
}