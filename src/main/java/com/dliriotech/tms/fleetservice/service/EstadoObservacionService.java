package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.EstadoObservacionResponse;
import reactor.core.publisher.Flux;

public interface EstadoObservacionService {
    Flux<EstadoObservacionResponse> getAllEstadoObservacion();
}