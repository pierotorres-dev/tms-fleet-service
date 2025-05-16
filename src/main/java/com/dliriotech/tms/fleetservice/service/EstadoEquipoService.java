package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.EstadoEquipoResponse;
import reactor.core.publisher.Flux;

public interface EstadoEquipoService {
    Flux<EstadoEquipoResponse> getAllEstadoEquipo();
}
