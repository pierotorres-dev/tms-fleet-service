package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.EsquemaEquipoResponse;
import reactor.core.publisher.Flux;

public interface EsquemaEquipoService {
    Flux<EsquemaEquipoResponse> getEsquemasByTipoEquipoId(Integer tipoEquipoId);
}