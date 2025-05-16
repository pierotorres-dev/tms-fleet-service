package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.TipoObservacionNeumaticoResponse;
import reactor.core.publisher.Flux;

public interface TipoObservacionNeumaticoService {
    Flux<TipoObservacionNeumaticoResponse> getAllTipoObservacionNeumatico();
}
