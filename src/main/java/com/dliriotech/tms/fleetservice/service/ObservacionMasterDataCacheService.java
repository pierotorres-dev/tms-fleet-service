package com.dliriotech.tms.fleetservice.service;

import reactor.core.publisher.Mono;

public interface ObservacionMasterDataCacheService {
    Mono<Integer> getEstadoObservacionIdByNombre(String nombre);

    Mono<Void> invalidateEstadoObservacionIdByNombreCache(String nombre);
}
