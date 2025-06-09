package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.EquipoRequest;
import com.dliriotech.tms.fleetservice.dto.EquipoResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EquipoService {
    Flux<EquipoResponse> getAllEquiposByEmpresaId(Integer empresaId);
    Mono<EquipoResponse> getEquipoById(Integer id);
    Mono<EquipoResponse> saveEquipo(EquipoRequest equipoRequest);
    Mono<EquipoResponse> updateEquipo(Integer id, EquipoRequest request);
    Mono<EquipoResponse> updateEstadoEquipo(Integer id, Integer estadoId);
}