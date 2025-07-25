package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.EquipoRequest;
import com.dliriotech.tms.fleetservice.dto.EquipoResponse;
import com.dliriotech.tms.fleetservice.dto.EstadoEquipoResponse;
import com.dliriotech.tms.fleetservice.entity.Equipo;
import com.dliriotech.tms.fleetservice.exception.DuplicatePlacaException;
import com.dliriotech.tms.fleetservice.exception.EquipoException;
import com.dliriotech.tms.fleetservice.exception.EquipoNotFoundException;
import com.dliriotech.tms.fleetservice.repository.EquipoRepository;
import com.dliriotech.tms.fleetservice.service.EquipoService;
import com.dliriotech.tms.fleetservice.service.EstadoEquipoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final EstadoEquipoService estadoEquipoService;

    @Override
    public Flux<EquipoResponse> getAllEquiposByEmpresaId(Integer empresaId) {
        return equipoRepository.findByEmpresaId(empresaId)
                .flatMap(this::enrichEquipoWithRelations)
                .doOnSubscribe(s -> log.debug("Iniciando consulta de equipos para empresa {}", empresaId))
                .doOnComplete(() -> log.debug("Consulta de equipos para empresa {} completada", empresaId))
                .doOnError(error -> log.error("Error al obtener equipos para empresa {}: {}",
                        empresaId, error.getMessage()))
                .onErrorResume(e -> Flux.error(new EquipoException(
                        "FLEET-EQP-OPE-001", "Error al obtener equipos de la empresa " + empresaId)));
    }

    @Override
    public Mono<EquipoResponse> getEquipoById(Integer id) {
        return equipoRepository.findById(id)
                .switchIfEmpty(Mono.error(new EquipoNotFoundException(id.toString())))
                .flatMap(this::enrichEquipoWithRelations)
                .doOnSubscribe(s -> log.debug("Iniciando consulta de equipo {}", id))
                .doOnSuccess(result -> log.debug("Consulta de equipo {} completada", id))
                .doOnError(error -> log.error("Error al obtener equipo {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<EquipoResponse> saveEquipo(EquipoRequest equipoRequest) {
        Equipo entity = mapRequestToEntity(equipoRequest);

        return equipoRepository.save(entity)
                .flatMap(this::enrichEquipoWithRelations)
                .doOnSubscribe(s -> log.debug("Iniciando guardado de nuevo equipo"))
                .doOnSuccess(result -> log.debug("Equipo guardado exitosamente: {}", result.getId()))
                .doOnError(error -> log.error("Error al guardar equipo: {}", error.getMessage()))
                .onErrorResume(e -> {
                    if (e instanceof org.springframework.dao.DuplicateKeyException) {
                        return Mono.error(new DuplicatePlacaException(equipoRequest.getPlaca()));
                    }
                    return Mono.error(new EquipoException(
                            "FLEET-EQP-OPE-002", "Error al guardar equipo"));
                });
    }

    @Override
    public Mono<EquipoResponse> updateEquipo(Integer id, EquipoRequest request) {
        return equipoRepository.findById(id)
                .switchIfEmpty(Mono.error(new EquipoNotFoundException(id.toString())))
                .flatMap(existing -> {
                    updateEntityFromRequest(existing, request);
                    return equipoRepository.save(existing);
                })
                .flatMap(this::enrichEquipoWithRelations)
                .doOnSubscribe(s -> log.debug("Iniciando actualización de equipo {}", id))
                .doOnSuccess(result -> log.debug("Equipo {} actualizado exitosamente", id))
                .doOnError(error -> log.error("Error al actualizar equipo {}: {}", id, error.getMessage()))
                .onErrorResume(e -> e instanceof EquipoNotFoundException ? Mono.error(e) :
                        Mono.error(new EquipoException(
                                "FLEET-EQP-OPE-003", "Error al actualizar equipo " + id)));
    }

    @Override
    public Mono<EquipoResponse> updateEstadoEquipo(Integer id, Integer estadoId) {
        return equipoRepository.findById(id)
                .switchIfEmpty(Mono.error(new EquipoNotFoundException(id.toString())))
                .flatMap(existing -> {
                    existing.setEstadoId(estadoId);
                    return equipoRepository.save(existing);
                })
                .flatMap(this::enrichEquipoWithRelations)
                .doOnSubscribe(s -> log.debug("Iniciando actualización de estado para equipo {}", id))
                .doOnSuccess(result -> log.debug("Estado del equipo {} actualizado a {}", id, estadoId))
                .doOnError(error -> log.error("Error al actualizar estado del equipo {}: {}", id, error.getMessage()))
                .onErrorResume(e -> e instanceof EquipoNotFoundException ? Mono.error(e) :
                        Mono.error(new EquipoException(
                                "FLEET-EQP-OPE-004", "Error al actualizar estado del equipo " + id)));
    }

    private Mono<EquipoResponse> enrichEquipoWithRelations(Equipo equipo) {
        return estadoEquipoService
                .getAllEstadoEquipo()
                .filter(estado -> estado.getId().equals(equipo.getEstadoId()))
                .next()
                .switchIfEmpty(Mono.error(new EquipoException(
                        "FLEET-EQP-NF-001", "Estado de equipo no encontrado: " + equipo.getEstadoId())))
                .flatMap(estado -> Mono.fromCallable(() ->
                                mapEntityToResponse(equipo, estado))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    private Equipo mapRequestToEntity(EquipoRequest request) {
        return Equipo.builder()
                .placa(request.getPlaca())
                .negocio(request.getNegocio())
                .equipo(request.getEquipo())
                .fechaInspeccion(request.getFechaInspeccion())
                .kilometraje(request.getKilometraje())
                .estadoId(request.getEstadoId())
                .empresaId(request.getEmpresaId())
                .build();
    }

    private EquipoResponse mapEntityToResponse(Equipo entity, EstadoEquipoResponse estado) {
        return EquipoResponse.builder()
                .id(entity.getId())
                .placa(entity.getPlaca())
                .negocio(entity.getNegocio())
                .equipo(entity.getEquipo())
                .fechaInspeccion(entity.getFechaInspeccion())
                .kilometraje(entity.getKilometraje())
                .estadoEquipoResponse(estado)
                .empresaId(entity.getEmpresaId())
                .build();
    }

    private void updateEntityFromRequest(Equipo entity, EquipoRequest request) {
        if (request.getPlaca() != null) entity.setPlaca(request.getPlaca());
        if (request.getNegocio() != null) entity.setNegocio(request.getNegocio());
        if (request.getEquipo() != null) entity.setEquipo(request.getEquipo());
        if (request.getFechaInspeccion() != null) entity.setFechaInspeccion(request.getFechaInspeccion());
        if (request.getKilometraje() != null) entity.setKilometraje(request.getKilometraje());
        if (request.getEstadoId() != null) entity.setEstadoId(request.getEstadoId());
        if (request.getEmpresaId() != null) entity.setEmpresaId(request.getEmpresaId());
    }
}