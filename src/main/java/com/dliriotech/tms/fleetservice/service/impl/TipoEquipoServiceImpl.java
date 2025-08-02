package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.TipoEquipoResponse;
import com.dliriotech.tms.fleetservice.entity.Equipo;
import com.dliriotech.tms.fleetservice.entity.TipoEquipo;
import com.dliriotech.tms.fleetservice.exception.EquipoException;
import com.dliriotech.tms.fleetservice.repository.EquipoRepository;
import com.dliriotech.tms.fleetservice.repository.TipoEquipoRepository;
import com.dliriotech.tms.fleetservice.service.TipoEquipoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class TipoEquipoServiceImpl implements TipoEquipoService {

    private final EquipoRepository equipoRepository;
    private final TipoEquipoRepository tipoEquipoRepository;

    @Override
    public Flux<TipoEquipoResponse> getAllTiposEquipo() {
        return tipoEquipoRepository.findAll()
                .map(this::mapEntityToResponse)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> log.debug("Iniciando consulta de todos los tipos de equipo"))
                .doOnComplete(() -> log.debug("Consulta de todos los tipos de equipo completada"))
                .doOnError(error -> log.error("Error al obtener tipos de equipo: {}", error.getMessage()))
                .onErrorResume(e -> Flux.error(new EquipoException(
                        "FLEET-TEQ-OPE-002", "Error al obtener tipos de equipo")));
    }

    @Override
    public Flux<TipoEquipoResponse> getTiposEquipoActivosByEmpresaId(Integer empresaId) {
        return equipoRepository.findByEmpresaId(empresaId)
                .map(Equipo::getTipoEquipoId)
                .distinct()
                .flatMap(tipoEquipoRepository::findById)
                .map(this::mapEntityToResponse)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> log.debug("Iniciando consulta de tipos de equipo activos para empresa {}", empresaId))
                .doOnComplete(() -> log.debug("Consulta de tipos de equipo activos para empresa {} completada", empresaId))
                .doOnError(error -> log.error("Error al obtener tipos de equipo activos para empresa {}: {}", 
                        empresaId, error.getMessage()))
                .onErrorResume(e -> Flux.error(new EquipoException(
                        "FLEET-TEQ-OPE-001", "Error al obtener tipos de equipo activos de la empresa " + empresaId)));
    }

    private TipoEquipoResponse mapEntityToResponse(TipoEquipo entity) {
        return TipoEquipoResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}