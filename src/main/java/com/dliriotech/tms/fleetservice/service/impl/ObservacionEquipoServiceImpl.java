package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.EstadoObservacionResponse;
import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoRequest;
import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoResponse;
import com.dliriotech.tms.fleetservice.dto.TipoObservacionResponse;
import com.dliriotech.tms.fleetservice.entity.ObservacionEquipo;
import com.dliriotech.tms.fleetservice.exception.ObservacionEquipoException;
import com.dliriotech.tms.fleetservice.exception.ResourceNotFoundException;
import com.dliriotech.tms.fleetservice.repository.ObservacionEquipoRepository;
import com.dliriotech.tms.fleetservice.service.EstadoObservacionService;
import com.dliriotech.tms.fleetservice.service.ObservacionEquipoService;
import com.dliriotech.tms.fleetservice.service.TipoObservacionNeumaticoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObservacionEquipoServiceImpl implements ObservacionEquipoService {

    private final ObservacionEquipoRepository observacionEquipoRepository;
    private final TipoObservacionNeumaticoService tipoObservacionNeumaticoService;
    private final EstadoObservacionService estadoObservacionService;

    @Override
    public Flux<ObservacionEquipoResponse> getAllObservacionesByEquipoId(Integer equipoId) {
        return observacionEquipoRepository.findByEquipoId(equipoId)
                .flatMap(this::enrichObservacionWithRelations)
                .doOnSubscribe(s -> log.info("Iniciando consulta a la lista de observaciones del equipo {}", equipoId))
                .doOnComplete(() -> log.info("Consulta a la lista de observaciones del equipo {} completada", equipoId))
                .doOnError(error -> log.error("Error al obtener observaciones para el equipo {}: {}", equipoId, error.getMessage()))
                .onErrorResume(e -> Flux.error(new ObservacionEquipoException(
                        "FLEET-OBS-OPE-001", "Error al obtener observaciones del equipo " + equipoId)));
    }

    @Override
    public Mono<ObservacionEquipoResponse> saveObservacion(ObservacionEquipoRequest request) {
        ObservacionEquipo entity = mapRequestToEntity(request);
        entity.setFecha(entity.getFecha() != null ? entity.getFecha() : LocalDateTime.now(ZoneId.of("America/Lima")));

        return observacionEquipoRepository.save(entity)
                .flatMap(this::enrichObservacionWithRelations)
                .doOnSubscribe(s -> log.info("Iniciando guardado de nueva observación para el equipo {}", entity.getEquipoId()))
                .doOnSuccess(result -> log.info("Observación de equipo guardada exitosamente: {}", result.getId()))
                .doOnError(error -> log.error("Error al guardar observación de equipo: {}", error.getMessage()))
                .onErrorResume(e -> Mono.error(new ObservacionEquipoException(
                        "FLEET-OBS-OPE-002", "Error al guardar observación de equipo")));
    }

    @Override
    public Mono<ObservacionEquipoResponse> updateObservacion(Integer id, ObservacionEquipoRequest request) {
        return observacionEquipoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Observación de equipo", id.toString())))
                .flatMap(existing -> {
                    updateEntityFromRequest(existing, request);
                    return observacionEquipoRepository.save(existing);
                })
                .flatMap(this::enrichObservacionWithRelations)
                .doOnSubscribe(s -> log.info("Iniciando actualización de observación {}", id))
                .doOnSuccess(result -> log.info("Observación {} actualizada exitosamente", id))
                .doOnError(error -> log.error("Error al actualizar observación {}: {}", id, error.getMessage()))
                .onErrorResume(e -> e instanceof ResourceNotFoundException ? Mono.error(e) :
                        Mono.error(new ObservacionEquipoException(
                                "FLEET-OBS-OPE-004", "Error al actualizar observación " + id)));
    }

    public Mono<Integer> updateEstadoObservacionesByEquipoId(Integer equipoId, Integer nuevoEstadoId) {
        return observacionEquipoRepository.updateEstadoByEquipoId(equipoId, nuevoEstadoId)
                .doOnSubscribe(s -> log.info("Iniciando actualización masiva para equipo {}", equipoId))
                .doOnSuccess(count -> log.info("Actualizadas {} observaciones para el equipo {}", count, equipoId))
                .doOnError(error -> log.error("Error al actualizar estados de observaciones para equipo {}: {}",
                        equipoId, error.getMessage()))
                .onErrorResume(e -> Mono.error(new ObservacionEquipoException(
                        "FLEET-OBS-OPE-003", "Error al actualizar estado de observaciones para equipo " + equipoId)));
    }

    private Mono<ObservacionEquipoResponse> enrichObservacionWithRelations(ObservacionEquipo observacion) {
        Mono<TipoObservacionResponse> tipoMono = tipoObservacionNeumaticoService
                .getAllTipoObservacionNeumatico()
                .filter(tipo -> tipo.getId().equals(observacion.getTipoObservacionId()))
                .next()
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Tipo de observación",
                        observacion.getTipoObservacionId().toString())));

        Mono<EstadoObservacionResponse> estadoMono = estadoObservacionService
                .getAllEstadoObservacion()
                .filter(estado -> estado.getId().equals(observacion.getEstadoId()))
                .next()
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estado de observación",
                        observacion.getEstadoId().toString())));

        return Mono.zip(tipoMono, estadoMono)
                .flatMap(tuple -> Mono.fromCallable(() ->
                                mapEntityToResponse(observacion, tuple.getT1(), tuple.getT2()))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    private ObservacionEquipo mapRequestToEntity(ObservacionEquipoRequest request) {
        return ObservacionEquipo.builder()
                .equipoId(request.getEquipoId())
                .fecha(request.getFecha())
                .tipoObservacionId(request.getTipoObservacionId())
                .descripcion(request.getDescripcion())
                .estadoId(request.getEstadoId())
                .fechaResolucion(request.getFechaResolucion())
                .comentarioResolucion(request.getComentarioResolucion())
                .usuarioResolucion(request.getUsuarioResolucion())
                .usuarioId(request.getUsuarioId())
                .build();
    }

    private ObservacionEquipoResponse mapEntityToResponse(
            ObservacionEquipo entity,
            TipoObservacionResponse tipo,
            EstadoObservacionResponse estado) {

        return ObservacionEquipoResponse.builder()
                .id(entity.getId())
                .equipoId(entity.getEquipoId())
                .fecha(entity.getFecha())
                .tipoObservacionResponse(tipo)
                .descripcion(entity.getDescripcion())
                .estadoObservacionResponse(estado)
                .fechaResolucion(entity.getFechaResolucion())
                .comentarioResolucion(entity.getComentarioResolucion())
                .usuarioResolucion(entity.getUsuarioResolucion())
                .usuarioId(entity.getUsuarioId())
                .build();
    }

    private void updateEntityFromRequest(ObservacionEquipo entity, ObservacionEquipoRequest request) {
        if (request.getEquipoId() != null) entity.setEquipoId(request.getEquipoId());
        if (request.getFecha() != null) entity.setFecha(request.getFecha());
        if (request.getTipoObservacionId() != null) entity.setTipoObservacionId(request.getTipoObservacionId());
        if (request.getDescripcion() != null) entity.setDescripcion(request.getDescripcion());
        if (request.getEstadoId() != null) entity.setEstadoId(request.getEstadoId());
        if (request.getFechaResolucion() != null) entity.setFechaResolucion(request.getFechaResolucion());
        if (request.getComentarioResolucion() != null) entity.setComentarioResolucion(request.getComentarioResolucion());
        if (request.getUsuarioResolucion() != null) entity.setUsuarioResolucion(request.getUsuarioResolucion());
        if (request.getUsuarioId() != null) entity.setUsuarioId(request.getUsuarioId());
    }
}