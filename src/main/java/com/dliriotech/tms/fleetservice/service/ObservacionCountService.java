package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.ObservacionesCountResponse;
import reactor.core.publisher.Mono;

public interface ObservacionCountService {
    Mono<ObservacionesCountResponse> obtenerConteoDetalladoObservacionesPendientes(Integer equipoId);
}