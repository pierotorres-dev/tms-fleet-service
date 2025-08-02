package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.*;
import com.dliriotech.tms.fleetservice.entity.Equipo;
import com.dliriotech.tms.fleetservice.entity.HistorialEquipoKilometraje;
import com.dliriotech.tms.fleetservice.exception.DuplicatePlacaException;
import com.dliriotech.tms.fleetservice.exception.EquipoException;
import com.dliriotech.tms.fleetservice.exception.EquipoNotFoundException;
import com.dliriotech.tms.fleetservice.repository.EquipoRepository;
import com.dliriotech.tms.fleetservice.repository.HistorialEquipoKilometrajeRepository;
import com.dliriotech.tms.fleetservice.service.EquipoEntityCacheService;
import com.dliriotech.tms.fleetservice.service.EquipoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final EquipoEntityCacheService equipoEntityCacheService;
    private final HistorialEquipoKilometrajeRepository historialEquipoKilometrajeRepository;

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
    public Mono<EquipoResponse> saveEquipo(EquipoNuevoRequest equipoNuevoRequest) {
        Equipo entity = mapRequestToEntity(equipoNuevoRequest);

        return equipoRepository.save(entity)
                .flatMap(this::enrichEquipoWithRelations)
                .doOnSubscribe(s -> log.debug("Iniciando guardado de nuevo equipo"))
                .doOnSuccess(result -> log.debug("Equipo guardado exitosamente: {}", result.getId()))
                .doOnError(error -> log.error("Error al guardar equipo: {}", error.getMessage()))
                .onErrorResume(e -> {
                    if (e instanceof org.springframework.dao.DuplicateKeyException) {
                        return Mono.error(new DuplicatePlacaException(equipoNuevoRequest.getPlaca()));
                    }
                    return Mono.error(new EquipoException(
                            "FLEET-EQP-OPE-002", "Error al guardar equipo"));
                });
    }

    @Override
    public Mono<EquipoResponse> updateEquipo(Integer id, EquipoUpdateRequest request) {
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
                .onErrorResume(e -> {
                    if (e instanceof org.springframework.dao.DuplicateKeyException) {
                        // Extract placa from the request or existing entity
                        String placa = request.getPlaca();
                        return Mono.error(new DuplicatePlacaException(placa));
                    }
                    return e instanceof EquipoNotFoundException ? Mono.error(e)
                            : Mono.error(new EquipoException(
                            "FLEET-EQP-OPE-003", "Error al actualizar equipo " + id));
                });
    }

    @Override
    public Mono<EquipoResponse> updateEquipoKilometraje(Integer id, EquipoUpdateKilometrajeRequest request) {
        return equipoRepository.findById(id)
                .switchIfEmpty(Mono.error(new EquipoNotFoundException(id.toString())))
                .flatMap(equipo -> {
                    // Create history record
                    HistorialEquipoKilometraje historial = HistorialEquipoKilometraje.builder()
                            .equipoId(id)
                            .kilometrajeAnterior(equipo.getKilometraje())
                            .kilometrajeNuevo(request.getKilometrajeNuevo())
                            .fechaActualizacion(LocalDateTime.now(ZoneId.of("America/Lima"))) // Timestamp for history record
                            .usuarioId(request.getUsuarioId())
                            .build();

                    // Update equipo
                    equipo.setKilometraje(request.getKilometrajeNuevo());
                    equipo.setFechaActualizacionKilometraje(LocalDate.now(ZoneId.of("America/Lima"))); // Current date for equipment

                    // Save history record first, then update equipment
                    return historialEquipoKilometrajeRepository.save(historial)
                            .then(equipoRepository.save(equipo));
                })
                .flatMap(this::enrichEquipoWithRelations)
                .doOnSubscribe(s -> log.debug("Iniciando actualización de kilometraje para equipo {}", id))
                .doOnSuccess(result -> log.debug("Kilometraje del equipo {} actualizado exitosamente", id))
                .doOnError(error -> log.error("Error al actualizar kilometraje del equipo {}: {}", id, error.getMessage()))
                .onErrorResume(e -> e instanceof EquipoNotFoundException ? Mono.error(e)
                        : Mono.error(new EquipoException(
                        "FLEET-EQP-OPE-004", "Error al actualizar kilometraje del equipo " + id)));
    }

    private Mono<EquipoResponse> enrichEquipoWithRelations(Equipo equipo) {
        // Usar el servicio de cache para obtener las entidades relacionadas
        Mono<EstadoEquipoResponse> estadoMono = equipoEntityCacheService
                .getEstadoEquipo(equipo.getEstadoId());

        Mono<TipoEquipoResponse> tipoMono = equipoEntityCacheService
                .getTipoEquipo(equipo.getTipoEquipoId());

        Mono<EsquemaEquipoResponse> esquemaMono = equipoEntityCacheService
                .getEsquemaEquipo(equipo.getEsquemaEquipoId());

        // Ejecutar las consultas en paralelo para optimizar el rendimiento
        return Mono.zip(
                estadoMono.subscribeOn(Schedulers.boundedElastic()),
                tipoMono.subscribeOn(Schedulers.boundedElastic()),
                esquemaMono.subscribeOn(Schedulers.boundedElastic())
        ).flatMap(tuple -> Mono
                .fromCallable(() -> mapEntityToResponse(equipo, tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .subscribeOn(Schedulers.boundedElastic()));
    }

    private Equipo mapRequestToEntity(EquipoNuevoRequest request) {
        return Equipo.builder()
                .placa(request.getPlaca())
                .negocio(request.getNegocio())
                .tipoEquipoId(request.getTipoEquipoId())
                .esquemaEquipoId(request.getEsquemaEquipoId())
                .estadoId(request.getEstadoId())
                .empresaId(request.getEmpresaId())
                .build();
    }

    private EquipoResponse mapEntityToResponse(
            Equipo entity,
            EstadoEquipoResponse estado,
            TipoEquipoResponse tipoEquipo,
            EsquemaEquipoResponse esquemaEquipo) {
        return EquipoResponse.builder()
                .id(entity.getId())
                .placa(entity.getPlaca())
                .negocio(entity.getNegocio())
                .tipoEquipoResponse(tipoEquipo)
                .esquemaEquipoResponse(esquemaEquipo)
                .fechaInspeccion(entity.getFechaInspeccion())
                .kilometraje(entity.getKilometraje())
                .fechaActualizacionKilometraje(entity.getFechaActualizacionKilometraje())
                .estadoEquipoResponse(estado)
                .empresaId(entity.getEmpresaId())
                .build();
    }

    private void updateEntityFromRequest(Equipo entity, EquipoUpdateRequest request) {
        if (request.getPlaca() != null)
            entity.setPlaca(request.getPlaca());
        if (request.getNegocio() != null)
            entity.setNegocio(request.getNegocio());
        if (request.getTipoEquipoId() != null)
            entity.setTipoEquipoId(request.getTipoEquipoId());
        if (request.getEsquemaEquipoId() != null)
            entity.setEsquemaEquipoId(request.getEsquemaEquipoId());
        if (request.getEstadoId() != null)
            entity.setEstadoId(request.getEstadoId());
    }
}