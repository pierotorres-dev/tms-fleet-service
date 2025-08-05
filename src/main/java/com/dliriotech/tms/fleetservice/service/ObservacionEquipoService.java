package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoNuevoRequest;
import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoResponse;
import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ObservacionEquipoService {
    Flux<ObservacionEquipoResponse> getAllObservacionesByEquipoId(Integer equipoId);
    Flux<ObservacionEquipoResponse> getAllObservacionesPendientesAndByEquipoId(Integer equipoId);
    Mono<ObservacionEquipoResponse> saveObservacion(ObservacionEquipoNuevoRequest observacionEquipoNuevoRequest);
    Mono<ObservacionEquipoResponse> updateObservacion(Integer observacionId, ObservacionEquipoUpdateRequest request);
    Mono<Integer> updateEstadoObservacionesByEquipoId(Integer equipoId, Integer nuevoEstadoId);
}