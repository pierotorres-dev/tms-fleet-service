package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.TipoEquipoResponse;
import reactor.core.publisher.Flux;

public interface TipoEquipoService {
    Flux<TipoEquipoResponse> getTiposEquipoActivosByEmpresaId(Integer empresaId);
}