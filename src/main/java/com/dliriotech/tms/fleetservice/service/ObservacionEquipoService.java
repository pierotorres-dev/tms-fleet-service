package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoRequest;
import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ObservacionEquipoService {
    Flux<ObservacionEquipoResponse> getAllObservacionesByEquipoId(Integer equipoId);
    Mono<ObservacionEquipoResponse> saveObservacion(ObservacionEquipoRequest observacionEquipoRequest);
    Mono<ObservacionEquipoResponse> updateObservacion(Integer id, ObservacionEquipoRequest request);
    Mono<Integer> updateEstadoObservacionesByEquipoId(Integer equipoId, Integer nuevoEstadoId);
}