package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.TipoObservacionResponse;
import reactor.core.publisher.Flux;

public interface TipoObservacionNeumaticoService {
    Flux<TipoObservacionResponse> getAllTipoObservacionNeumatico();
    Flux<TipoObservacionResponse> getAllTipoObservacionEquipo();
}