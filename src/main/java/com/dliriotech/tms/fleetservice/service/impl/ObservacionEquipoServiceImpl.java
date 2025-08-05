package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.constants.EstadoObservacionConstants;
import com.dliriotech.tms.fleetservice.dto.*;
import com.dliriotech.tms.fleetservice.entity.ObservacionEquipo;
import com.dliriotech.tms.fleetservice.exception.ObservacionCreationException;
import com.dliriotech.tms.fleetservice.exception.ObservacionEquipoException;
import com.dliriotech.tms.fleetservice.exception.ObservacionUpdateException;
import com.dliriotech.tms.fleetservice.repository.ObservacionEquipoRepository;
import com.dliriotech.tms.fleetservice.service.ObservacionEquipoService;
import com.dliriotech.tms.fleetservice.service.ObservacionMasterDataCacheService;
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
    private final ObservacionMasterDataCacheService observacionMasterDataCacheService;

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
    public Mono<ObservacionEquipoResponse> saveObservacion(ObservacionEquipoNuevoRequest request) {
        log.info("Creando nueva observación para equipo {}", request.getEquipoId());
        
        // Validaciones de entrada
        return Mono.fromCallable(() -> validateObservacionRequest(request))
                .subscribeOn(Schedulers.boundedElastic())
                .then(observacionMasterDataCacheService.getEstadoObservacionIdByNombre(EstadoObservacionConstants.PENDIENTE)
                    .onErrorMap(error -> ObservacionCreationException.estadoObservacionNotFound(EstadoObservacionConstants.PENDIENTE)))
                .flatMap(estadoPendienteId -> 
                    // Validar que el tipo de observación existe usando cache
                    observacionMasterDataCacheService.getTipoObservacion(request.getTipoObservacionId())
                        .onErrorMap(error -> ObservacionCreationException.tipoObservacionNotFound(request.getTipoObservacionId()))
                        .map(tipoObservacion -> estadoPendienteId)
                )
                .flatMap(estadoPendienteId ->
                    Mono.fromCallable(() -> buildObservacionEntity(request, estadoPendienteId))
                        .subscribeOn(Schedulers.boundedElastic())
                )
                .flatMap(observacionEntity ->
                    observacionEquipoRepository.save(observacionEntity)
                        .onErrorMap(error -> ObservacionCreationException.databaseError("guardar observación", error))
                )
                .flatMap(this::enrichObservacionWithRelations)
                .doOnSuccess(response -> log.info("Observación de equipo creada exitosamente con ID: {}", response.getId()))
                .doOnError(error -> log.error("Error al crear observación para equipo {}: {}", 
                    request.getEquipoId(), error.getMessage()));
    }

    @Override
    public Mono<ObservacionEquipoResponse> updateObservacion(Integer observacionId, ObservacionEquipoUpdateRequest request) {
        log.info("Actualizando observación con ID: {}", observacionId);
        
        // Validaciones de entrada
        return Mono.fromCallable(() -> validateUpdateRequest(observacionId, request))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(validatedRequest -> 
                    observacionEquipoRepository.findById(observacionId)
                        .switchIfEmpty(Mono.error(ObservacionUpdateException.notFound(observacionId)))
                )
                .flatMap(existingObservacion ->
                    validateBusinessRules(existingObservacion, request)
                        .then(Mono.fromCallable(() -> applyUpdates(existingObservacion, request))
                            .subscribeOn(Schedulers.boundedElastic()))
                )
                .flatMap(updatedObservacion ->
                    observacionEquipoRepository.save(updatedObservacion)
                        .onErrorMap(error -> ObservacionUpdateException.databaseError("actualizar observación", error))
                )
                .flatMap(this::enrichObservacionWithRelations)
                .doOnSuccess(response -> log.info("Observación actualizada exitosamente con ID: {}", response.getId()))
                .doOnError(error -> log.error("Error al actualizar observación con ID {}: {}", observacionId, error.getMessage()));
    }

    private Mono<ObservacionEquipoResponse> enrichObservacionWithRelations(ObservacionEquipo observacion) {
        log.debug("Enriqueciendo observación: {}", observacion.getId());
        
        // Obtener las entidades relacionadas de forma paralela usando cache
        Mono<TipoObservacionResponse> tipoObservacionMono = observacion.getTipoObservacionId() != null ?
                observacionMasterDataCacheService.getTipoObservacion(observacion.getTipoObservacionId())
                    .onErrorMap(error -> ObservacionCreationException.masterDataError("obtener tipo observacion", error))
                    .subscribeOn(Schedulers.boundedElastic()) :
                Mono.just(TipoObservacionResponse.builder().build());

        Mono<EstadoObservacionResponse> estadoObservacionMono = observacion.getEstadoId() != null ?
                observacionMasterDataCacheService.getEstadoObservacion(observacion.getEstadoId())
                    .onErrorMap(error -> ObservacionCreationException.masterDataError("obtener estado observacion", error))
                    .subscribeOn(Schedulers.boundedElastic()) :
                Mono.just(EstadoObservacionResponse.builder().build());

        // Combinar los resultados
        return Mono.zip(tipoObservacionMono, estadoObservacionMono)
                .flatMap(tuple -> 
                    Mono.fromCallable(() -> mapEntityToResponse(observacion, tuple.getT1(), tuple.getT2()))
                        .subscribeOn(Schedulers.boundedElastic())
                )
                .onErrorMap(error -> new ObservacionEquipoException(
                        "FLEET-OBS-ENR-001", 
                        "Error al enriquecer observación con ID: " + observacion.getId()
                ));
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

    private ObservacionEquipoNuevoRequest validateObservacionRequest(ObservacionEquipoNuevoRequest request) {
        if (request == null) {
            throw ObservacionCreationException.invalidRequest("request", "null");
        }
        if (request.getEquipoId() == null || request.getEquipoId() <= 0) {
            throw ObservacionCreationException.invalidRequest("equipoId", request.getEquipoId());
        }
        if (request.getTipoObservacionId() == null || request.getTipoObservacionId() <= 0) {
            throw ObservacionCreationException.invalidRequest("tipoObservacionId", request.getTipoObservacionId());
        }
        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
            throw ObservacionCreationException.invalidRequest("descripcion", request.getDescripcion());
        }
        if (request.getUsuarioId() == null || request.getUsuarioId() <= 0) {
            throw ObservacionCreationException.invalidRequest("usuarioId", request.getUsuarioId());
        }
        return request;
    }
    
    private ObservacionEquipo buildObservacionEntity(ObservacionEquipoNuevoRequest request, Integer estadoPendienteId) {
        return ObservacionEquipo.builder()
                .equipoId(request.getEquipoId())
                .fecha(LocalDateTime.now(ZoneId.of("America/Lima")))
                .tipoObservacionId(request.getTipoObservacionId())
                .descripcion(request.getDescripcion().trim())
                .estadoId(estadoPendienteId)
                .fechaResolucion(null)
                .comentarioResolucion(null)
                .usuarioResolucion(null)
                .usuarioId(request.getUsuarioId())
                .build();
    }

    private ObservacionEquipoUpdateRequest validateUpdateRequest(Integer observacionId, ObservacionEquipoUpdateRequest request) {
        if (observacionId == null || observacionId <= 0) {
            throw ObservacionUpdateException.invalidRequest("observacionId", observacionId);
        }
        if (request == null) {
            throw ObservacionUpdateException.invalidRequest("request", "null");
        }
        // Validar que al menos un campo está presente para actualizar
        if (request.getEstadoObservacionId() == null && 
            request.getUsuarioResolucionId() == null && 
            (request.getComentarioResolucion() == null || request.getComentarioResolucion().trim().isEmpty())) {
            throw ObservacionUpdateException.noFieldsToUpdate();
        }
        // Validar campos específicos si están presentes
        if (request.getEstadoObservacionId() != null && request.getEstadoObservacionId() <= 0) {
            throw ObservacionUpdateException.invalidRequest("estadoObservacionId", request.getEstadoObservacionId());
        }
        if (request.getUsuarioResolucionId() != null && request.getUsuarioResolucionId() <= 0) {
            throw ObservacionUpdateException.invalidRequest("usuarioResolucionId", request.getUsuarioResolucionId());
        }
        return request;
    }
    
    private ObservacionEquipo applyUpdates(ObservacionEquipo existing, ObservacionEquipoUpdateRequest request) {
        ObservacionEquipo.ObservacionEquipoBuilder builder = existing.toBuilder();
        
        // Si se está cambiando el estado de observación
        if (request.getEstadoObservacionId() != null) {
            builder.estadoId(request.getEstadoObservacionId())
                   .fechaResolucion(LocalDateTime.now(ZoneId.of("America/Lima")))
                   .usuarioResolucion(request.getUsuarioResolucionId());
        }
        
        // Actualizar comentario de resolución si se proporciona
        if (request.getComentarioResolucion() != null) {
            builder.comentarioResolucion(request.getComentarioResolucion().trim());
        }
        
        return builder.build();
    }
    
    private Mono<Void> validateBusinessRules(ObservacionEquipo existing, ObservacionEquipoUpdateRequest request) {
        return observacionMasterDataCacheService.getEstadoObservacion(existing.getEstadoId())
                .flatMap(currentState -> {
                    String currentStateName = currentState.getNombre();
                    
                    // Verificar si el estado actual es final (no se puede modificar)
                    if (EstadoObservacionConstants.RESUELTO.equalsIgnoreCase(currentStateName)) {
                        return Mono.error(ObservacionUpdateException.finalStateModification(currentStateName));
                    }
                    if (EstadoObservacionConstants.CANCELADO.equalsIgnoreCase(currentStateName)) {
                        return Mono.error(ObservacionUpdateException.finalStateModification(currentStateName));
                    }
                    
                    // Si se está cambiando el estado, validar la transición
                    if (request.getEstadoObservacionId() != null && 
                        !request.getEstadoObservacionId().equals(existing.getEstadoId())) {
                        
                        return observacionMasterDataCacheService.getEstadoObservacion(request.getEstadoObservacionId())
                                .flatMap(newState -> {
                                    String newStateName = newState.getNombre();
                                    
                                    // Validar transiciones permitidas desde "Pendiente"
                                    if (EstadoObservacionConstants.PENDIENTE.equalsIgnoreCase(currentStateName)) {
                                        // Desde Pendiente solo se puede ir a Resuelta o Cancelada
                                        if (EstadoObservacionConstants.RESUELTO.equalsIgnoreCase(newStateName) ||
                                            EstadoObservacionConstants.CANCELADO.equalsIgnoreCase(newStateName)) {
                                            return Mono.<Void>empty();
                                        } else {
                                            return Mono.error(ObservacionUpdateException.stateTransitionNotAllowed(currentStateName, newStateName));
                                        }
                                    }
                                    
                                    // Para cualquier otro estado, no permitir cambios
                                    return Mono.error(ObservacionUpdateException.stateTransitionNotAllowed(currentStateName, newStateName));
                                })
                                .onErrorMap(error -> {
                                    if (error instanceof ObservacionUpdateException) {
                                        return error;
                                    }
                                    return ObservacionUpdateException.masterDataError("obtener nuevo estado observacion", error);
                                });
                    }
                    
                    return Mono.<Void>empty();
                })
                .onErrorMap(error -> {
                    if (error instanceof ObservacionUpdateException) {
                        return error;
                    }
                    return ObservacionUpdateException.masterDataError("obtener estado observacion actual", error);
                });
    }
}